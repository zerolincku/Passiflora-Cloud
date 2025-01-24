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

import static com.mybatisflex.core.query.QueryMethods.select;
import static com.zerolinck.passiflora.model.iam.entity.table.IamPermissionTableDef.IAM_PERMISSION;
import static com.zerolinck.passiflora.model.iam.entity.table.IamRolePermissionTableDef.IAM_ROLE_PERMISSION;

import java.util.Collection;
import java.util.List;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.zerolinck.passiflora.base.enums.DelFlagEnum;
import com.zerolinck.passiflora.base.enums.StatusEnum;
import com.zerolinck.passiflora.model.iam.entity.IamRolePermission;
import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 角色权限 Mybatis Mapper
 *
 * @author 林常坤
 * @since 2024-08-17
 */
public interface IamRolePermissionMapper extends BaseMapper<IamRolePermission> {

    /**
     * 根据角色ID集合删除角色权限
     *
     * @param roleIds 角色ID集合
     * @return 删除的角色权限数量
     * @since 2024-08-17
     */
    default int deleteByRoleIds(@Nullable Collection<String> roleIds) {
        if (CollectionUtils.isEmpty(roleIds)) {
            return 0;
        }

        return this.deleteByQuery(new QueryWrapper().in(IamRolePermission::getRoleId, roleIds));
    }

    /**
     * 根据角色ID集合查询权限ID集合
     *
     * @param roleIds 角色ID集合
     * @return 权限ID集合
     * @since 2024-08-17
     */
    @NotNull default List<String> permissionIdsByRoleIds(@NotNull List<String> roleIds) {
        return selectListByQueryAs(
                QueryWrapper.create()
                        .select(IAM_PERMISSION.PERMISSION_ID)
                        .from(IAM_PERMISSION)
                        .where(IAM_PERMISSION.PERMISSION_STATUS.eq(StatusEnum.ENABLE))
                        .and(IAM_PERMISSION.DEL_FLAG.eq(DelFlagEnum.NOT_DELETE))
                        .and(IAM_PERMISSION.PERMISSION_ID.in(select(IAM_ROLE_PERMISSION.PERMISSION_ID)
                                .from(IAM_ROLE_PERMISSION)
                                .where(IAM_ROLE_PERMISSION.DEL_FLAG.eq(DelFlagEnum.NOT_DELETE))
                                .and(IAM_ROLE_PERMISSION.ROLE_ID.in(roleIds)))),
                String.class);
    }
}
