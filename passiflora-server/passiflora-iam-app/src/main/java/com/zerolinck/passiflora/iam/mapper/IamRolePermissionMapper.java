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

import java.util.Collection;
import java.util.List;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.zerolinck.passiflora.model.iam.entity.IamRolePermission;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 角色权限 Mybatis Mapper
 *
 * @author 林常坤 on 2024-08-17
 */
public interface IamRolePermissionMapper extends BaseMapper<IamRolePermission> {

    default int deleteByRoleIds(@Nullable @Param("roleIds") Collection<String> roleIds) {
        if (CollectionUtils.isEmpty(roleIds)) {
            return 0;
        }

        return this.deleteByQuery(new QueryWrapper().in(IamRolePermission::getRoleId, roleIds));
    }

    @Select(
            """
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
    @NotNull List<String> permissionIdsByRoleIds(@NotNull @Param("roleIds") List<String> roleIds);
}
