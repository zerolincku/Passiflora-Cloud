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

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.zerolinck.passiflora.model.common.BaseEntity;
import com.zerolinck.passiflora.model.common.enums.StatusEnum;
import com.zerolinck.passiflora.model.iam.enums.PositionDataScopeTypeEnum;
import com.zerolinck.passiflora.model.valid.Insert;
import com.zerolinck.passiflora.model.valid.OnlyField;
import com.zerolinck.passiflora.model.valid.Update;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;

/** @author linck on 2024-05-14 */
@Data
@Schema(description = "职位")
@EqualsAndHashCode(callSuper = false)
public class IamPosition extends BaseEntity {

    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "职位ID", maxLength = 20)
    @Length(
            groups = {Insert.class, Update.class},
            max = 20,
            message = "职位ID长度不能大于20")
    @NotBlank(
            groups = {Update.class},
            message = "职位ID不能为空")
    private String positionId;

    @OnlyField
    @Schema(description = "职位名称", maxLength = 100)
    @Length(
            groups = {Insert.class, Update.class},
            max = 100,
            message = "职位名称长度不能大于100")
    @NotBlank(
            groups = {Insert.class, Update.class},
            message = "职位名称不能为空")
    private String positionName;

    @Schema(description = "职位级别")
    private Integer positionLevel;

    @Schema(description = "父职位ID", maxLength = 20)
    @Length(
            groups = {Insert.class, Update.class},
            max = 20,
            message = "父职位ID长度不能大于20")
    private String parentPositionId;

    @Schema(description = "数据范围类型")
    @NotNull(
            groups = {Insert.class, Update.class},
            message = "数据范围类型不能为空")
    private PositionDataScopeTypeEnum dataScopeType;

    @Schema(description = "职位ID路径", maxLength = 200)
    private String positionIdPath;

    @Schema(description = "职位状态")
    @NotNull(
            groups = {Insert.class, Update.class},
            message = "职位状态不能为空")
    private StatusEnum positionStatus;

    @Schema(description = "排序")
    @TableField(value = "\"order\"")
    @NotNull(
            groups = {Update.class},
            message = "排序不能为空")
    private Integer order;
}
