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

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.StringJoiner;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

/** @author 林常坤 on 2024/08/16 */
@UtilityClass
public class StrUtil {

    @Nonnull
    public static String str(@Nullable Object obj, @Nonnull Charset charset) {
        return switch (obj) {
            case null -> "null";
            case String s -> s;
            case byte[] bytes -> new String(bytes, charset);
            case ByteBuffer byteBuffer -> charset.decode(byteBuffer).toString();
            default -> obj.toString();
        };
    }

    @Nonnull
    @SuppressWarnings("unused")
    public static String camelToUnderline(@Nullable String str) {
        if (str == null) {
            return "";
        }
        StringJoiner joiner = new StringJoiner("_");
        String[] split = StringUtils.splitByCharacterTypeCamelCase(str);
        for (String s : split) {
            joiner.add(s.toLowerCase());
        }
        return joiner.toString();
    }

    @Nonnull
    public static String camelToMidline(@Nullable String str) {
        if (str == null) {
            return "";
        }
        StringJoiner joiner = new StringJoiner("-");
        String[] split = StringUtils.splitByCharacterTypeCamelCase(str);
        for (String s : split) {
            joiner.add(s.toLowerCase());
        }
        return joiner.toString();
    }
}
