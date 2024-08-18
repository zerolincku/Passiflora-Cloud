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
package com.zerolinck.passiflora.system.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zerolinck.passiflora.model.system.entity.SysUserRole;
import com.zerolinck.passiflora.model.system.vo.SysUserRoleVo;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * 用户角色绑定 Mybatis Mapper
 *
 * @author 林常坤
 * @since 2024-08-17
 */
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {

    /**
     * 分页查询
     *
     * @param page 分页条件
     * @param searchWrapper 搜索条件
     * @param sortWrapper 排序条件
     * @since 2024-08-17
     */
    @Nonnull
    Page<SysUserRole> page(
            @Nonnull IPage<SysUserRole> page,
            @Nonnull @Param(Constants.WRAPPER) QueryWrapper<SysUserRole> searchWrapper,
            @Nonnull @Param("sortWrapper") QueryWrapper<SysUserRole> sortWrapper);

    /**
     * 更新 del_flag = 1，保证 update_by 和 update_time 正确
     *
     * @param ids 用户角色绑定主键集合
     * @since 2024-08-17
     */
    int deleteByIds(@Nonnull @Param("ids") Collection<String> ids, @Nullable @Param("updateBy") String updateBy);

    int deleteByUserIds(
            @Nonnull @Param("userIds") Collection<String> userIds, @Nullable @Param("updateBy") String updateBy);

    @Nonnull
    List<SysUserRoleVo> selectByUserIds(@Nonnull @Param("userIds") Collection<String> userIds);

    int deleteByUserIdAndRoleIds(
            @Nonnull @Param("userId") String userId,
            @Nonnull @Param("roleIds") Collection<String> roleIds,
            @Nullable @Param("updateBy") String updateBy);
}
