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
import com.zerolinck.passiflora.model.iam.entity.IamApp;
import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.Nullable;

/**
 * 应用 Mybatis Mapper
 *
 * @author linck
 * @since 2024-09-30
 */
public interface IamAppMapper extends BaseMapper<IamApp> {

    /**
     * 禁用应用
     *
     * @param appIds 应用ID集合
     * @return 如果禁用成功返回true，否则返回false
     * @since 2024-09-30
     */
    default boolean disable(@Nullable Collection<String> appIds) {
        if (CollectionUtils.isEmpty(appIds)) {
            return true;
        }

        return UpdateChain.of(IamApp.class)
                .set(IamApp::getAppStatus, StatusEnum.DISABLE)
                .in(IamApp::getAppId, appIds)
                .update();
    }

    /**
     * 启用应用
     *
     * @param appIds 应用ID集合
     * @return 如果启用成功返回true，否则返回false
     * @since 2024-09-30
     */
    default boolean enable(@Nullable Collection<String> appIds) {
        if (CollectionUtils.isEmpty(appIds)) {
            return true;
        }

        return UpdateChain.of(IamApp.class)
                .set(IamApp::getAppStatus, StatusEnum.ENABLE)
                .in(IamApp::getAppId, appIds)
                .update();
    }
}
