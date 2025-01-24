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

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;

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
 * @author linck on 2022-11-18
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
    private Integer pageNum;

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

    public Integer getPageNum() {
        if (pageNum == null) {
            pageNum = 1;
        }
        return pageNum;
    }

    public Integer getPageSize() {
        if (pageSize == null) {
            pageSize = 10;
        } else if (pageSize > 5000) {
            pageSize = 5000;
        }
        return pageSize;
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
}
