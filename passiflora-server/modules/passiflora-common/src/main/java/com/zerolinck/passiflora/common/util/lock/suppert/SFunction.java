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
package com.zerolinck.passiflora.common.util.lock.suppert;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.function.Function;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 支持序列化的 Function */
@FunctionalInterface
public interface SFunction<T, R> extends Function<T, R>, Serializable {
    @NotNull Cache<Class<?>, String> FIELD_NAME_CACHE = Caffeine.newBuilder()
            .initialCapacity(512)
            .maximumSize(8192)
            .expireAfterAccess(Duration.ofHours(12))
            .build();

    @NotNull Logger log = LoggerFactory.getLogger(SFunction.class);

    @NotNull default String getFieldName() {
        return FIELD_NAME_CACHE.get(this.getClass(), clazz -> {
            SerializedLambda serializedLambda = getSerializedLambda(clazz);
            String fieldName = getString(serializedLambda);
            Class<?> implClass;
            try {
                implClass = Class.forName(serializedLambda.getImplClass().replace("/", "."));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Class not found: " + serializedLambda.getImplClass(), e);
            }
            Field field = getField(implClass, fieldName);
            String name = field.getName();
            log.trace("get field name success, class: {}, field: {}", clazz.getName(), name);
            return name;
        });
    }

    @NotNull private SerializedLambda getSerializedLambda(@NotNull Class<?> clazz) {
        try {
            Method writeReplaceMethod = clazz.getDeclaredMethod("writeReplace");
            boolean isAccessible = writeReplaceMethod.canAccess(this);
            try {
                writeReplaceMethod.setAccessible(true);
                return (SerializedLambda) writeReplaceMethod.invoke(this);
            } finally {
                writeReplaceMethod.setAccessible(isAccessible);
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            log.error("Failed to get SerializedLambda", e);
            throw new RuntimeException("Failed to get SerializedLambda", e);
        }
    }

    @NotNull private String getString(@NotNull SerializedLambda serializedLambda) {
        String implMethodName = serializedLambda.getImplMethodName();
        // get方法开头为 is 或者 get，将方法名 去除is或者get，然后首字母小写，就是属性名
        int prefixLen;
        if (implMethodName.startsWith("is")) {
            prefixLen = 2;
        } else if (implMethodName.startsWith("get")) {
            prefixLen = 3;
        } else {
            String message = "get方法名称: " + implMethodName + ", 不符合java bean规范";
            log.error(message);
            throw new RuntimeException(message);
        }
        String fieldName = implMethodName.substring(prefixLen);
        char firstChar = Character.toLowerCase(fieldName.charAt(0));
        return firstChar + fieldName.substring(1);
    }

    @NotNull private Field getField(@NotNull Class<?> clazz, @NotNull String name) {
        try {
            return clazz.getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            Class<?> superclass = clazz.getSuperclass();
            if (superclass != null) {
                return getField(superclass, name);
            }
            throw new RuntimeException(e);
        }
    }
}
