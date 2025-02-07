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

import org.apache.commons.lang3.RandomStringUtils;

import lombok.experimental.UtilityClass;

/** @author 林常坤 on 2024/10/08 */
@UtilityClass
public class RandomUtils {

    public static String lowerCharAndNum(int count) {
        return RandomStringUtils.random(count, "abcdefghijklmnopqrstuvwxyz0123456789");
    }
}
