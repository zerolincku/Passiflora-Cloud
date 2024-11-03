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
package com.zerolinck.passiflora.storage.controller;

import com.zerolinck.passiflora.common.api.Result;
import com.zerolinck.passiflora.common.util.AssertUtil;
import com.zerolinck.passiflora.common.util.QueryCondition;
import com.zerolinck.passiflora.feign.storage.StorageFileApi;
import com.zerolinck.passiflora.model.storage.entity.StorageFile;
import com.zerolinck.passiflora.storage.service.StorageFileService;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 通用文件 Controller
 *
 * @author linck on 2024-05-17
 */
@Slf4j
@RestController
@RequestMapping("storage-file")
@RequiredArgsConstructor
public class StorageFileController implements StorageFileApi {

    private final StorageFileService storageFileService;

    @NotNull @Override
    public Result<List<StorageFile>> page(@NotNull QueryCondition<StorageFile> condition) {
        return Result.ok(storageFileService.page(condition));
    }

    @NotNull @Override
    public Result<List<StorageFile>> listByFileIds(@NotNull List<String> fileIds) {
        return Result.ok(storageFileService.listByFileIds(fileIds));
    }

    /**
     * 尝试文件秒传
     *
     * @param storageFile 需要参数 originalFileName，contentType, fileMd5
     * @return 空字符串表示无法秒传，应再次调用文件上传接口；有值字符串表示上传成功，上传文件ID
     */
    @NotNull @Override
    public Result<String> tryQuicklyUpload(@NotNull StorageFile storageFile) {
        AssertUtil.notBlank(storageFile.getOriginalFileName(), "文件名称不能为空，请重新填写");
        AssertUtil.notBlank(storageFile.getContentType(), "文件 contentType 不能为空，请重新填写");
        AssertUtil.notBlank(storageFile.getFileMd5(), "文件 MD5 不能为空，请重新填写");
        return Result.ok(storageFileService.tryQuicklyUpload(storageFile));
    }

    @NotNull @Override
    public Result<String> upload(@NotNull MultipartFile file, @Nullable String fileName) {
        return Result.ok(storageFileService.upload(file, fileName));
    }

    @NotNull @Override
    public Result<StorageFile> detail(@NotNull String fileId) {
        AssertUtil.notBlank(fileId, "文件 ID 不能为空");
        return Result.ok(storageFileService.detail(fileId).orElseThrow(() -> new NoSuchElementException("无此文件")));
    }

    @NotNull @Override
    public Result<String> delete(@NotNull List<String> fileIds) {
        AssertUtil.notEmpty(fileIds, "文件列表不能为空");
        storageFileService.deleteByIds(fileIds);
        return Result.ok();
    }

    @Override
    public void downloadFile(@NotNull String fileId) {
        storageFileService.downloadFile(fileId);
    }

    @Override
    public void downloadZip(@NotNull List<String> fileIds) {
        storageFileService.downloadZip(fileIds);
    }

    @NotNull @Override
    public Result<String> confirmFile(@NotNull List<String> fileIds) {
        storageFileService.confirmFiles(fileIds);
        return Result.ok();
    }
}
