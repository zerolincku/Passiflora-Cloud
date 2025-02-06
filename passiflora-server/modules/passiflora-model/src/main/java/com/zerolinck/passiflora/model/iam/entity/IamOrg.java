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

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import com.zerolinck.passiflora.base.BaseEntity;
import com.zerolinck.passiflora.base.valid.Insert;
import com.zerolinck.passiflora.base.valid.UniqueField;
import com.zerolinck.passiflora.base.valid.Update;
import org.hibernate.validator.constraints.Length;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.EqualsAndHashCode;

/** @author linck on 2024-04-09 */
@Data
@Table("iam_org")
@Schema(description = "机构")
@EqualsAndHashCode(callSuper = false)
public class IamOrg extends BaseEntity {

    @Id
    @Schema(description = "机构ID", maxLength = 20)
    @Length(
            groups = {Insert.class, Update.class},
            max = 20,
            message = "机构ID长度不能大于20")
    @NotBlank(
            groups = {Update.class},
            message = "机构ID不能为空")
    private String orgId;

    @Schema(description = "机构名称", maxLength = 100)
    @Length(
            groups = {Insert.class, Update.class},
            max = 100,
            message = "机构名称长度不能大于100")
    @NotBlank(
            groups = {Insert.class},
            message = "机构名称不能为空")
    private String orgName;

    @UniqueField
    @Schema(description = "机构编码", maxLength = 100)
    @Length(
            groups = {Insert.class, Update.class},
            max = 100,
            message = "机构编码长度不能大于100")
    @NotBlank(
            groups = {Insert.class},
            message = "机构编码不能为空")
    private String orgCode;

    @Schema(description = "机构类型")
    @NotNull(
            groups = {Insert.class, Update.class},
            message = "机构类型不能为空")
    private Integer orgType;

    @Schema(description = "机构级别")
    @NotNull(
            groups = {Insert.class, Update.class},
            message = "机构级别不能为空")
    private Integer orgLevel;

    @Schema(description = "父机构ID", maxLength = 20)
    @Length(
            groups = {Insert.class, Update.class},
            max = 20,
            message = "父机构ID长度不能大于20")
    private String parentOrgId;

    @Schema(description = "机构ID路径", maxLength = 200)
    @Length(
            groups = {Insert.class, Update.class},
            max = 200,
            message = "机构ID路径长度不能大于200")
    private String orgIdPath;

    @Schema(description = "排序")
    @Column(value = "order")
    @NotNull(
            groups = {Update.class},
            message = "排序不能为空")
    private Integer order;
}
