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

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import com.zerolinck.passiflora.base.BaseEntity;
import com.zerolinck.passiflora.base.valid.Insert;
import com.zerolinck.passiflora.base.valid.Update;
import org.hibernate.validator.constraints.Length;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.EqualsAndHashCode;

/** @author linck on 2024-05-14 */
@Data
@Table("iam_position_data_scope")
@Schema(description = "职位数据权限")
@EqualsAndHashCode(callSuper = false)
public class IamPositionDataScope extends BaseEntity {

    @Id
    @Schema(description = "主键", maxLength = 20)
    @Length(
            groups = {Insert.class, Update.class},
            max = 20,
            message = "主键长度不能大于20")
    @NotBlank(
            groups = {Update.class},
            message = "主键不能为空")
    private String scopeId;

    @Schema(description = "职位ID", maxLength = 20)
    @Length(
            groups = {Insert.class, Update.class},
            max = 20,
            message = "职位ID长度不能大于20")
    @NotBlank(
            groups = {Insert.class, Update.class},
            message = "职位ID不能为空")
    private String positionId;

    @Schema(description = "机构ID", maxLength = 20)
    @Length(
            groups = {Insert.class, Update.class},
            max = 20,
            message = "机构ID长度不能大于20")
    @NotBlank(
            groups = {Insert.class, Update.class},
            message = "机构ID不能为空")
    private String orgId;

    @Schema(description = "数据范围")
    @NotNull(
            groups = {Insert.class, Update.class},
            message = "数据范围不能为空")
    private Integer dataScope;
}
