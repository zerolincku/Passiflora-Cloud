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
package com.zerolinck.passiflora.common.config.jackson;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.zerolinck.passiflora.base.ILabelValue;
import com.zerolinck.passiflora.common.util.EnumUtil;
import com.zerolinck.passiflora.common.util.TimeUtil;
import com.zerolinck.passiflora.common.util.lock.ClassUtil;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Jackson配置类 配置Jackson的序列化和反序列化规则
 *
 * @since 2024-02-06
 */
@Configuration(proxyBeanMethods = false)
public class JacksonConfig {

    /**
     * 配置Jackson的ObjectMapper
     *
     * @return Jackson2ObjectMapperBuilderCustomizer对象
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        Map<Class<?>, JsonSerializer<?>> serializers = getDefaultSerializer();
        Map<Class<?>, JsonDeserializer<?>> deserializers = getDefaultDeserializer();

        return builder -> builder.locale(Locale.CHINA)
                .timeZone(TimeZone.getTimeZone(ZoneId.systemDefault()))
                .serializersByType(serializers)
                .deserializersByType(deserializers);
    }

    /**
     * 获取默认的反序列化器
     *
     * @return 反序列化器的映射
     */
    @SuppressWarnings("unchecked")
    public static Map<Class<?>, JsonDeserializer<?>> getDefaultDeserializer() {
        Map<Class<?>, JsonDeserializer<?>> deserializers = new HashMap<>();
        deserializers.put(LocalDateTime.class, new LocalDateTimeDeserializer());
        deserializers.put(LocalDate.class, new LocalDateDeserializer(TimeUtil.NORMAL_DATE_FORMATTER));
        deserializers.put(LocalTime.class, new LocalTimeDeserializer(TimeUtil.NORMAL_TIME_FORMATTER_NO_SECOND));

        Set<Class<?>> classes = ClassUtil.getLabelValueClasses();

        // 自动注册枚举反序列化规则
        classes.forEach(clazz -> deserializers.put(clazz, new JsonDeserializer<>() {
            @Override
            public Object deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException {
                Integer value = jsonParser.getValueAsInt();
                return EnumUtil.getEnumByValue((Class<? extends ILabelValue>) clazz, value);
            }
        }));
        return deserializers;
    }

    /**
     * 获取默认的序列化器
     *
     * @return 序列化器的映射
     */
    public static Map<Class<?>, JsonSerializer<?>> getDefaultSerializer() {
        Map<Class<?>, JsonSerializer<?>> serializers = new HashMap<>();
        serializers.put(LocalDateTime.class, new LocalDateTimeSerializer());
        serializers.put(LocalDate.class, new LocalDateSerializer(TimeUtil.NORMAL_DATE_FORMATTER));
        serializers.put(LocalTime.class, new LocalTimeSerializer(TimeUtil.NORMAL_TIME_FORMATTER_NO_SECOND));

        // Long 序列化规则
        serializers.put(Long.class, ToStringSerializer.instance);
        serializers.put(Long.TYPE, ToStringSerializer.instance);

        // NameValue 序列化配置
        serializers.put(ILabelValue.class, new JsonSerializer<ILabelValue>() {
            @Override
            public void serialize(ILabelValue value, JsonGenerator jsonGenerator, SerializerProvider serializers)
                    throws IOException {
                jsonGenerator.writeObject(value.getValue());
            }
        });
        return serializers;
    }
}
