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
import com.zerolinck.passiflora.model.iam.entity.IamUserRole;
import com.zerolinck.passiflora.model.iam.resp.IamUserRoleResp;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.jetbrains.annotations.NotNull;

/**
 * 用户角色绑定 Mybatis Mapper
 *
 * @author 林常坤 on 2024-08-17
 */
public interface IamUserRoleMapper extends BaseMapper<IamUserRole> {

    default int deleteByUserIds(@NotNull @Param("userIds") Collection<String> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return 0;
        }

        return this.deleteByQuery(new QueryWrapper().in(IamUserRole::getUserId, userIds));
    }

    @NotNull @Select(
            """
        SELECT a.user_id, b.role_id, b.role_name FROM
        (SELECT user_id, role_id
        FROM iam_user_role
        WHERE del_flag = 0 AND user_id IN
        <foreach item="id" index="index" collection="userIds" open="(" separator="," close=")">
            #{id}
        </foreach>
        ) as a
        INNER JOIN iam_role as b ON a.role_id = b.role_id
        WHERE b.del_flag = 0 AND b.role_status = 1
    """)
    List<IamUserRoleResp> selectByUserIds(@NotNull @Param("userIds") Collection<String> userIds);

    default int deleteByUserIdAndRoleIds(
            @NotNull @Param("userId") String userId, @NotNull @Param("roleIds") Collection<String> roleIds) {
        if (CollectionUtils.isEmpty(roleIds)) {
            return 0;
        }

        return this.deleteByQuery(
                new QueryWrapper().eq(IamUserRole::getUserId, userId).in(IamUserRole::getRoleId, roleIds));
    }
}
