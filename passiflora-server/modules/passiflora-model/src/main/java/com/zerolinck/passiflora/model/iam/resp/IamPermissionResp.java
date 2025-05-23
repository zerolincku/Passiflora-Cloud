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
package com.zerolinck.passiflora.model.iam.resp;

import java.util.Collection;

import com.zerolinck.passiflora.model.iam.enums.PermissionTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

/** @author linck on 2024-05-06 */
@Data
public class IamPermissionResp {

    private String permissionId;

    @Schema(description = "名称", maxLength = 50)
    private String name;

    private String permissionParentId;

    private MenuMeta meta;

    private Collection<IamPermissionResp> children;

    @Data
    public static class MenuMeta {

        private String title;

        private String icon;

        private String order;

        @Schema(description = "类型")
        private PermissionTypeEnum permissionType;
    }
}
