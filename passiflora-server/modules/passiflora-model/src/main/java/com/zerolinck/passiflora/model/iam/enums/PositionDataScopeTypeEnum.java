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

/** @author linck on 2024-05-29 */
@Getter
@AllArgsConstructor
public enum PositionDataScopeTypeEnum implements ILabelValue {
    USER_ORGANIZATION("用户所属机构", 0),
    USER_ORGANIZATION_AND_SUBORDINATES("用户所属机构及其下级机构", 1),
    SPECIFIED_ORGANIZATION("指定机构", 2),
    SPECIFIED_ORGANIZATION_AND_SUBORDINATES("指定机构及其下级机构", 3),
    ALL("全部", 4);

    private final String label;

    @EnumValue
    private final Integer value;
}
