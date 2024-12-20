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

import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zerolinck.passiflora.model.iam.entity.IamUserRole;
import com.zerolinck.passiflora.model.iam.resp.IamUserRoleResp;

/**
 * 用户角色绑定 Mybatis Mapper
 *
 * @author 林常坤 on 2024-08-17
 */
public interface IamUserRoleMapper extends BaseMapper<IamUserRole> {

    @NotNull default Page<IamUserRole> page(
            @NotNull IPage<IamUserRole> page,
            @NotNull @Param(Constants.WRAPPER) QueryWrapper<IamUserRole> searchWrapper,
            @NotNull @Param("sortWrapper") QueryWrapper<IamUserRole> sortWrapper) {
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

        return (Page<IamUserRole>) this.selectPage(page, searchWrapper);
    }

    default int deleteByIds(
            @NotNull @Param("ids") Collection<String> ids, @Nullable @Param("updateBy") String updateBy) {
        if (CollectionUtils.isEmpty(ids)) {
            return 0;
        }

        LambdaUpdateWrapper<IamUserRole> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper
                .in(IamUserRole::getId, ids)
                .set(IamUserRole::getUpdateTime, LocalDateTime.now())
                .set(IamUserRole::getUpdateBy, updateBy)
                .set(IamUserRole::getDelFlag, 1);

        return this.update(null, updateWrapper);
    }

    default int deleteByUserIds(
            @NotNull @Param("userIds") Collection<String> userIds, @Nullable @Param("updateBy") String updateBy) {
        if (CollectionUtils.isEmpty(userIds)) {
            return 0;
        }

        LambdaUpdateWrapper<IamUserRole> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper
                .in(IamUserRole::getUserId, userIds)
                .set(IamUserRole::getUpdateTime, LocalDateTime.now())
                .set(IamUserRole::getUpdateBy, updateBy)
                .set(IamUserRole::getDelFlag, 1);

        return this.update(null, updateWrapper);
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
            @NotNull @Param("userId") String userId,
            @NotNull @Param("roleIds") Collection<String> roleIds,
            @Nullable @Param("updateBy") String updateBy) {
        if (CollectionUtils.isEmpty(roleIds)) {
            return 0;
        }

        LambdaUpdateWrapper<IamUserRole> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper
                .eq(IamUserRole::getUserId, userId)
                .in(IamUserRole::getRoleId, roleIds)
                .set(IamUserRole::getUpdateTime, LocalDateTime.now())
                .set(IamUserRole::getUpdateBy, updateBy)
                .set(IamUserRole::getDelFlag, 1);

        return this.update(null, updateWrapper);
    }
}
