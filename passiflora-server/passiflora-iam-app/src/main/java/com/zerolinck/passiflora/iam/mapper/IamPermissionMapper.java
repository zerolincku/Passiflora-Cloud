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

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.jetbrains.annotations.NotNull;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zerolinck.passiflora.model.iam.entity.IamPermission;
import com.zerolinck.passiflora.model.iam.resp.IamPermissionTableResp;

/** @author linck on 2024-05-06 */
public interface IamPermissionMapper extends BaseMapper<IamPermission> {

    @NotNull default Page<IamPermission> page(
            IPage<IamPermission> page,
            @Param(Constants.WRAPPER) QueryWrapper<IamPermission> searchWrapper,
            @Param("sortWrapper") QueryWrapper<IamPermission> sortWrapper) {
        if (searchWrapper == null) {
            searchWrapper = new QueryWrapper<>();
        }
        searchWrapper.eq("del_flag", 0);

        if (sortWrapper == null
                || sortWrapper.getSqlSegment() == null
                || sortWrapper.getSqlSegment().isEmpty()) {
            searchWrapper.orderByAsc("permission_level", "\"order\"", "permission_title");
        } else {
            searchWrapper.last(sortWrapper.getSqlSegment());
        }

        return (Page<IamPermission>) this.selectPage(page, searchWrapper);
    }

    default int deleteByIds(Collection<String> permissionIds, String updateBy) {
        if (permissionIds == null || permissionIds.isEmpty()) {
            return 0;
        }

        LambdaUpdateWrapper<IamPermission> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper
                .in(IamPermission::getPermissionId, permissionIds)
                .set(IamPermission::getUpdateTime, LocalDateTime.now())
                .set(IamPermission::getUpdateBy, updateBy)
                .set(IamPermission::getDelFlag, 1);

        return this.update(null, updateWrapper);
    }

    @Update(
            """
        UPDATE iam_permission SET \"order\" = #{iamPermissionTableResp.order}
        WHERE permission_id = #{iamPermissionTableResp.permissionId}""")
    void updateOrder(@Param("iamPermissionTableResp") IamPermissionTableResp iamPermissionTableResp);

    default void disable(Collection<String> permissionIds, String updateBy) {
        if (permissionIds == null || permissionIds.isEmpty()) {
            return;
        }

        LambdaUpdateWrapper<IamPermission> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper
                .in(IamPermission::getPermissionIdPath, permissionIds)
                .set(IamPermission::getUpdateTime, LocalDateTime.now())
                .set(IamPermission::getUpdateBy, updateBy)
                .set(IamPermission::getPermissionStatus, 0);

        this.update(null, updateWrapper);
    }

    default void enable(Collection<String> permissionIds, String updateBy) {
        if (permissionIds == null || permissionIds.isEmpty()) {
            return;
        }

        LambdaUpdateWrapper<IamPermission> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper
                .in(IamPermission::getPermissionId, permissionIds)
                .set(IamPermission::getUpdateTime, LocalDateTime.now())
                .set(IamPermission::getUpdateBy, updateBy)
                .set(IamPermission::getPermissionStatus, 1);

        this.update(null, updateWrapper);
    }

    @NotNull @Select(
            """
        SELECT * FROM iam_permission WHERE del_flag = 0 AND permission_parent_id = #{permissionParentId}
        ORDER BY permission_level , \"order\", permission_title""")
    List<IamPermission> listByParentId(@Param("permissionParentId") String permissionParentId);

    @NotNull @Select(
            """
        SELECT * FROM iam_permission
        WHERE del_flag = 0 AND permission_id_path like concat('%', #{permissionId}, '%')
        ORDER BY permission_level , \"order\", permission_title""")
    List<IamPermission> listSelfAndSub(@Param("permissionId") String permissionId);

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
