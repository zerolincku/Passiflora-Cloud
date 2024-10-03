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

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zerolinck.passiflora.model.common.LabelValueInterface;
import io.swagger.v3.oas.annotations.media.Schema;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;

/**
 * 通用条件构造器，通过 url param 参数设置，使用方式
 *
 * <p>eg: 查询 id = 1 的数据，eq[id]=1 eg: 模糊查询 name 包含张三的数据，like[name]=张三 eg: 左模糊查询 name 包含张三的数据，likeL[name]=张三
 *
 * <p>eg: 右模糊查询 name 包含张三的数据，likeR[name]=张三 eg: 模糊查询 name 不包含张三的数据，nLike[name]=张三
 *
 * <p>eg: 范围查询 time 在 2023-01-01 和 2023-02-02 之间的数据, ge[time]=2023-01-01&le[time]=2023-02-02
 *
 * <p>eg: 排序 sort[name]=desc&sort[id]=asc 等同于 order by name desc, id asc
 *
 * @param <T> 创建 page 以及，queryWrapper 的泛型类
 * @author linck
 * @since 2022-11-18
 */
@Data
public class QueryCondition<T> {

    /** 创建 page 以及，queryWrapper 是泛型对应的具体实体类 */
    @Schema(hidden = true)
    private Class<?> entityClazz;

    /**
     * 表别名，只作用于实体类字段，不作用于 fieldNameCover 映射 eg: eq[name] = 2，对应到SQL时，不是 name = 2，而应该是 user.name = 2 就可以设置 tableAlise =
     * user
     */
    @Schema(hidden = true)
    private String tableAlise;

    /** 字段名转换 eg：eq[name] = 2，对应到SQL时，不是 name = 2，而应该是 user.display_name = 2 就可以配置 name: user.display_name 这样的映射关系 */
    @Schema(hidden = true)
    private Map<String, String> fieldNameCover;

    /** 字段缓存 外层 map key 为实体类 class 内存 map key 为字段名称 */
    @Schema(hidden = true)
    private static final Map<Class<?>, Map<String, Field>> FIELD_CACHE = new HashMap<>();

    /** eq == eg: eg[name]=张三 */
    @Schema(hidden = true)
    private Map<String, List<String>> eq;

    /** neq != eg: ne[name]=张三 */
    @Schema(hidden = true)
    private Map<String, List<String>> ne;

    /** gt > */
    @Schema(hidden = true)
    private Map<String, List<String>> gt;

    /** ge >= */
    @Schema(hidden = true)
    private Map<String, List<String>> ge;

    /** lt < */
    @Schema(hidden = true)
    private Map<String, List<String>> lt;

    /** le <= */
    @Schema(hidden = true)
    private Map<String, List<String>> le;

    /** 模糊搜索 eg: like[phone]=138 等同于 phone like '%138%' */
    @Schema(hidden = true)
    private Map<String, List<String>> like;

    /** 模糊搜索 eg: nLike[phone]=138 等同于 phone not like '%138%' */
    @Schema(hidden = true)
    private Map<String, List<String>> nLike;

    /** 左模糊 eg: likeL[phone]=138 等同于 phone like '%138' */
    @Schema(hidden = true)
    private Map<String, List<String>> likeL;

    /** 右模糊 eg: likeR[phone]=138 等同于 phone like '138%' */
    @Schema(hidden = true)
    private Map<String, List<String>> likeR;

    /** 排序 eg: sort[name]=desc 等同于 order by name desc eg: sort[name]=desc&sort[id]=asc 等同于 order by name desc, id asc */
    @Schema(hidden = true)
    private Map<String, List<String>> sort;

    /** 页数 */
    private Integer current;

    /** 每页行数 */
    private Integer pageSize;

    public QueryCondition() {
        eq = new HashMap<>();
        gt = new HashMap<>();
        ge = new HashMap<>();
        lt = new HashMap<>();
        le = new HashMap<>();
        ne = new HashMap<>();
        like = new HashMap<>();
        nLike = new HashMap<>();
        likeL = new HashMap<>();
        likeR = new HashMap<>();
        sort = new HashMap<>();
    }

    /** 开启分页查询，如果没有分页参数，则查询第一页，20行数据，单页最大允许查询 5000 条数据 */
    public Page<T> page() {
        if (current == null) {
            current = 1;
        }
        if (pageSize == null) {
            pageSize = 20;
        }
        // 请求允许一次最大查询 5000 条数据
        if (pageSize > 5000) {
            pageSize = 5000;
        }
        return new Page<>(current, pageSize);
    }

    /** 设置表别名 */
    @SuppressWarnings("unused")
    public QueryCondition<T> tableAlise(String tableAlise) {
        this.tableAlise = tableAlise;
        return this;
    }

