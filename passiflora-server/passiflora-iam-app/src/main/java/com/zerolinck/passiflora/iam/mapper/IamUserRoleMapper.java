/* 
 * Copyright (C) 2025 Linck. <zerolinck@foxmail.com>
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
import com.zerolinck.passiflora.base.enums.DelFlagEnum;
import com.zerolinck.passiflora.base.enums.StatusEnum;
import com.zerolinck.passiflora.model.iam.entity.IamUserRole;
import com.zerolinck.passiflora.model.iam.entity.table.IamRoleTableDef;
import com.zerolinck.passiflora.model.iam.entity.table.IamUserRoleTableDef;
import com.zerolinck.passiflora.model.iam.resp.IamUserRoleResp;
import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;

/**
 * 用户角色绑定 Mybatis Mapper
 *
 * @author 林常坤
 * @since 2024-08-17
 */
public interface IamUserRoleMapper extends BaseMapper<IamUserRole> {

    /**
     * 根据用户ID集合删除用户角色绑定
     *
     * @param userIds 用户ID集合
     * @return 删除的用户角色绑定数量
     * @since 2024-08-17
     */
    default int deleteByUserIds(@NotNull Collection<String> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return 0;
        }

        return this.deleteByQuery(new QueryWrapper().in(IamUserRole::getUserId, userIds));
    }

    /**
     * 根据用户ID集合查询用户角色响应对象列表
     *
     * @param userIds 用户ID集合
     * @return 用户角色响应对象列表
     * @since 2024-08-17
     */
    default List<IamUserRoleResp> selectByUserIds(@NotNull Collection<String> userIds) {
        IamRoleTableDef r = IamRoleTableDef.IAM_ROLE.as("r");
        IamUserRoleTableDef ur = IamUserRoleTableDef.IAM_USER_ROLE.as("ur");

        return selectListByQueryAs(
                QueryWrapper.create()
                        .select(ur.USER_ID, r.ROLE_ID, r.ROLE_NAME)
                        .from(r)
                        .innerJoin(ur)
                        .on(ur.ROLE_ID.eq(r.ROLE_ID))
                        .where(r.DEL_FLAG.eq(DelFlagEnum.NOT_DELETE))
                        .where(r.ROLE_STATUS.eq(StatusEnum.ENABLE))
                        .and(ur.USER_ID.in(userIds)),
                IamUserRoleResp.class);
    }

    /**
     * 根据用户ID和角色ID集合删除用户角色绑定
     *
     * @param userId 用户ID
     * @param roleIds 角色ID集合
     * @return 删除的用户角色绑定数量
     * @since 2024-08-17
     */
    default int deleteByUserIdAndRoleIds(@NotNull String userId, @NotNull Collection<String> roleIds) {
        if (CollectionUtils.isEmpty(roleIds)) {
            return 0;
        }

        return this.deleteByQuery(
                new QueryWrapper().eq(IamUserRole::getUserId, userId).in(IamUserRole::getRoleId, roleIds));
    }
}
