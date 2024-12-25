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

import static com.zerolinck.passiflora.model.iam.entity.table.IamPermissionTableDef.IAM_PERMISSION;

import java.util.Collection;
import java.util.List;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.update.UpdateChain;
import com.zerolinck.passiflora.base.enums.StatusEnum;
import com.zerolinck.passiflora.model.iam.entity.IamPermission;
import com.zerolinck.passiflora.model.iam.resp.IamPermissionTableResp;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.jetbrains.annotations.NotNull;

/** @author linck on 2024-05-06 */
public interface IamPermissionMapper extends BaseMapper<IamPermission> {

    default void updateOrder(@Param("iamPermissionTableResp") IamPermissionTableResp iamPermissionTableResp) {
        UpdateChain.of(IamPermission.class)
                .set(IamPermission::getOrder, iamPermissionTableResp.getOrder())
                .eq(IamPermission::getPermissionId, iamPermissionTableResp.getPermissionId())
                .update();
    }

    default boolean disable(Collection<String> permissionIds) {
        if (CollectionUtils.isEmpty(permissionIds)) {
            return true;
        }

        return UpdateChain.of(IamPermission.class)
                .set(IamPermission::getPermissionStatus, StatusEnum.DISABLE)
                .in(IamPermission::getPermissionId, permissionIds)
                .update();
    }

    default boolean enable(Collection<String> permissionIds, String updateBy) {
        if (CollectionUtils.isEmpty(permissionIds)) {
            return true;
        }

        return UpdateChain.of(IamPermission.class)
                .set(IamPermission::getPermissionStatus, StatusEnum.ENABLE)
                .in(IamPermission::getPermissionId, permissionIds)
                .update();
    }

    default List<IamPermission> listByParentId(@Param("permissionParentId") String permissionParentId) {
        return selectListByQuery(QueryWrapper.create()
                .where(IAM_PERMISSION.PERMISSION_PARENT_ID.eq(permissionParentId))
                .orderBy(IAM_PERMISSION.PERMISSION_LEVEL, true)
                .orderBy(IAM_PERMISSION.ORDER, true)
                .orderBy(IAM_PERMISSION.PERMISSION_TITLE, true));
    }

    default List<IamPermission> listSelfAndSub(@Param("permissionId") String permissionId) {
        return selectListByQuery(QueryWrapper.create()
                .where(IAM_PERMISSION.PERMISSION_ID_PATH.like(permissionId))
                .orderBy(IAM_PERMISSION.PERMISSION_LEVEL, true)
                .orderBy(IAM_PERMISSION.ORDER, true)
                .orderBy(IAM_PERMISSION.PERMISSION_TITLE, true));
    }

    @NotNull @Select(
            """
        SELECT b.* FROM
            (SELECT position_id, permission_id FROM iam_position_permission
            WHERE del_flag = 0 AND position_id = #{positionId}) AS a
            INNER JOIN iam_permission AS b ON a.permission_id = b.permission_id
            INNER JOIN iam_position AS sp ON a.position_id = sp.position_id
            WHERE b.del_flag = 0 AND sp.del_flag = 0 AND sp.position_status = 1
            ORDER BY b.permission_level, b.\"order\", b.permission_title""")
    List<IamPermission> listByPositionId(@NotNull @Param("positionId") String positionId);

    @NotNull @Select(
            """
        SELECT b.* FROM
            (SELECT role_id, permission_id FROM iam_role_permission
            WHERE del_flag = 0 AND role_id = #{roleId}) AS a
            INNER JOIN iam_permission AS b ON a.permission_id = b.permission_id
            INNER JOIN iam_role AS sr ON a.role_id = sr.role_id
            WHERE b.del_flag = 0 AND sr.del_flag = 0 AND sr.role_status = 1
            ORDER BY b.permission_level, b.\"order\", b.permission_title""")
    List<IamPermission> listByRoleId(@NotNull @Param("roleId") String roleId);

    @NotNull @Select(
            """
        SELECT * FROM
            (SELECT d.* FROM
                (
                SELECT b.permission_id FROM
                (
                    SELECT position_id FROM iam_user_position WHERE del_flag = 0 AND user_id = #{userId}
                ) AS a
                INNER JOIN iam_position_permission AS b ON a.position_id = b.position_id
                INNER JOIN iam_position AS sp ON a.position_id = sp.position_id
                WHERE b.del_flag = 0 AND sp.del_flag = 0 AND sp.position_status = 1
                ) AS c INNER JOIN iam_permission AS d ON c.permission_id = d.permission_id
                UNION SELECT d.* FROM
                (
                SELECT b.permission_id FROM
                (SELECT role_id FROM iam_user_role WHERE del_flag = 0 AND user_id = #{userId}) AS a
                INNER JOIN iam_role_permission AS b ON a.role_id = b.role_id
                INNER JOIN iam_role AS sr ON a.role_id = sr.role_id
                WHERE b.del_flag = 0 AND sr.del_flag = 0 AND sr.role_status = 1
                ) AS c
                INNER JOIN iam_permission AS d ON c.permission_id = d.permission_id) AS e
                WHERE e.del_flag = 0 ORDER BY e.permission_level, e.\"order\", e.permission_title""")
    List<IamPermission> listByUserId(@NotNull @Param("userId") String userId);
}
