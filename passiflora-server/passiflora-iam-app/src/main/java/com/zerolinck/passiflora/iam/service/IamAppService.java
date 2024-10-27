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
package com.zerolinck.passiflora.iam.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zerolinck.passiflora.common.util.CurrentUtil;
import com.zerolinck.passiflora.common.util.OnlyFieldCheck;
import com.zerolinck.passiflora.common.util.QueryCondition;
import com.zerolinck.passiflora.common.util.lock.LockUtil;
import com.zerolinck.passiflora.common.util.lock.LockWrapper;
import com.zerolinck.passiflora.iam.mapper.IamAppMapper;
import com.zerolinck.passiflora.model.iam.entity.IamApp;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 应用 Service
 *
 * @author linck on 2024-09-30
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IamAppService extends ServiceImpl<IamAppMapper, IamApp> {

    private static final String LOCK_KEY = "passiflora:lock:iamApp:";

    /**
     * 分页查询
     *
     * @param condition 搜索条件
     * @since 2024-09-30
     */
    @NotNull public Page<IamApp> page(@Nullable QueryCondition<IamApp> condition) {
        condition = Objects.requireNonNullElse(condition, new QueryCondition<>());
        return baseMapper.page(
                condition.page(), condition.searchWrapper(IamApp.class), condition.sortWrapper(IamApp.class));
    }

    /**
     * 新增应用
     *
     * @param iamApp 应用
     * @since 2024-09-30
     */
    public void add(@NotNull IamApp iamApp) {
        LockUtil.lock(LOCK_KEY, new LockWrapper<>(), true, () -> {
            OnlyFieldCheck.checkInsert(baseMapper, iamApp);
            baseMapper.insert(iamApp);
        });
    }

    /**
     * 更新应用
     *
     * @param iamApp 应用
     * @since 2024-09-30
     */
    public boolean update(@NotNull IamApp iamApp) {
        return LockUtil.lock(LOCK_KEY, new LockWrapper<>(), true, () -> {
            OnlyFieldCheck.checkUpdate(baseMapper, iamApp);
            int changeRowCount = baseMapper.updateById(iamApp);
            return changeRowCount > 0;
        });
    }

    /**
     * 删除应用
     *
     * @param appIds 应用ID集合
     * @since 2024-09-30
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteByIds(@Nullable Collection<String> appIds) {
        if (CollectionUtils.isEmpty(appIds)) {
            return 0;
        }
        return baseMapper.deleteByIds(appIds, CurrentUtil.getCurrentUserId());
    }

    @Transactional(rollbackFor = Exception.class)
    public int disable(@Nullable Collection<String> appIds) {
        if (CollectionUtils.isEmpty(appIds)) {
            return 0;
        }
        return baseMapper.disable(appIds, CurrentUtil.getCurrentUserId());
    }

    @Transactional(rollbackFor = Exception.class)
    public int enable(@Nullable Collection<String> appIds) {
        if (CollectionUtils.isEmpty(appIds)) {
            return 0;
        }
        return baseMapper.enable(appIds, CurrentUtil.getCurrentUserId());
    }

    /**
     * 应用详情
     *
     * @param appId 应用ID
     * @since 2024-09-30
     */
    @NotNull public Optional<IamApp> detail(@NotNull String appId) {
        return Optional.ofNullable(baseMapper.selectById(appId));
    }
}
