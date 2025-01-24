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
package com.zerolinck.passiflora.model.iam.enums;

import com.mybatisflex.annotation.EnumValue;
import com.zerolinck.passiflora.base.ILabelValue;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** @author linck on 2024-05-08 */
@Getter
@AllArgsConstructor
public enum PermissionTypeEnum implements ILabelValue {
    MENU_SET("目录", 0),
    MENU("菜单", 1),
    PERMISSION("权限", 2);

    private final String label;

    @EnumValue
    private final Integer value;
}
