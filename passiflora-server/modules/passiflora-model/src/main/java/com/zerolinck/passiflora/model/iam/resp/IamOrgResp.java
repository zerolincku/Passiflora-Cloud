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

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/** @author linck on 2024-04-09 */
@Data
@Schema(description = "机构")
public class IamOrgResp {

    @Schema(description = "机构ID")
    private String orgId;

    @Schema(description = "机构名称")
    private String orgName;

    @Schema(description = "机构编码")
    private String orgCode;

    @Schema(description = "机构类型")
    private Integer orgType;

    @Schema(description = "机构级别")
    private Integer orgLevel;

    @Schema(description = "父机构ID")
    private String parentOrgId;

    @Schema(description = "机构ID路径")
    private String orgIdPath;

    @Schema(description = "排序")
    private Integer order;

    @Schema(description = "乐观锁版本")
    private Long version;

    @Schema(description = "子机构")
    List<IamOrgResp> children = new ArrayList<>();
}
