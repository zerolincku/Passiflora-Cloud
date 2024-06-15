package com.zerolinck.passiflora.${moduleName}.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zerolinck.passiflora.model.${moduleName}.entity.${entityClass};
import org.apache.ibatis.annotations.Param;

import java.util.Collection;

/**
 * ${table.description} Mybatis Mapper
 *
 * @author linck
 * @since ${date}
 */
public interface ${mapperClass} extends BaseMapper<${entityClass}> {

    Page<${entityClass}> page(IPage<${entityClass}> page,
            @Param(Constants.WRAPPER) QueryWrapper<${entityClass}> searchWrapper,
            @Param("sortWrapper") QueryWrapper<${entityClass}> sortWrapper);

    /**
    * 使用更新删除，保证 update_by 和 update_time 正确
    */
    int deleteByIds(@Param("${table.pkFieldName}s") Collection<String> ${table.pkFieldName}s, @Param("updateBy") String updateBy);
}
