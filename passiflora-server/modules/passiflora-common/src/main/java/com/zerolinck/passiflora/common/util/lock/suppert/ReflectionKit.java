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

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import com.zerolinck.passiflora.common.util.AssertUtil;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** 反射工具类，提供反射相关的快捷操作 */
public final class ReflectionKit {

    /** class field cache */
    private static final Map<Class<?>, List<Field>> CLASS_FIELD_CACHE = new ConcurrentHashMap<>();

    @Deprecated
    private static final Map<Class<?>, Class<?>> PRIMITIVE_WRAPPER_TYPE_MAP = new IdentityHashMap<>(8);

    private static final Map<Class<?>, Class<?>> PRIMITIVE_TYPE_TO_WRAPPER_MAP = new IdentityHashMap<>(8);

    static {
        PRIMITIVE_WRAPPER_TYPE_MAP.put(Boolean.class, boolean.class);
        PRIMITIVE_WRAPPER_TYPE_MAP.put(Byte.class, byte.class);
        PRIMITIVE_WRAPPER_TYPE_MAP.put(Character.class, char.class);
        PRIMITIVE_WRAPPER_TYPE_MAP.put(Double.class, double.class);
        PRIMITIVE_WRAPPER_TYPE_MAP.put(Float.class, float.class);
        PRIMITIVE_WRAPPER_TYPE_MAP.put(Integer.class, int.class);
        PRIMITIVE_WRAPPER_TYPE_MAP.put(Long.class, long.class);
        PRIMITIVE_WRAPPER_TYPE_MAP.put(Short.class, short.class);
        for (Map.Entry<Class<?>, Class<?>> entry : PRIMITIVE_WRAPPER_TYPE_MAP.entrySet()) {
            PRIMITIVE_TYPE_TO_WRAPPER_MAP.put(entry.getValue(), entry.getKey());
        }
    }

    /**
     * 获取字段值
     *
     * @param entity 实体
     * @param fieldName 字段名称
     * @return 属性值
     */
    public static Object getFieldValue(Object entity, String fieldName) {
        Class<?> cls = entity.getClass();
        Map<String, Field> fieldMaps = getFieldMap(cls);
        try {
            Field field = fieldMaps.get(fieldName);
            AssertUtil.notNull(field, "Error: NoSuchField in %s for %s.  Cause:", cls.getSimpleName(), fieldName);
            field.setAccessible(true);
            return field.get(entity);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(
                    String.format("Error: Cannot read field in %s.  Cause:", cls.getSimpleName()), e);
        }
    }

    /**
     * 反射对象获取泛型
     *
     * @param clazz 对象
     * @param genericIfc 所属泛型父类
     * @param index 泛型所在位置
     * @return Class
     */
    public static Class<?> getSuperClassGenericType(final Class<?> clazz, final Class<?> genericIfc, final int index) {
        // update by noear @2021-09-03
        Class<?>[] typeArguments = GenericTypeUtils.resolveTypeArguments(ClassUtils.getUserClass(clazz), genericIfc);
        return null == typeArguments ? null : typeArguments[index];
    }

    /**
     * 获取该类的所有属性列表
     *
     * @param clazz 反射类
     */
    public static Map<String, Field> getFieldMap(Class<?> clazz) {
        List<Field> fieldList = getFieldList(clazz);
        return CollectionUtils.isNotEmpty(fieldList)
                ? fieldList.stream().collect(Collectors.toMap(Field::getName, Function.identity()))
                : Collections.emptyMap();
    }

    /**
     * 获取该类的所有属性列表
     *
     * @param clazz 反射类
     */
    public static List<Field> getFieldList(Class<?> clazz) {
        if (Objects.isNull(clazz)) {
            return Collections.emptyList();
        }
        return CollectionUtils.computeIfAbsent(CLASS_FIELD_CACHE, clazz, k -> {
            Field[] fields = k.getDeclaredFields();
            List<Field> superFields = new ArrayList<>();
            Class<?> currentClass = k.getSuperclass();
            while (currentClass != null) {
                Field[] declaredFields = currentClass.getDeclaredFields();
                Collections.addAll(superFields, declaredFields);
                currentClass = currentClass.getSuperclass();
            }
            /* 排除重载属性 */
            Map<String, Field> fieldMap = excludeOverrideSuperField(fields, superFields);
            /*
             * 重写父类属性过滤后处理忽略部分，支持过滤父类属性功能
             * 场景：中间表不需要记录创建时间，忽略父类 createTime 公共属性
             * 中间表实体重写父类属性 ` private transient Date createTime; `
             */
            return fieldMap.values().stream()
                    /* 过滤静态属性 */
                    .filter(f -> !Modifier.isStatic(f.getModifiers()))
                    /* 过滤 transient关键字修饰的属性 */
                    .filter(f -> !Modifier.isTransient(f.getModifiers()))
                    .collect(Collectors.toList());
        });
    }

    /**
     * 排序重置父类属性
     *
     * @param fields 子类属性
     * @param superFieldList 父类属性
     */
    public static Map<String, Field> excludeOverrideSuperField(Field[] fields, List<Field> superFieldList) {
        // 子类属性
        Map<String, Field> fieldMap = Stream.of(fields)
                .collect(toMap(
                        Field::getName,
                        identity(),
                        (u, v) -> {
                            throw new IllegalStateException(String.format("Duplicate key %s", u));
                        },
                        LinkedHashMap::new));
        superFieldList.stream()
                .filter(field -> !fieldMap.containsKey(field.getName()))
                .forEach(f -> fieldMap.put(f.getName(), f));
        return fieldMap;
    }

    /**
     * 判断是否为基本类型或基本包装类型
     *
     * @param clazz class
     * @return 是否基本类型或基本包装类型
     */
    @Deprecated
    public static boolean isPrimitiveOrWrapper(Class<?> clazz) {
        AssertUtil.notNull(clazz, "Class must not be null");
        return (clazz.isPrimitive() || PRIMITIVE_WRAPPER_TYPE_MAP.containsKey(clazz));
    }

    public static Class<?> resolvePrimitiveIfNecessary(Class<?> clazz) {
        return (clazz.isPrimitive() && clazz != void.class ? PRIMITIVE_TYPE_TO_WRAPPER_MAP.get(clazz) : clazz);
    }
}
