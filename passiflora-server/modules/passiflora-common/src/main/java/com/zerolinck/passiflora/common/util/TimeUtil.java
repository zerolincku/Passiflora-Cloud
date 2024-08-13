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

import jakarta.annotation.Nonnull;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

/**
 * @author linck
 * @since 2024-02-06
 */
public class TimeUtil {

    private static final Pattern NORMAL = Pattern.compile("^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$");

    private static final Pattern NO_SECOND = Pattern.compile("^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}$");
    private static final Pattern NO_MINUTS = Pattern.compile("^\\d{4}-\\d{2}-\\d{2} \\d{2}$");
    private static final Pattern NO_HOUR = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");

    public static final DateTimeFormatter NORMAL_DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static final DateTimeFormatter NORMAL_DATE_TIME_FORMATTER_NO_SECOND =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static final DateTimeFormatter NORMAL_DATE_TIME_FORMATTER_NO_MINUTS =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH");

    public static final DateTimeFormatter NORMAL_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static final DateTimeFormatter NORMAL_TIME_FORMATTER_NO_SECOND = DateTimeFormatter.ofPattern("HH:mm");

    @Nonnull
    public static LocalDateTime commonStrDate2LocalDateTime(@Nonnull String dateStr) {
        if (NORMAL.matcher(dateStr).matches()) {
            return LocalDateTime.parse(dateStr, NORMAL_DATE_TIME_FORMATTER);
        } else if (NO_SECOND.matcher(dateStr).matches()) {
            return LocalDateTime.parse(dateStr, NORMAL_DATE_TIME_FORMATTER_NO_SECOND);
        } else if (NO_MINUTS.matcher(dateStr).matches()) {
            return LocalDateTime.parse(dateStr, NORMAL_DATE_TIME_FORMATTER_NO_MINUTS);
        } else if (NO_HOUR.matcher(dateStr).matches()) {
            return LocalDateTime.parse(dateStr + " 00", NORMAL_DATE_TIME_FORMATTER_NO_MINUTS);
        }
        throw new RuntimeException("时间参数错误: " + dateStr);
    }

    @Nonnull
    public static String getDateStrNow() {
        return LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
    }
}
