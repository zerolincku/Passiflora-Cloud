<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.zerolinck.passiflora.${moduleName}.mapper.${mapperClass}">

    <!-- 通用查询结果列 -->
    <sql id="Base_Column_List">
        <#list table.originColumnList as column>${column.columnName?trim}<#if column_has_next>, </#if></#list>
    </sql>

    <select id="page" resultType="com.zerolinck.passiflora.model.${moduleName}.entity.${entityClass}">
        SELECT * FROM ${tableName}
        <where>
            del_flag = 0
            <if test="ew.sqlSegment != null and ew.sqlSegment != ''">
                AND ${'$'}{ew.sqlSegment}
            </if>
        </where>
        <if test="sortWrapper.sqlSegment == null or sortWrapper.sqlSegment == ''">
            ORDER BY ${table.pkColumnName} ASC
        </if>
        <if test="sortWrapper.sqlSegment != null and sortWrapper.sqlSegment != ''">
            ${'$'}{sortWrapper.sqlSegment}
        </if>
    </select>

    <delete id="deleteByIds">
        UPDATE ${tableName} SET update_time = now(),
                                update_by = ${'#'}{updateBy},
                                del_flag = 1
        WHERE ${table.pkColumnName} IN
        <foreach item="id" index="index" collection="${table.pkFieldName}s" open="(" separator="," close=")">
            ${'#'}{id}
        </foreach>
    </delete>

</mapper>