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
package com.zerolinck.passiflora.model.iam.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.zerolinck.passiflora.model.common.BaseEntity;
import com.zerolinck.passiflora.model.common.enums.StatusEnum;
import com.zerolinck.passiflora.model.iam.enums.PermissionTypeEnum;
import com.zerolinck.passiflora.model.valid.Insert;
import com.zerolinck.passiflora.model.valid.OnlyField;
import com.zerolinck.passiflora.model.valid.Update;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.EqualsAndHashCode;

/** @author linck on 2024-05-06 */
@Data
@Schema(description = "菜单")
@EqualsAndHashCode(callSuper = false)
public class IamPermission extends BaseEntity {

    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键", maxLength = 20)
    @Length(
            groups = {Insert.class, Update.class},
            max = 20,
            message = "主键长度不能大于20")
    @NotBlank(
            groups = {Update.class},
            message = "主键不能为空")
    private String permissionId;

    @OnlyField
    @Schema(description = "标题", maxLength = 50)
    @Length(
            groups = {Insert.class, Update.class},
            max = 50,
            message = "标题长度不能大于50")
    @NotBlank(
            groups = {Insert.class, Update.class},
            message = "标题不能为空")
    private String permissionTitle;

    @Schema(description = "名称", maxLength = 50)
    @Length(
            groups = {Insert.class, Update.class},
            max = 50,
            message = "名称长度不能大于50")
    @NotBlank(
            groups = {Insert.class, Update.class},
            message = "名称不能为空")
    private String permissionName;

    @Schema(description = "图标", maxLength = 50)
    @Length(
            groups = {Insert.class, Update.class},
            max = 50,
            message = "图标长度不能大于50")
    private String permissionIcon;

    @Schema(description = "上级ID", maxLength = 20)
    @Length(
            groups = {Insert.class, Update.class},
            max = 20,
            message = "上级ID长度不能大于20")
    private String permissionParentId;

    @Schema(description = "ID路径", maxLength = 200)
    @Length(
            groups = {Insert.class, Update.class},
            max = 200,
            message = "ID路径长度不能大于200")
    private String permissionIdPath;

    @Schema(description = "排序")
    @TableField(value = "\"order\"")
    @NotNull(
            groups = {Update.class},
            message = "排序不能为空")
    private Integer order;

    @Schema(description = "备注", maxLength = 200)
    @Length(
            groups = {Insert.class, Update.class},
            max = 200,
            message = "备注长度不能大于200")
    private String remark;

    @Schema(description = "级别")
    private Integer permissionLevel;

    @Schema(description = "状态")
    @NotNull(
            groups = {Update.class},
            message = "状态不能为空")
    private StatusEnum permissionStatus;

    @Schema(description = "类型")
    @NotNull(
            groups = {Insert.class, Update.class},
            message = "类型不能为空")
    private PermissionTypeEnum permissionType;
}
