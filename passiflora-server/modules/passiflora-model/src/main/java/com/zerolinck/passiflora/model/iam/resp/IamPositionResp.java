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
package com.zerolinck.passiflora.model.iam.resp;

import java.util.ArrayList;
import java.util.Collection;

import com.zerolinck.passiflora.base.enums.StatusEnum;
import com.zerolinck.passiflora.model.iam.enums.PositionDataScopeTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

/** @author linck on 2024-05-28 */
@Data
@Schema(description = "职位")
public class IamPositionResp {

    @Schema(description = "职位ID")
    private String positionId;

    @Schema(description = "职位名称")
    private String positionName;

    @Schema(description = "职位级别")
    private Integer positionLevel;

    @Schema(description = "父职位ID")
    private String parentPositionId;

    @Schema(description = "数据范围类型")
    private PositionDataScopeTypeEnum dataScopeType;

    @Schema(description = "职位ID路径")
    private String positionIdPath;

    @Schema(description = "职位状态")
    private StatusEnum positionStatus;

    @Schema(description = "排序")
    private Integer order;

    @Schema(description = "乐观锁版本")
    private Long version;

    @Schema(description = "子职位")
    private Collection<IamPositionResp> children = new ArrayList<>();
}
