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
package com.zerolinck.passiflora.common.util;

import java.util.HashSet;
import java.util.Set;

import lombok.experimental.UtilityClass;

/** @author linck on 2024-06-29 */
@UtilityClass
public class SetUtil {

    /** 返回 set2 相对于 set1 多出的值 */
    public static Set<String> set2MoreOutSet1(Set<String> set1, Set<String> set2) {
        Set<String> difference = new HashSet<>(set2);
        difference.removeAll(set1);
        return difference;
    }
}
