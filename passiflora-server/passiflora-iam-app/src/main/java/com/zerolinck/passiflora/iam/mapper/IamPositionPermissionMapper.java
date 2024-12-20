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
import com.zerolinck.passiflora.model.iam.entity.IamPositionPermission;

/** @author linck on 2024-05-06 */
public interface IamPositionPermissionMapper extends BaseMapper<IamPositionPermission> {

    @NotNull default Page<IamPositionPermission> page(
            @NotNull IPage<IamPositionPermission> page,
            @Param(Constants.WRAPPER) QueryWrapper<IamPositionPermission> searchWrapper,
            @Param("sortWrapper") QueryWrapper<IamPositionPermission> sortWrapper) {
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

        return (Page<IamPositionPermission>) this.selectPage(page, searchWrapper);
    }

    default int deleteByIds(
            @Nullable @Param("ids") Collection<String> ids, @Nullable @Param("updateBy") String updateBy) {
        if (CollectionUtils.isEmpty(ids)) {
            return 0;
        }

        LambdaUpdateWrapper<IamPositionPermission> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper
                .in(IamPositionPermission::getId, ids)
                .set(IamPositionPermission::getUpdateTime, LocalDateTime.now())
                .set(IamPositionPermission::getUpdateBy, updateBy)
                .set(IamPositionPermission::getDelFlag, 1);

        return this.update(null, updateWrapper);
    }

    default int deleteByPositionIds(
            @Nullable @Param("positionIds") Collection<String> positionIds,
            @Nullable @Param("updateBy") String updateBy) {
        if (CollectionUtils.isEmpty(positionIds)) {
            return 0;
        }
        LambdaUpdateWrapper<IamPositionPermission> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper
                .in(IamPositionPermission::getPositionId, positionIds)
                .set(IamPositionPermission::getUpdateTime, LocalDateTime.now())
                .set(IamPositionPermission::getUpdateBy, updateBy)
                .set(IamPositionPermission::getDelFlag, 1);

        return this.update(null, updateWrapper);
    }

    @NotNull @Select(
            """
        SELECT permission_id from iam_permission
        WHERE permission_status = 1 AND del_flag = 0
        AND permission_id IN
            (SELECT permission_id from iam_position_permission
            WHERE del_flag = 0 AND position_id IN
            (SELECT position_id from iam_position_permission
            WHERE del_flag = 0 AND position_id IN (#{positionIds})))""")
    List<String> permissionIdsByPositionIds(@NotNull @Param("positionIds") List<String> positionIds);
}
