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

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.net.URLEncodeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.http.Header;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zerolinck.passiflora.common.config.PassifloraProperties;
import com.zerolinck.passiflora.common.exception.BizException;
import com.zerolinck.passiflora.common.util.CurrentUtil;
import com.zerolinck.passiflora.common.util.NetUtil;
import com.zerolinck.passiflora.common.util.QueryCondition;
import com.zerolinck.passiflora.common.util.lock.LockUtil;
import com.zerolinck.passiflora.common.util.lock.LockWrapper;
import com.zerolinck.passiflora.model.storage.entity.StorageFile;
import com.zerolinck.passiflora.model.storage.enums.FileStatusEnum;
import com.zerolinck.passiflora.storage.mapper.StorageFileMapper;
import io.minio.*;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * 通用文件 Service
 *
 * @author linck
 * @since 2024-05-17
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StorageFileService extends ServiceImpl<StorageFileMapper, StorageFile> {

    private final MinioClient minioClient;
    private final PassifloraProperties passifloraProperties;

    private static final String LOCK_KEY = "passiflora:lock:storageFile:";

    @Nonnull
    public Page<StorageFile> page(@Nullable QueryCondition<StorageFile> condition) {
        condition = Objects.requireNonNullElse(condition, new QueryCondition<>());
        return baseMapper.page(
                condition.page(), condition.searchWrapper(StorageFile.class), condition.sortWrapper(StorageFile.class));
    }

    public List<StorageFile> listByFileIds(List<String> fileIds) {
        return baseMapper.selectList(new LambdaQueryWrapper<StorageFile>().in(StorageFile::getFileId, fileIds));
    }

    /**
     * 尝试文件秒传
     *
     * @param storageFile 需要参数 originalFileName，contentType, fileMd5
     * @return 空字符串表示无法秒传，应再次调用文件上传接口；有值字符串表示上传成功，上传文件ID
     */
    @Nonnull
    public String tryQuicklyUpload(@Nonnull StorageFile storageFile) {
        return LockUtil.lock(
                LOCK_KEY,
                new LockWrapper<StorageFile>().lock(StorageFile::getFileMd5, storageFile.getFileMd5()),
                true,
                () -> {
                    List<StorageFile> storageFiles = listByFileMd5(storageFile.getFileMd5());
                    if (CollectionUtil.isEmpty(storageFiles)) {
                        return "";
                    }
                    StorageFile dbFile = storageFiles.getFirst();
                    dbFile.setFileId(null);
                    dbFile.setFilePurpose(null);
                    dbFile.setDownloadCount(null);
                    dbFile.setLastDownloadTime(null);
                    dbFile.setFileStatus(FileStatusEnum.TEMP);
                    dbFile.setOriginalFileName(storageFile.getOriginalFileName());
                    dbFile.setContentType(storageFile.getContentType());
                    baseMapper.insert(dbFile);
                    return dbFile.getFileId();
                });
    }

    @Nonnull
    @SneakyThrows
    public String upload(@Nonnull MultipartFile file, @Nonnull String fileName) {
        String md5Hex = DigestUtil.md5Hex(file.getBytes());
        return LockUtil.lock(
                LOCK_KEY, new LockWrapper<StorageFile>().lock(StorageFile::getFileMd5, md5Hex), true, () -> {
                    String bucket = passifloraProperties.getStorage().getBucketName();
                    StorageFile storageFile = new StorageFile();
                    storageFile.setOriginalFileName(file.getOriginalFilename());
                    if (StrUtil.isNotBlank(fileName)) {
                        storageFile.setOriginalFileName(fileName);
                    }
                    storageFile.setContentType(file.getContentType());
                    storageFile.setFileMd5(md5Hex);
                    storageFile.setFileSize(file.getSize());
                    storageFile.setFileStatus(FileStatusEnum.TEMP);
                    String extName = FileUtil.extName(storageFile.getOriginalFileName());
                    String objectName = md5Hex + "." + extName;
                    storageFile.setBucketName(bucket);
                    storageFile.setObjectName(objectName);
                    String tryQuicklyUploadResult = tryQuicklyUpload(storageFile);
                    if (StrUtil.isNotBlank(tryQuicklyUploadResult)) {
                        return tryQuicklyUploadResult;
                    }

                    // 检查文件是否已经上传
                    List<StorageFile> storageFiles = baseMapper.selectList(
                            new LambdaQueryWrapper<StorageFile>().eq(StorageFile::getFileMd5, md5Hex));
                    StatObjectResponse statObject = null;
                    if (!storageFiles.isEmpty()) {
                        StorageFile dbStorageFile = storageFiles.getFirst();
                        objectName = dbStorageFile.getObjectName();
                        bucket = dbStorageFile.getBucketName();
                        // 检查 minio 文件是否存在
                        try {
                            statObject = minioClient.statObject(StatObjectArgs.builder()
                                    .bucket(dbStorageFile.getBucketName())
                                    .object(dbStorageFile.getObjectName())
                                    .build());
                            log.info("文件已存在，秒传，fileMd5: {}", md5Hex);
                        } catch (Exception e) {
                            // 可能查询文件不存在，尝试重新覆盖文件
                            log.warn(
                                    "minio stat 文件错误 objectName: {}, exception: {}",
                                    dbStorageFile.getObjectName(),
                                    e.getMessage());
                        }
                    }

                    if (statObject == null) {
                        try {
                            minioClient.putObject(PutObjectArgs.builder().bucket(bucket).object(objectName).stream(
                                            file.getInputStream(),
                                            file.getInputStream().available(),
                                            -1)
                                    .contentType(file.getContentType())
                                    .build());
                        } catch (Exception e) {
                            throw new BizException(e);
                        }
                    }
                    this.save(storageFile);
                    return storageFile.getFileId();
                });
    }

    @Nonnull
    public List<StorageFile> listByFileMd5(@Nonnull String fileMd5) {
        return baseMapper.listByFileMd5(fileMd5);
    }

    public void deleteByIds(@Nonnull Collection<String> fileIds) {
        for (String fileId : fileIds) {
            deleteById(fileId);
        }
    }

    @SneakyThrows
    public void deleteById(@Nonnull String fileId) {
        if (StrUtil.isBlank(fileId)) {
            return;
        }
        StorageFile storageFile = baseMapper.selectById(fileId);
        if (storageFile == null) {
            return;
        }
        LockUtil.lock(
                LOCK_KEY + "md5:",
                new LockWrapper<StorageFile>().lock(StorageFile::getFileMd5, storageFile.getFileMd5()),
                true,
                () -> {
                    baseMapper.deleteByIds(List.of(fileId), CurrentUtil.getCurrentUserId());
                    Integer md5Count = baseMapper.countByFileMd5(storageFile.getFileMd5());
                    if (md5Count == 0) {
                        try {
                            minioClient.removeObject(RemoveObjectArgs.builder()
                                    .bucket(storageFile.getBucketName())
                                    .object(storageFile.getObjectName())
                                    .build());
                        } catch (Exception e) {
                            throw new BizException(e);
                        }
                    }
                    return null;
                });
    }

    @Nonnull
    public StorageFile detail(@Nonnull String fileId) {
        StorageFile storageFile = baseMapper.selectById(fileId);
        if (storageFile == null) {
            throw new BizException("无对应通用文件数据，请刷新后重试");
        }
        return storageFile;
    }

    @SneakyThrows
    public void downloadFile(@Nonnull String fileId) {
        StorageFile storageFile = baseMapper.selectById(fileId);
        if (storageFile == null) {
            throw new BizException("文件消失了");
        }
        GetObjectResponse inputStream = minioClient.getObject(GetObjectArgs.builder()
                .bucket(storageFile.getBucketName())
                .object(storageFile.getObjectName())
                .build());
        HttpServletResponse response = NetUtil.getResponse();
        if (MimeTypeUtils.APPLICATION_JSON.toString().equals(storageFile.getContentType())) {
            response.setContentType(MimeTypeUtils.TEXT_PLAIN.toString());
        } else {
            response.setContentType(storageFile.getContentType());
        }
        response.setHeader(
                Header.CONTENT_DISPOSITION.getValue(),
                "attachment; filename=" + URLEncodeUtil.encode(storageFile.getOriginalFileName()));
        IOUtils.copy(inputStream, response.getOutputStream());
        baseMapper.incrDownCount(fileId);
    }

    @SneakyThrows
    public void downloadZip(@Nonnull List<String> fileIds) {
        NetUtil.getResponse()
                .setHeader(
                        Header.CONTENT_DISPOSITION.getValue(),
                        "attachment; filename=" + URLEncodeUtil.encode("文件压缩包.zip"));
        ZipOutputStream zipOut = new ZipOutputStream(NetUtil.getResponse().getOutputStream());
        Set<String> fileNameSet = new HashSet<>();
        for (String fileId : fileIds) {
            StorageFile storageFile = baseMapper.selectById(fileId);

            // 出现同名文件时重命名
            String fileName = dealFileName(storageFile, fileNameSet);
            fileNameSet.add(fileName);
            zipOut.putNextEntry(new ZipEntry(fileName));
            GetObjectResponse inputStream = minioClient.getObject(GetObjectArgs.builder()
                    .bucket(storageFile.getBucketName())
                    .object(storageFile.getObjectName())
                    .build());
            IOUtils.copy(inputStream, zipOut);
            inputStream.close();
            zipOut.closeEntry();
            baseMapper.incrDownCount(fileId);
        }
        zipOut.close();
    }

    public void confirmFile(@Nonnull List<String> fileIds) {
        if (fileIds.isEmpty()) {
            return;
        }
        baseMapper.confirmFile(fileIds, CurrentUtil.getCurrentUserId());
    }

    /** 处理压缩包文件重名问题 */
    @Nonnull
    private static String dealFileName(@Nonnull StorageFile storageFile, @Nonnull Set<String> fileNameSet) {
        String fileName = storageFile.getOriginalFileName();
        while (fileNameSet.contains(fileName)) {
            String[] fileNames = fileName.split("\\.");
            if (fileNames.length == 1) {
                fileName += "-1";
                continue;
            }
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < fileNames.length - 1; i++) {
                sb.append(fileNames[i]);
                if (i != fileNames.length - 2) {
                    sb.append(".");
                }
            }
            sb.append("-1");
            sb.append(".");
            sb.append(fileNames[fileNames.length - 1]);
            fileName = sb.toString();
        }
        return fileName;
    }
}
