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
import com.zerolinck.passiflora.model.iam.entity.IamApp;

/**
 * 应用 Mybatis Mapper
 *
 * @author linck on 2024-09-30
 */
public interface IamAppMapper extends BaseMapper<IamApp> {

    @NotNull default Page<IamApp> page(
            @NotNull IPage<IamApp> page,
            @Param(Constants.WRAPPER) QueryWrapper<IamApp> searchWrapper,
            @Param("sortWrapper") QueryWrapper<IamApp> sortWrapper) {
        if (searchWrapper == null) {
            searchWrapper = new QueryWrapper<>();
        }
        searchWrapper.eq("del_flag", 0);

        if (sortWrapper == null
                || sortWrapper.getSqlSegment() == null
                || sortWrapper.getSqlSegment().isEmpty()) {
            searchWrapper.orderByAsc("app_id");
        } else {
            searchWrapper.last(sortWrapper.getSqlSegment());
        }

        return (Page<IamApp>) this.selectPage(page, searchWrapper);
    }

    default int deleteByIds(
            @NotNull @Param("appIds") Collection<String> appIds, @Nullable @Param("updateBy") String updateBy) {
        if (CollectionUtils.isEmpty(appIds)) {
            return 0;
        }

        LambdaUpdateWrapper<IamApp> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper
                .in(IamApp::getAppId, appIds)
                .set(IamApp::getUpdateTime, LocalDateTime.now())
                .set(IamApp::getUpdateBy, updateBy)
                .set(IamApp::getDelFlag, 1);

        return this.update(null, updateWrapper);
    }

    default int disable(
            @NotNull @Param("appIds") Collection<String> appIds, @Nullable @Param("updateBy") String updateBy) {
        if (CollectionUtils.isEmpty(appIds)) {
            return 0;
        }

        LambdaUpdateWrapper<IamApp> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper
                .in(IamApp::getAppId, appIds)
                .set(IamApp::getUpdateTime, LocalDateTime.now())
                .set(IamApp::getUpdateBy, updateBy)
                .set(IamApp::getAppStatus, 0);

        return this.update(null, updateWrapper);
    }

    default int enable(
            @NotNull @Param("appIds") Collection<String> appIds, @Nullable @Param("updateBy") String updateBy) {
        if (CollectionUtils.isEmpty(appIds)) {
            return 0;
        }

        LambdaUpdateWrapper<IamApp> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper
                .in(IamApp::getAppId, appIds)
                .set(IamApp::getUpdateTime, LocalDateTime.now())
                .set(IamApp::getUpdateBy, updateBy)
                .set(IamApp::getAppStatus, 1);

        return this.update(null, updateWrapper);
    }
}
