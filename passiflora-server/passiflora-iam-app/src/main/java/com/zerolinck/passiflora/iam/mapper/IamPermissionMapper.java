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
import static com.zerolinck.passiflora.model.iam.entity.table.IamPositionPermissionTableDef.IAM_POSITION_PERMISSION;
import static com.zerolinck.passiflora.model.iam.entity.table.IamRolePermissionTableDef.IAM_ROLE_PERMISSION;
import static com.zerolinck.passiflora.model.iam.entity.table.IamUserPositionTableDef.IAM_USER_POSITION;
import static com.zerolinck.passiflora.model.iam.entity.table.IamUserRoleTableDef.IAM_USER_ROLE;

import java.util.Collection;
import java.util.List;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.update.UpdateChain;
import com.zerolinck.passiflora.base.enums.StatusEnum;
import com.zerolinck.passiflora.model.iam.entity.IamPermission;
import com.zerolinck.passiflora.model.iam.resp.IamPermissionTableResp;
import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;

/**
 * 权限 Mybatis Mapper
 *
 * @author linck
 * @since 2024-05-06
 */
public interface IamPermissionMapper extends BaseMapper<IamPermission> {

    /**
     * 更新权限顺序
     *
     * @param iamPermissionTableResp 权限表响应对象
     * @since 2024-05-06
     */
    default void updateOrder(@NotNull IamPermissionTableResp iamPermissionTableResp) {
        UpdateChain.of(IamPermission.class)
                .set(IamPermission::getOrder, iamPermissionTableResp.getOrder())
                .eq(IamPermission::getPermissionId, iamPermissionTableResp.getPermissionId())
                .update();
    }

    /**
     * 禁用权限
     *
     * @param permissionIds 权限ID集合
     * @return 如果禁用成功返回true，否则返回false
     * @since 2024-05-06
     */
    default boolean disable(Collection<String> permissionIds) {
        if (CollectionUtils.isEmpty(permissionIds)) {
            return true;
        }

        return UpdateChain.of(IamPermission.class)
                .set(IamPermission::getPermissionStatus, StatusEnum.DISABLE)
                .in(IamPermission::getPermissionId, permissionIds)
                .update();
    }

    /**
     * 启用权限
     *
     * @param permissionIds 权限ID集合
     * @return 如果启用成功返回true，否则返回false
     * @since 2024-05-06
     */
    default boolean enable(Collection<String> permissionIds) {
        if (CollectionUtils.isEmpty(permissionIds)) {
            return true;
        }

        return UpdateChain.of(IamPermission.class)
                .set(IamPermission::getPermissionStatus, StatusEnum.ENABLE)
                .in(IamPermission::getPermissionId, permissionIds)
                .update();
    }

    /**
     * 根据父权限ID查询子权限列表
     *
     * @param permissionParentId 父权限ID
     * @return 子权限列表
     * @since 2024-05-06
     */
    default List<IamPermission> listByParentId(@NotNull String permissionParentId) {
        return selectListByQuery(QueryWrapper.create()
                .where(IAM_PERMISSION.PERMISSION_PARENT_ID.eq(permissionParentId))
                .orderBy(IAM_PERMISSION.PERMISSION_LEVEL, true)
                .orderBy(IAM_PERMISSION.ORDER, true)
                .orderBy(IAM_PERMISSION.PERMISSION_TITLE, true));
    }

    /**
     * 根据权限ID查询自身及子权限列表
     *
     * @param permissionId 权限ID
     * @return 自身及子权限列表
     * @since 2024-05-06
     */
    default List<IamPermission> listSelfAndSub(@NotNull String permissionId) {
        return selectListByQuery(QueryWrapper.create()
                .where(IAM_PERMISSION.PERMISSION_ID_PATH.like(permissionId))
                .orderBy(IAM_PERMISSION.PERMISSION_LEVEL, true)
                .orderBy(IAM_PERMISSION.ORDER, true)
                .orderBy(IAM_PERMISSION.PERMISSION_TITLE, true));
    }

