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
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.update.UpdateChain;
import com.zerolinck.passiflora.base.enums.StatusEnum;
import com.zerolinck.passiflora.model.iam.entity.IamRole;
import com.zerolinck.passiflora.mybatis.util.FlexPage;
import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;

/**
 * 角色 Mybatis Mapper
 *
 * @author 林常坤
 * @since 2024-08-17
 */
public interface IamRoleMapper extends BaseMapper<IamRole> {

    /**
     * 分页查询
     *
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param queryWrapper 查询条件
     * @author 林常坤 on 2025/1/24
     */
    default com.zerolinck.passiflora.common.api.Page<IamRole> page(
            Number pageNum, Number pageSize, QueryWrapper queryWrapper) {
        Page<IamRole> paginate = paginate(pageNum, pageSize, queryWrapper);
        return FlexPage.convert(paginate);
    }

    /**
     * 禁用角色
     *
     * @param roleIds 角色ID集合
     * @return 如果禁用成功返回true，否则返回false
     * @since 2024-08-17
     */
    default boolean disable(@NotNull Collection<String> roleIds) {
        if (CollectionUtils.isEmpty(roleIds)) {
            return true;
        }

        return UpdateChain.of(IamRole.class)
                .set(IamRole::getRoleStatus, StatusEnum.DISABLE)
                .in(IamRole::getRoleId, roleIds)
                .update();
    }

    /**
     * 启用角色
     *
     * @param roleIds 角色ID集合
     * @return 如果启用成功返回true，否则返回false
     * @since 2024-08-17
     */
    default boolean enable(@NotNull Collection<String> roleIds) {
        if (CollectionUtils.isEmpty(roleIds)) {
            return true;
        }

        return UpdateChain.of(IamRole.class)
                .set(IamRole::getRoleStatus, StatusEnum.ENABLE)
                .in(IamRole::getRoleId, roleIds)
                .update();
    }
}
