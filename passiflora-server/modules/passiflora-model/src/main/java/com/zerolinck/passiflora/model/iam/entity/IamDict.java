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
import com.zerolinck.passiflora.base.enums.YesOrNoEnum;
import com.zerolinck.passiflora.base.valid.Insert;
import com.zerolinck.passiflora.base.valid.UniqueField;
import com.zerolinck.passiflora.base.valid.Update;
import org.hibernate.validator.constraints.Length;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.EqualsAndHashCode;

/** @author linck on 2024-04-01 */
@Data
@Table("iam_dict")
@Schema(description = "字典")
@EqualsAndHashCode(callSuper = false)
public class IamDict extends BaseEntity {

    @Id
    @Schema(description = "主键ID", maxLength = 20)
    @Length(
            groups = {Update.class},
            max = 20,
            message = "主键ID长度不能大于20")
    @NotBlank(
            groups = {Update.class},
            message = "主键ID不能为空")
    private String dictId;

    @UniqueField
    @Schema(description = "字典名称", maxLength = 20)
    @Length(
            groups = {Insert.class, Update.class},
            max = 20,
            message = "字典名称长度不能大于20")
    @NotBlank(
            groups = {Insert.class, Update.class},
            message = "字典名称不能为空")
    private String dictName;

    @UniqueField
    @Schema(description = "字典标识", maxLength = 100)
    @Length(
            groups = {Insert.class, Update.class},
            max = 100,
            message = "字典标识长度不能大于100")
    @NotBlank(
            groups = {Insert.class, Update.class},
            message = "字典标识不能为空")
    private String dictTag;

    @Schema(description = "描述", maxLength = 200)
    @Length(
            groups = {Insert.class, Update.class},
            max = 200,
            message = "描述长度不能大于200")
    private String remark;

    @Schema(description = "是否系统内置")
    private YesOrNoEnum isSystem;

    @Schema(description = "字典项值是否唯一")
    private YesOrNoEnum valueIsOnly;
}
