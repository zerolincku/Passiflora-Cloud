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
package com.zerolinck.passiflora.common.util;

import com.zerolinck.passiflora.base.ILabelValue;
import org.jetbrains.annotations.NotNull;

import lombok.experimental.UtilityClass;

/** @author linck on 2024-02-06 */
@UtilityClass
public class EnumUtils {

    @NotNull public static ILabelValue getEnumByValue(@NotNull Class<? extends ILabelValue> clazz, @NotNull Object value) {
        ILabelValue[] enumConstants = clazz.getEnumConstants();
        for (ILabelValue nameValue : enumConstants) {
            if (nameValue.getValue().equals(value)) {
                return nameValue;
            }
        }
        throw new IllegalStateException(clazz.getSimpleName() + " 无此value: " + value);
    }

    @NotNull @SuppressWarnings("unused")
    public static ILabelValue getEnumByName(@NotNull Class<? extends ILabelValue> clazz, @NotNull Object name) {
        ILabelValue[] enumConstants = clazz.getEnumConstants();
        for (ILabelValue nameValue : enumConstants) {
            if (nameValue.getLabel().equals(name)) {
                return nameValue;
            }
        }
        throw new IllegalStateException(clazz.getSimpleName() + " 无此name: " + name);
    }
}
