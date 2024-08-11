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
package com.zerolinck.passiflora.feign.storage;

import com.zerolinck.passiflora.common.api.ListWithPage;
import com.zerolinck.passiflora.common.api.Result;
import com.zerolinck.passiflora.common.util.QueryCondition;
import com.zerolinck.passiflora.feign.config.FeignConfiguration;
import com.zerolinck.passiflora.model.storage.entity.StorageFile;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author linck
 * @since 2024-05-17
 */
@Tag(name = "通用文件")
@FeignClient(
        value = "storageFile",
        contextId = "storageFile",
        path = "/passiflora/storage-api/storageFile",
        configuration = FeignConfiguration.class)
public interface StorageFileApi {
    @Operation(summary = "分页查询")
    @GetMapping("page")
    Result<ListWithPage<StorageFile>> page(QueryCondition<StorageFile> condition);

    @Operation(summary = "根据文件ids获取列表")
    @PostMapping("listByFileIds")
    Result<List<StorageFile>> listByFileIds(@RequestBody List<String> fileIds);

    /**
     * @param storageFile 需要参数 originalFileName，contentType, fileMd5
     * @return 空字符串表示无法秒传，应再次调用文件上传接口；有值字符串表示上传成功，上传文件ID
     */
    @Nonnull
    @Operation(
            summary = "尝试文件秒传",
            description =
                    "需要参数 originalFileName，contentType, fileMd5。\n" + "返回空字符串表示无法秒传，应再次调用文件上传接口；有值字符串表示上传成功，上传文件ID")
    @PostMapping("tryQuicklyUpload")
    Result<String> tryQuicklyUpload(@Nonnull @RequestBody StorageFile storageFile);

    @Nonnull
    @Operation(summary = "文件上传")
    @PostMapping("upload")
    Result<String> upload(
            @Nonnull @RequestParam("file") MultipartFile file,
            @Nullable @RequestParam(value = "fileName", required = false) String fileName);

    @Nonnull
    @Operation(summary = "详情")
    @GetMapping("detail")
    Result<StorageFile> detail(@Nonnull @RequestParam(value = "fileId") String fileId);

    @Nonnull
    @Operation(summary = "删除")
    @PostMapping("delete")
    Result<String> delete(@Nonnull @RequestBody List<String> fileIds);

    @Operation(summary = "文件下载")
    @GetMapping(value = "/downloadFile")
    void downloadFile(@Nonnull @RequestParam("fileId") String fileId);

    @Operation(summary = "文件批量下载")
    @PostMapping(value = "/downloadZip")
    void downloadZip(@Nonnull @RequestBody List<String> fileIds);

    @Nonnull
    @Operation(summary = "确认文件使用", description = "文件由临时文件转换为正式文件")
    @PostMapping(value = "/confirmFile")
    Result<String> confirmFile(@Nonnull @RequestBody List<String> fileIds);
}
