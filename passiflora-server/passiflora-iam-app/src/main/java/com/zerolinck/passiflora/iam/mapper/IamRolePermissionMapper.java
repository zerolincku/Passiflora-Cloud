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
import com.zerolinck.passiflora.model.iam.entity.IamRolePermission;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * 角色权限 Mybatis Mapper
 *
 * @author 林常坤
 * @since 2024-08-17
 */
public interface IamRolePermissionMapper extends BaseMapper<IamRolePermission> {

    /**
     * 分页查询
     *
     * @param page 分页条件
     * @param searchWrapper 搜索条件
     * @param sortWrapper 排序条件
     * @since 2024-08-17
     */
    @Nonnull
    Page<IamRolePermission> page(
            @Nonnull IPage<IamRolePermission> page,
            @Nonnull @Param(Constants.WRAPPER) QueryWrapper<IamRolePermission> searchWrapper,
            @Nonnull @Param("sortWrapper") QueryWrapper<IamRolePermission> sortWrapper);

    /**
     * 更新 del_flag = 1，保证 update_by 和 update_time 正确
     *
     * @param ids 角色权限主键集合
     * @since 2024-08-17
     */
    int deleteByIds(@Nonnull @Param("ids") Collection<String> ids, @Nullable @Param("updateBy") String updateBy);

    int deleteByRoleIds(
            @Nonnull @Param("roleIds") Collection<String> roleIds, @Nullable @Param("updateBy") String updateBy);

    @Nonnull
    List<String> permissionIdsByRoleIds(@Nonnull @Param("roleIds") List<String> roleIds);
}
