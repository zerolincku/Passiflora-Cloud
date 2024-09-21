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
package com.zerolinck.passiflora.iam.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zerolinck.passiflora.model.iam.entity.SysRole;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.util.Collection;
import org.apache.ibatis.annotations.Param;

/**
 * 角色 Mybatis Mapper
 *
 * @author 林常坤
 * @since 2024-08-17
 */
public interface SysRoleMapper extends BaseMapper<SysRole> {

    /**
     * 分页查询
     *
     * @param page 分页条件
     * @param searchWrapper 搜索条件
     * @param sortWrapper 排序条件
     * @since 2024-08-17
     */
    @Nonnull
    Page<SysRole> page(
            @Nonnull IPage<SysRole> page,
            @Nonnull @Param(Constants.WRAPPER) QueryWrapper<SysRole> searchWrapper,
            @Nonnull @Param("sortWrapper") QueryWrapper<SysRole> sortWrapper);

    /**
     * 更新 del_flag = 1，保证 update_by 和 update_time 正确
     *
     * @param roleIds 角色主键集合
     * @since 2024-08-17
     */
    int deleteByIds(
            @Nonnull @Param("roleIds") Collection<String> roleIds, @Nullable @Param("updateBy") String updateBy);

    void disable(@Nonnull @Param("roleIds") Collection<String> roleIds, @Nullable @Param("updateBy") String updateBy);

    void enable(@Nonnull @Param("roleIds") Collection<String> roleIds, @Nullable @Param("updateBy") String updateBy);
}
