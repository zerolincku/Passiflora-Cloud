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
package com.zerolinck.passiflora.model.iam.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import com.zerolinck.passiflora.base.BaseEntity;
import com.zerolinck.passiflora.base.enums.StatusEnum;
import com.zerolinck.passiflora.base.valid.Insert;
import com.zerolinck.passiflora.base.valid.UniqueField;
import com.zerolinck.passiflora.base.valid.Update;
import org.hibernate.validator.constraints.Length;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色 Entity
 *
 * @author 林常坤 on 2024-08-17
 */
@Data
@Table("iam_role")
@Schema(description = "角色")
@EqualsAndHashCode(callSuper = false)
public class IamRole extends BaseEntity {

    @Id
    @Schema(description = "角色ID", maxLength = 20)
    @Length(
            groups = {Insert.class, Update.class},
            max = 20,
            message = "角色ID长度不能大于20")
    @NotBlank(
            groups = {Update.class},
            message = "角色ID不能为空")
    private String roleId;

    @UniqueField
    @Schema(description = "角色名称", maxLength = 100)
    @Length(
            groups = {Insert.class, Update.class},
            max = 100,
            message = "角色名称长度不能大于100")
    @NotBlank(
            groups = {Insert.class, Update.class},
            message = "角色名称不能为空")
    private String roleName;

    @UniqueField
    @Schema(description = "角色标识", maxLength = 100)
    @Length(
            groups = {Insert.class, Update.class},
            max = 100,
            message = "角色标识长度不能大于100")
    @NotBlank(
            groups = {Insert.class, Update.class},
            message = "角色标识不能为空")
    private String roleCode;

    @Schema(description = "角色状态")
    @NotNull(
            groups = {Insert.class, Update.class},
            message = "角色状态不能为空")
    private StatusEnum roleStatus;
}
