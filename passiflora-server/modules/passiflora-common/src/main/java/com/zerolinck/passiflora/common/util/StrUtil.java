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
import javax.annotation.Nullable;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import lombok.experimental.UtilityClass;

/** @author 林常坤 on 2024/08/16 */
@UtilityClass
public class StrUtil {

    @NotNull public static String str(@Nullable Object obj, @NotNull Charset charset) {
        return switch (obj) {
            case null -> "null";
            case String s -> s;
            case byte[] bytes -> new String(bytes, charset);
            case ByteBuffer byteBuffer -> charset.decode(byteBuffer).toString();
            default -> obj.toString();
        };
    }

    /** 驼峰字符串，转换为使用 _ 连接的字符串 */
    @NotNull @SuppressWarnings("unused")
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

    /** 驼峰字符串，转换为使用 - 连接的字符串 */
    @NotNull public static String camelToMidline(@Nullable String str) {
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

    /** 将字符串的第一个字符转为大写 */
    public static String upperFirst(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    /** 将下划线分隔的字符串转换为驼峰命名法 */
    public static String toCamelCase(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }

        StringBuilder result = new StringBuilder();
        boolean toUpperCase = false;

        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == '_') {
                toUpperCase = true;
            } else {
                if (toUpperCase) {
                    result.append(Character.toUpperCase(c));
                    toUpperCase = false;
                } else {
                    result.append(c);
                }
            }
        }
        return result.toString();
    }

    /**
     * 生成指定长度的随机字符串
     *
     * @param length 字符串长度
     * @return 随机字符串
     */
    public static String randomString(int length) {
        return RandomStringUtils.secure().next(length);
    }
}
