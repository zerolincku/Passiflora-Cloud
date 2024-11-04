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

import com.zerolinck.passiflora.model.common.enums.StatusEnum;
import com.zerolinck.passiflora.model.iam.enums.PermissionTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.Collection;
import lombok.Data;

/** @author linck on 2024-05-06 */
@Data
public class IamPermissionTableResp {

    @Schema(description = "主键")
    private String permissionId;

    @Schema(description = "标题")
    private String permissionTitle;

    @Schema(description = "名称")
    private String permissionName;

    @Schema(description = "图标")
    private String permissionIcon;

    @Schema(description = "上级ID")
    private String permissionParentId;

    @Schema(description = "ID路径")
    private String permissionIdPath;

    @Schema(description = "排序")
    private Integer order;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "级别")
    private Integer permissionLevel;

    @Schema(description = "状态")
    private StatusEnum permissionStatus;

    @Schema(description = "类型")
    private PermissionTypeEnum permissionType;

    @Schema(description = "乐观锁版本")
    private Long version;

    @Schema(description = "子权限")
    private Collection<IamPermissionTableResp> children = new ArrayList<>();
}
