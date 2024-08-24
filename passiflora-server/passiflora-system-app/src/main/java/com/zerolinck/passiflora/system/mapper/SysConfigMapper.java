package com.zerolinck.passiflora.system.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zerolinck.passiflora.model.system.entity.SysConfig;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;

/**
 * 系统配置 Mybatis Mapper
 *
 * @author 林常坤
 * @since 2024-08-24
 */
public interface SysConfigMapper extends BaseMapper<SysConfig> {

    /**
     * 分页查询
     *
     * @param page 分页条件
     * @param searchWrapper 搜索条件
     * @param sortWrapper 排序条件
     * @since 2024-08-24
     */
    @Nonnull
    Page<SysConfig> page(@Nonnull IPage<SysConfig> page,
            @Nonnull @Param(Constants.WRAPPER) QueryWrapper<SysConfig> searchWrapper,
            @Nonnull @Param("sortWrapper") QueryWrapper<SysConfig> sortWrapper);

    /**
     * 更新 del_flag = 1，保证 update_by 和 update_time 正确
     *
     * @param configIds 系统配置主键集合
     * @since 2024-08-24
     */
    int deleteByIds(@Nonnull @Param("configIds") Collection<String> configIds,
                    @Nullable @Param("updateBy") String updateBy);
}
