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

import org.hibernate.validator.constraints.Length;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.zerolinck.passiflora.model.common.BaseEntity;
import com.zerolinck.passiflora.model.valid.Insert;
import com.zerolinck.passiflora.model.valid.Update;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户角色绑定 Entity
 *
 * @author 林常坤 on 2024-08-17
 */
@Data
@Schema(description = "用户角色绑定")
@EqualsAndHashCode(callSuper = false)
public class IamUserRole extends BaseEntity {

    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键", maxLength = 20)
    @Length(
            groups = {Insert.class, Update.class},
            max = 20,
            message = "主键长度不能大于20")
    @NotBlank(
            groups = {Update.class},
            message = "主键不能为空")
    private String id;

    @Schema(description = "用户ID", maxLength = 20)
    @Length(
            groups = {Insert.class, Update.class},
            max = 20,
            message = "用户ID长度不能大于20")
    @NotBlank(
            groups = {Insert.class, Update.class},
            message = "用户ID不能为空")
    private String userId;

    @Schema(description = "角色ID", maxLength = 20)
    @Length(
            groups = {Insert.class, Update.class},
            max = 20,
            message = "角色ID长度不能大于20")
    @NotBlank(
            groups = {Insert.class, Update.class},
            message = "角色ID不能为空")
    private String roleId;
}
