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
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zerolinck.passiflora.model.iam.entity.IamRolePermission;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * 角色权限 Mybatis Mapper
 *
 * @author 林常坤 on 2024-08-17
 */
public interface IamRolePermissionMapper extends BaseMapper<IamRolePermission> {

    @NotNull
    default Page<IamRolePermission> page(
            @NotNull IPage<IamRolePermission> page,
            @Param(Constants.WRAPPER) QueryWrapper<IamRolePermission> searchWrapper,
            @Param("sortWrapper") QueryWrapper<IamRolePermission> sortWrapper) {
        if (searchWrapper == null) {
            searchWrapper = new QueryWrapper<>();
        }
        searchWrapper.eq("del_flag", 0);

        if (sortWrapper == null
            || sortWrapper.getSqlSegment() == null
            || sortWrapper.getSqlSegment().isEmpty()) {
            searchWrapper.orderByAsc("id");
        } else {
            searchWrapper.last(sortWrapper.getSqlSegment());
        }

        return (Page<IamRolePermission>) this.selectPage(page, searchWrapper);
    }

    default int deleteByIds(@Nullable @Param("ids") Collection<String> ids, @Nullable @Param("updateBy") String updateBy) {
        if (CollectionUtils.isEmpty(ids)) {
            return 0;
        }

        LambdaUpdateWrapper<IamRolePermission> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper
                .in(IamRolePermission::getId, ids)
                .set(IamRolePermission::getUpdateTime, LocalDateTime.now())
                .set(IamRolePermission::getUpdateBy, updateBy)
                .set(IamRolePermission::getDelFlag, 1);

        return this.update(null, updateWrapper);
    }

    default int deleteByRoleIds(@Nullable @Param("roleIds") Collection<String> roleIds, @Nullable @Param("updateBy") String updateBy) {
        if (CollectionUtils.isEmpty(roleIds)) {
            return 0;
        }

        LambdaUpdateWrapper<IamRolePermission> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper
                .in(IamRolePermission::getRoleId, roleIds)
                .set(IamRolePermission::getUpdateTime, LocalDateTime.now())
                .set(IamRolePermission::getUpdateBy, updateBy)
                .set(IamRolePermission::getDelFlag, 1);

        return this.update(null, updateWrapper);
    }

    @Select("""
        SELECT permission_id from iam_permission
        WHERE permission_status = 1 AND del_flag = 0
        AND permission_id IN (
            SELECT permission_id from iam_role_permission
            WHERE del_flag = 0 AND role_id IN
            <foreach item="id" index="index" collection="roleIds" open="(" separator="," close=")">
                #{id}
            </foreach>
        )
    """)
    @NotNull
    List<String> permissionIdsByRoleIds(@NotNull @Param("roleIds") List<String> roleIds);
}
