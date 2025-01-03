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

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.update.UpdateChain;
import com.zerolinck.passiflora.base.enums.StatusEnum;
import com.zerolinck.passiflora.model.iam.entity.IamRole;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.annotations.Param;
import org.jetbrains.annotations.NotNull;

/**
 * 角色 Mybatis Mapper
 *
 * @author 林常坤 on 2024-08-17
 */
public interface IamRoleMapper extends BaseMapper<IamRole> {

    default boolean disable(@NotNull @Param("roleIds") Collection<String> roleIds) {
        if (CollectionUtils.isEmpty(roleIds)) {
            return true;
        }

        return UpdateChain.of(IamRole.class)
                .set(IamRole::getRoleStatus, StatusEnum.DISABLE)
                .in(IamRole::getRoleId, roleIds)
                .update();
    }

    default boolean enable(@NotNull @Param("roleIds") Collection<String> roleIds) {
        if (CollectionUtils.isEmpty(roleIds)) {
            return true;
        }

        return UpdateChain.of(IamRole.class)
                .set(IamRole::getRoleStatus, StatusEnum.ENABLE)
                .in(IamRole::getRoleId, roleIds)
                .update();
    }
}
