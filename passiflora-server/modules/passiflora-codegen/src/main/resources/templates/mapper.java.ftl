package com.zerolinck.passiflora.${moduleName}.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zerolinck.passiflora.model.${moduleName}.entity.${entityClass};
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;

/**
 * ${table.description} Mybatis Mapper
 *
 * @author ${author} on ${date}
 */
public interface ${mapperClass} extends BaseMapper<${entityClass}> {

    /**
     * 分页查询
     *
     * @param page 分页条件
     * @param searchWrapper 搜索条件
     * @param sortWrapper 排序条件
     * @since ${date}
     */
    @NotNull
    Page<${entityClass}> page(@NotNull IPage<${entityClass}> page,
            @NotNull @Param(Constants.WRAPPER) QueryWrapper<${entityClass}> searchWrapper,
            @NotNull @Param("sortWrapper") QueryWrapper<${entityClass}> sortWrapper);

    /**
     * 更新 del_flag = 1，保证 update_by 和 update_time 正确
     *
     * @param ${table.pkFieldName}s ${table.description}主键集合
     * @since ${date}
     */
    int deleteByIds(@NotNull @Param("${table.pkFieldName}s") Collection<String> ${table.pkFieldName}s,
                    @Nullable @Param("updateBy") String updateBy);
}
