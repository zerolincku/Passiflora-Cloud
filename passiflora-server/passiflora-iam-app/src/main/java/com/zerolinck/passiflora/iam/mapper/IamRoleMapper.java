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

import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.annotations.Param;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zerolinck.passiflora.model.iam.entity.IamRole;

/**
 * 角色 Mybatis Mapper
 *
 * @author 林常坤 on 2024-08-17
 */
public interface IamRoleMapper extends BaseMapper<IamRole> {

    @NotNull default Page<IamRole> page(
            @NotNull IPage<IamRole> page,
            @Param(Constants.WRAPPER) QueryWrapper<IamRole> searchWrapper,
            @Param("sortWrapper") QueryWrapper<IamRole> sortWrapper) {
        if (searchWrapper == null) {
            searchWrapper = new QueryWrapper<>();
        }
        searchWrapper.eq("del_flag", 0);

        if (sortWrapper == null
                || sortWrapper.getSqlSegment() == null
                || sortWrapper.getSqlSegment().isEmpty()) {
            searchWrapper.orderByAsc("role_id");
        } else {
            searchWrapper.last(sortWrapper.getSqlSegment());
        }

        return (Page<IamRole>) this.selectPage(page, searchWrapper);
    }

    default int deleteByIds(
            @NotNull @Param("roleIds") Collection<String> roleIds, @Nullable @Param("updateBy") String updateBy) {
        if (CollectionUtils.isEmpty(roleIds)) {
            return 0;
        }

        LambdaUpdateWrapper<IamRole> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper
                .in(IamRole::getRoleId, roleIds)
                .set(IamRole::getUpdateTime, LocalDateTime.now())
                .set(IamRole::getUpdateBy, updateBy)
                .set(IamRole::getDelFlag, 1);

        return this.update(null, updateWrapper);
    }

    default void disable(
            @NotNull @Param("roleIds") Collection<String> roleIds, @Nullable @Param("updateBy") String updateBy) {
        if (CollectionUtils.isEmpty(roleIds)) {
            return;
        }

        LambdaUpdateWrapper<IamRole> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper
                .in(IamRole::getRoleId, roleIds)
                .set(IamRole::getUpdateTime, LocalDateTime.now())
                .set(IamRole::getUpdateBy, updateBy)
                .set(IamRole::getRoleStatus, 0);

        this.update(null, updateWrapper);
    }

    default void enable(
            @NotNull @Param("roleIds") Collection<String> roleIds, @Nullable @Param("updateBy") String updateBy) {
        if (CollectionUtils.isEmpty(roleIds)) {
            return;
        }

        LambdaUpdateWrapper<IamRole> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper
                .in(IamRole::getRoleId, roleIds)
                .set(IamRole::getUpdateTime, LocalDateTime.now())
                .set(IamRole::getUpdateBy, updateBy)
                .set(IamRole::getRoleStatus, 1);

        this.update(null, updateWrapper);
    }
}
