/* 
 * Copyright (C) 2024 Linck. <zerolinck@foxmail.com>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.zerolinck.passiflora.storage.service;

import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import jakarta.servlet.http.HttpServletResponse;

import com.zerolinck.passiflora.base.constant.Header;
import com.zerolinck.passiflora.common.api.Page;
import com.zerolinck.passiflora.common.config.PassifloraProperties;
import com.zerolinck.passiflora.common.exception.BizException;
import com.zerolinck.passiflora.common.util.Condition;
import com.zerolinck.passiflora.common.util.NetUtils;
import com.zerolinck.passiflora.common.util.lock.LockUtils;
import com.zerolinck.passiflora.common.util.lock.LockWrapper;
import com.zerolinck.passiflora.model.storage.entity.StorageFile;
import com.zerolinck.passiflora.model.storage.enums.FileStatusEnum;
import com.zerolinck.passiflora.storage.mapper.StorageFileMapper;
import com.zerolinck.passiflora.storage.util.OssS3Util;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * 通用文件 Service
 *
 * @author linck
 * @since 2024-05-17
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StorageFileService {
    private final StorageFileMapper mapper;
    private final PassifloraProperties passifloraProperties;

    private static final String LOCK_KEY = "passiflora:lock:storageFile:";
    private static final URLCodec urlCodec = new URLCodec();

    /**
     * 分页查询
     *
     * @param condition 搜索条件
     * @return 文件的分页结果
     * @since 2024-05-17
     */
    @NotNull public Page<StorageFile> page(@Nullable Condition<StorageFile> condition) {
        condition = Objects.requireNonNullElse(condition, new Condition<>());
        return mapper.page(condition);
    }

    /**
     * 根据文件ID集合查询文件列表
     *
     * @param fileIds 文件ID集合
     * @return 文件列表
     * @since 2024-05-17
     */
    public List<StorageFile> listByFileIds(List<String> fileIds) {
        return mapper.selectListByIds(fileIds);
    }

    /**
     * 尝试文件秒传
     *
     * @param storageFile 需要参数 originalFileName，contentType, fileMd5
     * @return 空字符串表示无法秒传，应再次调用文件上传接口；有值字符串表示上传成功，上传文件ID
     * @since 2024-05-17
     */
    @NotNull public String tryQuicklyUpload(@NotNull StorageFile storageFile) {
        return LockUtils.lock(
                LOCK_KEY,
                new LockWrapper<StorageFile>().lock(StorageFile::getFileMd5, storageFile.getFileMd5()),
                true,
                () -> {
                    List<StorageFile> storageFiles = listByFileMd5(storageFile.getFileMd5());
                    if (CollectionUtils.isEmpty(storageFiles)) {
                        return "";
                    }
                    StorageFile dbFile = storageFiles.getFirst();
                    dbFile.setFileId(null);
                    // TODO 文件作用
                    dbFile.setFilePurpose("");
                    dbFile.setDownloadCount(0L);
                    dbFile.setLastDownloadTime(null);
                    dbFile.setFileStatus(FileStatusEnum.TEMP);
                    dbFile.setOriginalFileName(storageFile.getOriginalFileName());
                    dbFile.setContentType(storageFile.getContentType());
                    mapper.insert(dbFile);
                    return dbFile.getFileId();
                });
    }

    /**
     * 上传文件
     *
     * @param file 文件
     * @param fileName 文件名
     * @return 上传文件ID
     * @since 2024-05-17
     */
    @NotNull @SneakyThrows
    public String upload(@NotNull MultipartFile file, @Nullable String fileName) {
        String md5Hex = DigestUtils.md5Hex(file.getBytes());
        return LockUtils.lock(
                LOCK_KEY, new LockWrapper<StorageFile>().lock(StorageFile::getFileMd5, md5Hex), true, () -> {
                    String bucket = passifloraProperties.getStorage().getBucketName();
                    StorageFile storageFile = new StorageFile();
                    storageFile.setOriginalFileName(file.getOriginalFilename());
                    if (StringUtils.isNotBlank(fileName)) {
                        storageFile.setOriginalFileName(fileName);
                    }
                    storageFile.setContentType(file.getContentType());
                    storageFile.setFileMd5(md5Hex);
                    // TODO 文件作用
                    storageFile.setFilePurpose("");
                    storageFile.setFileSize(file.getSize());
                    storageFile.setFileStatus(FileStatusEnum.TEMP);
                    storageFile.setDownloadCount(0L);
                    String extName = FilenameUtils.getExtension(storageFile.getOriginalFileName());
                    String objectName = md5Hex + "." + extName;
                    storageFile.setBucketName(bucket);
                    storageFile.setObjectName(objectName);
                    String tryQuicklyUploadResult = tryQuicklyUpload(storageFile);
                    if (StringUtils.isNotBlank(tryQuicklyUploadResult)) {
                        return tryQuicklyUploadResult;
                    }

                    // 检查文件是否已经上传
                    List<StorageFile> storageFiles = mapper.listByFileMd5(md5Hex);
                    boolean exist = false;
                    if (!storageFiles.isEmpty()) {
                        StorageFile dbStorageFile = storageFiles.getFirst();
                        objectName = dbStorageFile.getObjectName();
                        bucket = dbStorageFile.getBucketName();
                        // 检查 oss 文件是否存在
                        try {
                            exist = OssS3Util.doesFileExist(
                                    dbStorageFile.getBucketName(), dbStorageFile.getObjectName());
                            log.info("文件已存在，秒传，fileMd5: {}", md5Hex);
                        } catch (Exception e) {
                            // 可能查询文件不存在，尝试重新覆盖文件
                            log.warn(
                                    "oss stat 文件错误 objectName: {}, exception: {}",
                                    dbStorageFile.getObjectName(),
                                    e.getMessage());
                        }
                    }

                    if (!exist) {
                        try {
                            OssS3Util.uploadFile(bucket, objectName, file);
                        } catch (Exception e) {
                            throw new BizException(e);
                        }
                    }
                    mapper.insert(storageFile);
                    return storageFile.getFileId();
                });
    }

    /**
     * 根据文件MD5查询文件列表
     *
     * @param fileMd5 文件MD5
     * @return 文件列表
     * @since 2024-05-17
     */
    @NotNull public List<StorageFile> listByFileMd5(@NotNull String fileMd5) {
        return mapper.listByFileMd5(fileMd5);
    }

    /**
     * 根据文件ID集合删除文件
     *
     * @param fileIds 文件ID集合
     * @since 2024-05-17
     */
    public void deleteByIds(@NotNull Collection<String> fileIds) {
        for (String fileId : fileIds) {
            deleteById(fileId);
        }
    }

    /**
     * 根据文件ID删除文件
     *
     * @param fileId 文件ID
     * @since 2024-05-17
     */
    @SneakyThrows
    public void deleteById(@NotNull String fileId) {
        if (StringUtils.isBlank(fileId)) {
            return;
        }
        StorageFile storageFile = mapper.selectOneById(fileId);
        if (storageFile == null) {
            return;
        }
        LockUtils.lock(
                LOCK_KEY,
                new LockWrapper<StorageFile>().lock(StorageFile::getFileMd5, storageFile.getFileMd5()),
                true,
                () -> {
                    mapper.deleteBatchByIds(List.of(fileId), 500);
                    long md5Count = mapper.countByFileMd5(storageFile.getFileMd5());
                    if (md5Count == 0) {
                        OssS3Util.deleteFile(storageFile.getBucketName(), storageFile.getObjectName());
                    }
                });
    }

    /**
     * 查询文件详情
     *
     * @param fileId 文件ID
     * @return 包含文件的Optional对象
     * @since 2024-05-17
     */
    @NotNull public Optional<StorageFile> detail(@NotNull String fileId) {
        return Optional.ofNullable(mapper.selectOneById(fileId));
    }

    /**
     * 下载文件
     *
     * @param fileId 文件ID
     * @since 2024-05-17
     */
    @SneakyThrows
    public void downloadFile(@NotNull String fileId) {
        StorageFile storageFile = mapper.selectOneById(fileId);
        if (storageFile == null) {
            throw new NoSuchElementException("文件不存在");
        }
        HttpServletResponse response = NetUtils.getResponse();
        if (MimeTypeUtils.APPLICATION_JSON.toString().equals(storageFile.getContentType())) {
            response.setContentType(MimeTypeUtils.TEXT_PLAIN.toString());
        } else {
            response.setContentType(storageFile.getContentType());
        }
        response.setHeader(
                Header.CONTENT_DISPOSITION.getValue(),
                "attachment; filename=" + urlCodec.encode(storageFile.getOriginalFileName()));
        OssS3Util.downloadFile(storageFile.getBucketName(), storageFile.getObjectName(), response.getOutputStream());
        mapper.incrDownCount(fileId);
    }

    /**
     * 下载文件压缩包
     *
     * @param fileIds 文件ID集合
     * @since 2024-05-17
     */
    @SneakyThrows
    public void downloadZip(@NotNull List<String> fileIds) {
        NetUtils.getResponse()
                .setHeader(
                        Header.CONTENT_DISPOSITION.getValue(), "attachment; filename=" + urlCodec.encode("文件压缩包.zip"));
        ZipOutputStream zipOut = new ZipOutputStream(NetUtils.getResponse().getOutputStream());
        Map<String, Integer> fileNameCountMap = new HashMap<>();
        for (String fileId : fileIds) {
            StorageFile storageFile = mapper.selectOneById(fileId);
            if (storageFile == null) {
                throw new NoSuchElementException("文件不存在，fileId=" + fileId);
            }
            // 出现同名文件时重命名
            String fileName = dealFileName(storageFile, fileNameCountMap);
            zipOut.putNextEntry(new ZipEntry(fileName));
            OssS3Util.downloadFile(storageFile.getBucketName(), storageFile.getObjectName(), zipOut);
            zipOut.closeEntry();
            mapper.incrDownCount(fileId);
        }
        zipOut.close();
    }

    /**
     * 批量确认文件使用，将临时文件转换为正式文件
     *
     * @param fileIds 文件ID集合
     * @since 2024-05-17
     */
    public void confirmFiles(@NotNull List<String> fileIds) {
        if (fileIds.isEmpty()) {
            return;
        }
        for (String fileId : fileIds) {
            confirmFile(fileId);
        }
    }

    /**
     * 确认文件使用，将临时文件转换为正式文件
     *
     * @param fileId 文件ID
     * @since 2024-05-17
     */
    public void confirmFile(@NotNull String fileId) {
        mapper.confirmFile(fileId);
    }

    /**
     * 获取过期的临时文件ID集合
     *
     * @return 过期的临时文件ID集合
     * @since 2024-05-17
     */
    public Set<String> expiredTempFileIds() {
        return mapper.expiredTempFileIds();
    }

    /**
     * 处理压缩包文件重名问题
     *
     * @param storageFile 文件
     * @param fileNameCountMap 文件名计数Map
     * @return 处理后的文件名
     * @since 2024-05-17
     */
    @NotNull private static String dealFileName(
            @NotNull StorageFile storageFile, @NotNull Map<String, Integer> fileNameCountMap) {
        String fileName = storageFile.getOriginalFileName();
        if (fileNameCountMap.containsKey(fileName)) {
            String[] fileNames = fileName.split("\\.");
            if (fileNames.length == 1) {
                return fileName + "(" + fileNameCountMap.get(fileName) + ")";
            }
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < fileNames.length - 1; i++) {
                sb.append(fileNames[i]);
                if (i != fileNames.length - 2) {
                    sb.append(".");
                }
            }
            sb.append("(").append(fileNameCountMap.get(fileName)).append(").").append(fileNames[fileNames.length - 1]);
            fileName = sb.toString();
            fileNameCountMap.put(fileName, fileNameCountMap.get(fileName) + 1);
        } else {
            fileNameCountMap.put(fileName, 1);
        }
        return fileName;
    }
}