    /**
     * 根据职位ID查询权限列表
     *
     * @param positionId 职位ID
     * @return 权限列表
     * @since 2024-05-06
     */
    default List<IamPermission> listByPositionId(@NotNull String positionId) {
        return selectListByQuery(QueryWrapper.create()
                .select(IAM_PERMISSION.ALL_COLUMNS)
                .from(IAM_PERMISSION)
                .where(IAM_PERMISSION.PERMISSION_STATUS.eq(StatusEnum.ENABLE))
                .and(IAM_PERMISSION.DEL_FLAG.eq(StatusEnum.ENABLE))
                .and(IAM_PERMISSION.PERMISSION_ID.in(select(IAM_POSITION_PERMISSION.PERMISSION_ID)
                        .from(IAM_POSITION_PERMISSION)
                        .where(IAM_POSITION_PERMISSION.DEL_FLAG.eq(StatusEnum.ENABLE))
                        .and(IAM_POSITION_PERMISSION.POSITION_ID.eq(positionId)))));
    }

    /**
     * 根据角色ID查询权限列表
     *
     * @param roleId 角色ID
     * @return 权限列表
     * @since 2024-05-06
     */
    default List<IamPermission> listByRoleId(@NotNull String roleId) {
        return selectListByQuery(QueryWrapper.create()
                .select(IAM_PERMISSION.ALL_COLUMNS)
                .from(IAM_PERMISSION)
                .where(IAM_PERMISSION.PERMISSION_STATUS.eq(StatusEnum.ENABLE))
                .and(IAM_PERMISSION.DEL_FLAG.eq(StatusEnum.ENABLE))
                .and(IAM_PERMISSION.PERMISSION_ID.in(select(IAM_ROLE_PERMISSION.PERMISSION_ID)
                        .from(IAM_ROLE_PERMISSION)
                        .where(IAM_ROLE_PERMISSION.DEL_FLAG.eq(StatusEnum.ENABLE))
                        .and(IAM_ROLE_PERMISSION.ROLE_ID.eq(roleId)))));
    }

    /**
     * 根据用户ID查询权限列表
     *
     * @param userId 用户ID
     * @return 权限列表
     * @since 2024-05-06
     */
    default List<IamPermission> listByUserId(@NotNull String userId) {
        return selectListByQuery(QueryWrapper.create()
                .select(IAM_PERMISSION.ALL_COLUMNS)
                .from(IAM_PERMISSION)
                .where(IAM_PERMISSION.PERMISSION_STATUS.eq(StatusEnum.ENABLE))
                .and(IAM_PERMISSION.DEL_FLAG.eq(StatusEnum.ENABLE))
                .and(IAM_PERMISSION.PERMISSION_ID.in(select(IAM_POSITION_PERMISSION.PERMISSION_ID)
                        .from(IAM_POSITION_PERMISSION)
                        .where(IAM_POSITION_PERMISSION.DEL_FLAG.eq(StatusEnum.ENABLE))
                        .and(IAM_POSITION_PERMISSION.POSITION_ID.in(select(IAM_USER_POSITION.POSITION_ID)
                                .from(IAM_USER_POSITION)
                                .where(IAM_USER_POSITION.DEL_FLAG.eq(StatusEnum.ENABLE))
                                .and(IAM_USER_POSITION.USER_ID.eq(userId))))))
                .union(select(IAM_PERMISSION.ALL_COLUMNS)
                        .from(IAM_PERMISSION)
                        .where(IAM_PERMISSION.PERMISSION_STATUS.eq(StatusEnum.ENABLE))
                        .and(IAM_PERMISSION.DEL_FLAG.eq(StatusEnum.ENABLE))
                        .and(IAM_PERMISSION.PERMISSION_ID.in(select(IAM_ROLE_PERMISSION.PERMISSION_ID)
                                .from(IAM_ROLE_PERMISSION)
                                .where(IAM_ROLE_PERMISSION.DEL_FLAG.eq(StatusEnum.ENABLE))
                                .and(IAM_ROLE_PERMISSION.ROLE_ID.in(select(IAM_USER_ROLE.ROLE_ID)
                                        .from(IAM_USER_ROLE)
                                        .where(IAM_USER_ROLE.DEL_FLAG.eq(StatusEnum.ENABLE))
                                        .and(IAM_USER_ROLE.USER_ID.eq(userId))))))));
    }
}
