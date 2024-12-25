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

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import com.zerolinck.passiflora.base.BaseEntity;
import com.zerolinck.passiflora.base.valid.Insert;
import com.zerolinck.passiflora.base.valid.Update;
import org.hibernate.validator.constraints.Length;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色权限 Entity
 *
 * @author 林常坤 on 2024-08-17
 */
@Data
@Table("iam_role_permission")
@Schema(description = "角色权限")
@EqualsAndHashCode(callSuper = false)
public class IamRolePermission extends BaseEntity {

    @Id
    @Schema(description = "主键", maxLength = 20)
    @Length(
            groups = {Insert.class, Update.class},
            max = 20,
            message = "主键长度不能大于20")
    @NotBlank(
            groups = {Update.class},
            message = "主键不能为空")
    private String id;

    @Schema(description = "角色ID", maxLength = 20)
    @Length(
            groups = {Insert.class, Update.class},
            max = 20,
            message = "角色ID长度不能大于20")
    @NotBlank(
            groups = {Insert.class, Update.class},
            message = "角色ID不能为空")
    private String roleId;

    @Schema(description = "权限ID", maxLength = 20)
    @Length(
            groups = {Insert.class, Update.class},
            max = 20,
            message = "权限ID长度不能大于20")
    @NotBlank(
            groups = {Insert.class, Update.class},
            message = "权限ID不能为空")
    private String permissionId;
}
