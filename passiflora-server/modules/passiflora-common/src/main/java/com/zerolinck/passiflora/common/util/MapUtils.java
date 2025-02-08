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

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import lombok.experimental.UtilityClass;

/** @author 林常坤 on 2024/10/24 */
@UtilityClass
public class MapUtils {

    public static MapBuilder builder() {
        return new MapBuilder();
    }

    public static class MapBuilder {

        private final Map<String, Object> map = new HashMap<>();

        public MapBuilder put(@NotNull String key, @Nullable Object value) {
            map.put(key, value);
            return this;
        }

        @NotNull public Map<String, Object> build() {
            return map;
        }
    }
}
