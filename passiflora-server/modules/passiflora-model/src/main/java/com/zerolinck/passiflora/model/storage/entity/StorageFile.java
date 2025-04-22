/* 
 * Copyright (C) 2025 Linck. <zerolinck@foxmail.com>
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
package com.zerolinck.passiflora.model.storage.entity;

import java.time.LocalDateTime;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import com.zerolinck.passiflora.base.BaseEntity;
import com.zerolinck.passiflora.base.valid.Insert;
import com.zerolinck.passiflora.base.valid.Update;
import com.zerolinck.passiflora.model.storage.enums.FileStatusEnum;
import org.hibernate.validator.constraints.Length;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 通用文件 Entity
 *
 * @author linck on 2024-05-17
 */
@Data
@Table("storage_file")
@Schema(description = "通用文件")
@EqualsAndHashCode(callSuper = false)
public class StorageFile extends BaseEntity {

    @Id
    @Schema(description = "文件ID", maxLength = 20)
    @Length(
            groups = {Insert.class, Update.class},
            max = 20,
            message = "文件ID长度不能大于20")
    @NotBlank(
            groups = {Update.class},
            message = "文件ID不能为空")
    private String fileId;

    @Schema(description = "文件名称", maxLength = 100)
    @Length(
            groups = {Insert.class, Update.class},
            max = 100,
            message = "文件名称长度不能大于100")
    @NotBlank(
            groups = {Insert.class, Update.class},
            message = "文件名称不能为空")
    private String originalFileName;

    @Schema(description = "文件用途", maxLength = 20)
    @Length(
            groups = {Insert.class, Update.class},
            max = 20,
            message = "文件用途长度不能大于20")
    @NotBlank(
            groups = {Insert.class, Update.class},
            message = "文件用途不能为空")
    private String filePurpose;

    @Schema(description = "储存桶名称", maxLength = 50)
    @Length(
            groups = {Insert.class, Update.class},
            max = 50,
            message = "储存桶名称长度不能大于50")
    @NotBlank(
            groups = {Insert.class, Update.class},
            message = "储存桶名称不能为空")
    private String bucketName;

    @Schema(description = "储存对象名称", maxLength = 50)
    @Length(
            groups = {Insert.class, Update.class},
            max = 50,
            message = "储存对象名称长度不能大于50")
    @NotBlank(
            groups = {Insert.class, Update.class},
            message = "储存对象名称不能为空")
    private String objectName;

    @Schema(description = "文件大小")
    @NotNull(
            groups = {Insert.class, Update.class},
            message = "文件大小不能为空")
    private Long fileSize;

    @Schema(description = "文件contentType")
    @NotBlank(
            groups = {Insert.class, Update.class},
            message = "文件contentType不能为空")
    private String contentType;

    @Schema(description = "文件MD5", maxLength = 50)
    @Length(
            groups = {Insert.class, Update.class},
            max = 50,
            message = "文件MD5长度不能大于50")
    @NotBlank(
            groups = {Insert.class, Update.class},
            message = "文件MD5不能为空")
    private String fileMd5;

    @Schema(description = "文件下载次数")
    @NotNull(
            groups = {Insert.class, Update.class},
            message = "文件下载次数不能为空")
    private Long downloadCount;

    @Schema(description = "最后一次下载时间")
    private LocalDateTime lastDownloadTime;

    @Schema(description = "文件状态")
    @NotNull(
            groups = {Insert.class, Update.class},
            message = "文件状态不能为空")
    private FileStatusEnum fileStatus;
}
