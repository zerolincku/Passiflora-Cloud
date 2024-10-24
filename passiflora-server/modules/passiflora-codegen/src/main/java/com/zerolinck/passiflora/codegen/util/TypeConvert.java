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
package com.zerolinck.passiflora.codegen.util;

import com.fasterxml.jackson.databind.JsonNode;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** @author linck on 2024-02-06 */
public class TypeConvert {

    public static Class<?> columnType2FieldType(String columnType) {
        if (columnType.startsWith("character varying")) {
            return String.class;
        } else if (columnType.startsWith("text")) {
            return String.class;
        } else if (columnType.startsWith("timestamp")) {
            return LocalDateTime.class;
        } else if (columnType.startsWith("smallint")) {
            return Integer.class;
        } else if (columnType.startsWith("integer")) {
            return Integer.class;
        } else if (columnType.startsWith("bigint")) {
            return Long.class;
        } else if (columnType.startsWith("json")) {
            return JsonNode.class;
        } else if (columnType.startsWith("boolean")) {
            return Boolean.class;
        } else if (columnType.startsWith("numeric")) {
            return BigDecimal.class;
        } else if (columnType.startsWith("date")) {
            return LocalDate.class;
        }
        throw new IllegalArgumentException("不支持的类型" + columnType);
    }
}