    /** 设置字段名转换映射 */
    @SuppressWarnings("unused")
    public QueryCondition<T> fieldNameCover(Map<String, String> fieldNameCover) {
        this.fieldNameCover = fieldNameCover;
        return this;
    }

    /**
     * 根据 condition 内容创建 queryWrapper column 会优先使用别名转换映射进行转换 如果字段在别名转换映射中不存在，则会通过 clazz 获取对应实体类的字段，存在的字段，才允许作为条件
     *
     * @param clazz list 查询的实体类
     */
    public QueryWrapper<T> searchWrapper(Class<?> clazz) {
        this.entityClazz = clazz;

        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        this.getEq()
                .forEach((column, values) -> values.forEach(value -> {
                    String coverColumn = fieldCover(column);
                    queryWrapper.eq(coverColumn, valueCover(column, value));
                }));

        this.getNe()
                .forEach((column, values) -> values.forEach(value -> {
                    String coverColumn = fieldCover(column);
                    queryWrapper.ne(coverColumn, valueCover(column, value));
                }));

        this.getGt()
                .forEach((column, values) -> values.forEach(value -> {
                    String coverColumn = fieldCover(column);
                    queryWrapper.gt(coverColumn, valueCover(column, value));
                }));

        this.getGe()
                .forEach((column, values) -> values.forEach(value -> {
                    String coverColumn = fieldCover(column);
                    queryWrapper.ge(coverColumn, valueCover(column, value));
                }));

        this.getLt()
                .forEach((column, values) -> values.forEach(value -> {
                    String coverColumn = fieldCover(column);
                    queryWrapper.lt(coverColumn, valueCover(column, value));
                }));

        this.getLe()
                .forEach((column, values) -> values.forEach(value -> {
                    String coverColumn = fieldCover(column);
                    queryWrapper.le(coverColumn, valueCover(column, value));
                }));

        this.getLike()
                .forEach((column, values) -> values.forEach(value -> {
                    String coverColumn = fieldCover(column);
                    queryWrapper.like(coverColumn, valueCover(column, value));
                }));

        this.getNLike()
                .forEach((column, values) -> values.forEach(value -> {
                    String coverColumn = fieldCover(column);
                    queryWrapper.notLike(coverColumn, valueCover(column, value));
                }));

        this.getLikeL()
                .forEach((column, values) -> values.forEach(value -> {
                    String coverColumn = fieldCover(column);
                    queryWrapper.likeLeft(coverColumn, valueCover(column, value));
                }));

        this.getLikeR()
                .forEach((column, values) -> values.forEach(value -> {
                    String coverColumn = fieldCover(column);
                    queryWrapper.likeRight(coverColumn, valueCover(column, value));
                }));
        return queryWrapper;
    }

    public QueryWrapper<T> sortWrapper(Class<?> clazz) {
        this.entityClazz = clazz;
        QueryWrapper<T> sortWrapper = new QueryWrapper<>();
        this.getSort()
                .forEach((column, values) -> values.forEach(value -> {
                    String coverColumn = fieldCover(column);
                    if ("desc".equals(value)) {
                        sortWrapper.orderByDesc(coverColumn);
                    } else {
                        sortWrapper.orderByAsc(coverColumn);
                    }
                }));
        return sortWrapper;
    }

    /**
     * 字段转换
     *
     * @param column 原字段
     * @return 转换后的字段
     */
    public String fieldCover(String column) {
        if (this.fieldNameCover != null && this.fieldNameCover.containsKey(column)) {
            return this.fieldNameCover.get(column);
        }
        Map<String, Field> fieldMap = this.getFields();
        if (fieldMap.containsKey(column)) {
            // 驼峰转下划线
            return this.tableAlise == null ? "" : this.tableAlise + "." + StringUtils.camelToUnderline(column);
        }
        throw new IllegalArgumentException(String.format("不允许的搜索条件: %s", column));
    }

    /**
     * 值数据格式转换，针对 postgres，Mysql 不需要
     *
     * @param column 原字段
     * @return 转换后的字段
     */
    public Object valueCover(String column, String value) {
        Field field = this.getFields().get(column);
        if (field.getType().equals(String.class)) {
            return value;
        } else if (field.getType().equals(LocalDateTime.class)) {
            return TimeUtil.commonStrDate2LocalDateTime(value);
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
    private Map<String, Field> getFields() {
        if (!FIELD_CACHE.containsKey(this.entityClazz)) {
            FIELD_CACHE.put(this.entityClazz, getClassFields(this.entityClazz));
        }
        return FIELD_CACHE.get(this.entityClazz);
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
