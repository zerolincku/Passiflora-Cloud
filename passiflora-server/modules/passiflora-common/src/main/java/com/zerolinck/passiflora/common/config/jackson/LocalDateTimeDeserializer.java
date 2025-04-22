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
package com.zerolinck.passiflora.common.config.jackson;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.commons.lang3.StringUtils;

/**
 * LocalDateTime反序列化器 用于将JSON中的时间戳字符串反序列化为LocalDateTime对象
 *
 * @author linck
 * @since 2024-02-06
 */
public class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
    /**
     * 反序列化 将JSON中的时间戳字符串反序列化为LocalDateTime对象
     *
     * @param p JsonParser对象，用于解析JSON内容
     * @param context DeserializationContext对象，提供反序列化上下文
     * @return 反序列化后的LocalDateTime对象
     * @throws IOException 如果解析过程中发生IO错误
     * @since 2024-02-06
     */
    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext context) throws IOException {
        String longStr = p.getValueAsString();
        if (StringUtils.isBlank(longStr)) {
            return null;
        }
        long timestamp = Long.parseLong(longStr);
        if (timestamp < 1_000_000_000_000L) {
            // 秒级时间戳
            return LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneId.systemDefault());
        } else {
            // 毫秒级时间戳
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
        }
    }
}
