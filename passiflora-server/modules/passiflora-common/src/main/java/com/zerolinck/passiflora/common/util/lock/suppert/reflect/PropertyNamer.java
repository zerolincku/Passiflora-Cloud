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
package com.zerolinck.passiflora.common.util.lock.suppert.reflect;

import java.util.Locale;

public final class PropertyNamer {

    private PropertyNamer() {
        // Prevent Instantiation of Static Class
    }

    public static String methodToProperty(String name) {
        if (name.startsWith("is")) {
            name = name.substring(2);
        } else if (name.startsWith("get") || name.startsWith("set")) {
            name = name.substring(3);
        } else {
            throw new RuntimeException(
                "Error parsing property name '" +
                name +
                "'.  Didn't start with 'is', 'get' or 'set'."
            );
        }

        if (
            name.length() == 1 ||
            (name.length() > 1 && !Character.isUpperCase(name.charAt(1)))
        ) {
            name =
            name.substring(0, 1).toLowerCase(Locale.ENGLISH) +
            name.substring(1);
        }

        return name;
    }

    public static boolean isProperty(String name) {
        return isGetter(name) || isSetter(name);
    }

    public static boolean isGetter(String name) {
        return (
            (name.startsWith("get") && name.length() > 3) ||
            (name.startsWith("is") && name.length() > 2)
        );
    }

    public static boolean isSetter(String name) {
        return name.startsWith("set") && name.length() > 3;
    }
}
