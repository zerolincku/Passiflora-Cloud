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

import cn.hutool.core.util.ClassUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.zerolinck.passiflora.common.util.EnumUtil;
import com.zerolinck.passiflora.common.util.TimeUtil;
import com.zerolinck.passiflora.model.common.LabelValueInterface;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author linck
 * @since 2024-02-06
 */
@Configuration
public class JacksonConfig {

    @Bean
    @SuppressWarnings("unchecked")
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        Map<Class<?>, JsonSerializer<?>> serializers = new HashMap<>();
        serializers.put(
            LocalDateTime.class,
            new LocalDateTimeSerializer(TimeUtil.NORMAL_DATE_TIME_FORMATTER)
        );
        serializers.put(
            LocalDate.class,
            new LocalDateSerializer(TimeUtil.NORMAL_DATE_FORMATTER)
        );
        serializers.put(
            LocalTime.class,
            new LocalTimeSerializer(TimeUtil.NORMAL_TIME_FORMATTER_NO_SECOND)
        );

        // NameValue 序列化配置
        serializers.put(
            LabelValueInterface.class,
            new JsonSerializer<LabelValueInterface>() {
                @Override
                public void serialize(
                    LabelValueInterface value,
                    JsonGenerator jsonGenerator,
                    SerializerProvider serializers
                ) throws IOException {
                    jsonGenerator.writeObject(value.getValue());
                }
            }
        );

        Map<Class<?>, JsonDeserializer<?>> deserializers = new HashMap<>();
        deserializers.put(LocalDateTime.class, new LocalDateTimeDeserializer());
        deserializers.put(
            LocalDate.class,
            new LocalDateDeserializer(TimeUtil.NORMAL_DATE_FORMATTER)
        );
        deserializers.put(
            LocalTime.class,
            new LocalTimeDeserializer(TimeUtil.NORMAL_TIME_FORMATTER_NO_SECOND)
        );

        // 扫描当前项目下所有 LabelValueInterface 实现类
        Set<Class<?>> classes = ClassUtil.scanPackage(
            "com.zerolinck",
            aClass ->
                LabelValueInterface.class.isAssignableFrom(aClass) &&
                !LabelValueInterface.class.equals(aClass)
        );

        // 自动注册枚举反序列化规则
        classes.forEach(clazz ->
            deserializers.put(
                clazz,
                new JsonDeserializer() {
                    @Override
                    public Object deserialize(
                        JsonParser jsonParser,
                        DeserializationContext ctxt
                    ) throws IOException {
                        Integer value = jsonParser.getIntValue();
                        return EnumUtil.getEnumByValue(
                            (Class<? extends LabelValueInterface>) clazz,
                            value
                        );
                    }
                }
            )
        );

        return builder ->
            builder
                .serializersByType(serializers)
                .deserializersByType(deserializers);
    }
}
