package com.zerolinck.passiflora.${moduleName}.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zerolinck.passiflora.model.${moduleName}.entity.${entityClass};
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;

/**
 * ${table.description} Mybatis Mapper
 *
 * @author ${author}
 * @since ${date}
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
    @Nonnull
    Page<${entityClass}> page(@Nonnull IPage<${entityClass}> page,
            @Nonnull @Param(Constants.WRAPPER) QueryWrapper<${entityClass}> searchWrapper,
            @Nonnull @Param("sortWrapper") QueryWrapper<${entityClass}> sortWrapper);

    /**
     * 更新 del_flag = 1，保证 update_by 和 update_time 正确
     *
     * @param ${table.pkFieldName}s ${table.description}主键集合
     * @since ${date}
     */
    int deleteByIds(@Nonnull @Param("${table.pkFieldName}s") Collection<String> ${table.pkFieldName}s,
                    @Nonnull @Param("updateBy") String updateBy);
}
