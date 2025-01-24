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
package com.zerolinck.passiflora.mybatis.util;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import com.mybatisflex.core.query.QueryWrapper;
import com.zerolinck.passiflora.base.LabelValueInterface;
import com.zerolinck.passiflora.common.util.QueryCondition;
import com.zerolinck.passiflora.common.util.StrUtil;
import com.zerolinck.passiflora.common.util.TimeUtil;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.experimental.UtilityClass;

/** @author 林常坤 on 2024/12/26 */
@UtilityClass
public class ConditionUtils {

    /** 字段缓存 外层 map key 为实体类 class 内存 map key 为字段名称 */
    @Schema(hidden = true)
    private static final Map<Class<?>, Map<String, Field>> FIELD_CACHE = new HashMap<>();

    /** 根据 condition 内容创建 queryWrapper column 会优先使用别名转换映射进行转换 如果字段在别名转换映射中不存在，则会通过 clazz 获取对应实体类的字段，存在的字段，才允许作为条件 */
    public static <T> QueryWrapper searchWrapper(QueryCondition<T> condition, Class<?> clazz) {

        QueryWrapper queryWrapper = new QueryWrapper();
        condition
                .getEq()
                .forEach((column, values) -> values.forEach(value -> {
                    String coverColumn = fieldCover(condition, column, clazz);
                    queryWrapper.eq(coverColumn, valueCover(column, value, clazz));
                }));

        condition
                .getNe()
                .forEach((column, values) -> values.forEach(value -> {
                    String coverColumn = fieldCover(condition, column, clazz);
                    queryWrapper.ne(coverColumn, valueCover(column, value, clazz));
                }));

        condition
                .getGt()
                .forEach((column, values) -> values.forEach(value -> {
                    String coverColumn = fieldCover(condition, column, clazz);
                    queryWrapper.gt(coverColumn, valueCover(column, value, clazz));
                }));

        condition
                .getGe()
                .forEach((column, values) -> values.forEach(value -> {
                    String coverColumn = fieldCover(condition, column, clazz);
                    queryWrapper.ge(coverColumn, valueCover(column, value, clazz));
                }));

        condition
                .getLt()
                .forEach((column, values) -> values.forEach(value -> {
                    String coverColumn = fieldCover(condition, column, clazz);
                    queryWrapper.lt(coverColumn, valueCover(column, value, clazz));
                }));

        condition
                .getLe()
                .forEach((column, values) -> values.forEach(value -> {
                    String coverColumn = fieldCover(condition, column, clazz);
                    queryWrapper.le(coverColumn, valueCover(column, value, clazz));
                }));

        condition
                .getLike()
                .forEach((column, values) -> values.forEach(value -> {
                    String coverColumn = fieldCover(condition, column, clazz);
                    queryWrapper.like(coverColumn, valueCover(column, value, clazz));
                }));

        condition
                .getNLike()
                .forEach((column, values) -> values.forEach(value -> {
                    String coverColumn = fieldCover(condition, column, clazz);
                    queryWrapper.notLike(coverColumn, valueCover(column, value, clazz));
                }));

        condition
                .getLikeL()
                .forEach((column, values) -> values.forEach(value -> {
                    String coverColumn = fieldCover(condition, column, clazz);
                    queryWrapper.likeLeft(coverColumn, valueCover(column, value, clazz));
                }));

        condition
                .getLikeR()
                .forEach((column, values) -> values.forEach(value -> {
                    String coverColumn = fieldCover(condition, column, clazz);
                    queryWrapper.likeRight(coverColumn, valueCover(column, value, clazz));
                }));
        condition
                .getSort()
                .forEach((column, values) -> values.forEach(value -> {
                    String coverColumn = fieldCover(condition, column, clazz);
                    queryWrapper.orderBy(coverColumn, !"desc".equals(value));
                }));
        return queryWrapper;
    }

    /**
     * 字段转换
     *
     * @param column 原字段
     * @return 转换后的字段
     */
    private static <T> String fieldCover(QueryCondition<T> condition, String column, Class<?> clazz) {
        if (condition.getFieldNameCover() != null
                && condition.getFieldNameCover().containsKey(column)) {
            return condition.getFieldNameCover().get(column);
        }
        Map<String, Field> fieldMap = getFields(clazz);
        if (fieldMap.containsKey(column)) {
            // 驼峰转下划线
            return condition.getTableAlise() == null
                    ? ""
                    : condition.getTableAlise() + "." + StrUtil.camelToUnderline(column);
        }
        throw new IllegalArgumentException(String.format("不允许的搜索条件: %s", column));
    }

    /**
     * 值数据格式转换，针对 postgres，Mysql 不需要
     *
     * @param column 原字段
     * @return 转换后的字段
     */
    private static Object valueCover(String column, String value, Class<?> clazz) {
        Field field = getFields(clazz).get(column);
        if (field.getType().equals(String.class)) {
            return value;
        } else if (field.getType().equals(LocalDateTime.class)) {
            return TimeUtil.timestamp2LocalDateTime(value);
        } else if (field.getType().equals(LocalDate.class)) {
            return LocalDate.parse(value, TimeUtil.NORMAL_DATE_FORMATTER);
        } else if (field.getType().equals(LocalTime.class)) {
            return LocalTime.parse(value, TimeUtil.NORMAL_TIME_FORMATTER_NO_SECOND);
        } else if (field.getType().equals(Integer.class)) {
            return Integer.valueOf(value);
        } else if (field.getType().equals(Long.class)) {
            return Long.valueOf(value);
        }
        Class<?>[] interfaces = field.getType().getInterfaces();
        if (interfaces.length > 0 && interfaces[0].equals(LabelValueInterface.class)) {
            return Integer.valueOf(value);
        }
        throw new IllegalArgumentException("不支持的搜索类型：" + field.getType().getSimpleName());
    }

    /** 获取字段缓存 */
    private static Map<String, Field> getFields(Class<?> clazz) {
        if (!FIELD_CACHE.containsKey(clazz)) {
            FIELD_CACHE.put(clazz, getClassFields(clazz));
        }
        return FIELD_CACHE.get(clazz);
    }

    /**
     * 初始化实体类字段缓存
     *
     * @param clazz 要操作的实体类 class
     */
    private static Map<String, Field> getClassFields(Class<?> clazz) {
        Map<String, Field> fieldMap = new HashMap<>();

        // 如果父类不是 Object，获取其父类字段
        Class<?> superclass = clazz.getSuperclass();
        while (!superclass.equals(Object.class)) {
            for (Field field : superclass.getDeclaredFields()) {
                fieldMap.put(field.getName(), field);
            }
            superclass = superclass.getSuperclass();
        }

        for (Field field : clazz.getDeclaredFields()) {
            fieldMap.put(field.getName(), field);
        }
        return fieldMap;
    }
}
