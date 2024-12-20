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
import com.zerolinck.passiflora.model.iam.entity.IamUser;

/** @author linck on 2024-02-07 */
public interface IamUserMapper extends BaseMapper<IamUser> {

    @NotNull default Page<IamUser> page(
            @Param("orgId") String orgId,
            @NotNull IPage<IamUser> page,
            @Param(Constants.WRAPPER) QueryWrapper<IamUser> searchWrapper,
            @Param("sortWrapper") QueryWrapper<IamUser> sortWrapper) {
        if (searchWrapper == null) {
            searchWrapper = new QueryWrapper<>();
        }
        searchWrapper.eq("del_flag", 0).ne("user_id", "1");

        if (orgId != null && !orgId.isEmpty()) {
            searchWrapper.inSql("org_id", "SELECT org_id FROM iam_org WHERE org_id_path LIKE '%" + orgId + "%'");
        }

        if (sortWrapper == null
                || sortWrapper.getSqlSegment() == null
                || sortWrapper.getSqlSegment().isEmpty()) {
            searchWrapper.orderByAsc("user_name", "user_id");
        } else {
            searchWrapper.last(sortWrapper.getSqlSegment());
        }

        return (Page<IamUser>) this.selectPage(page, searchWrapper);
    }

    default int deleteByIds(
            @Nullable @Param("userIds") Collection<String> userIds, @Nullable @Param("updateBy") String updateBy) {
        if (CollectionUtils.isEmpty(userIds)) {
            return 0;
        }

        LambdaUpdateWrapper<IamUser> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper
                .in(IamUser::getUserId, userIds)
                .set(IamUser::getUpdateTime, LocalDateTime.now())
                .set(IamUser::getUpdateBy, updateBy)
                .set(IamUser::getDelFlag, 1);

        return this.update(null, updateWrapper);
    }
}
