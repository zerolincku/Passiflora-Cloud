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

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

import com.zerolinck.passiflora.common.api.Page;
import com.zerolinck.passiflora.common.util.QueryCondition;
import com.zerolinck.passiflora.common.util.lock.LockUtil;
import com.zerolinck.passiflora.common.util.lock.LockWrapper;
import com.zerolinck.passiflora.iam.mapper.IamAppMapper;
import com.zerolinck.passiflora.model.iam.entity.IamApp;
import com.zerolinck.passiflora.mybatis.util.ConditionUtils;
import com.zerolinck.passiflora.mybatis.util.OnlyFieldCheck;
import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 应用 Service
 *
 * @author linck on 2024-09-30
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IamAppService {
    private final IamAppMapper mapper;
    private static final String LOCK_KEY = "passiflora:lock:iamApp:";

    /**
     * 分页查询IAM应用
     *
     * @param condition 查询条件
     * @return IAM应用的分页结果
     * @since 2024-09-30
     */
    @NotNull public Page<IamApp> page(@Nullable QueryCondition<IamApp> condition) {
        condition = Objects.requireNonNullElse(condition, new QueryCondition<>());
        return mapper.page(
                condition.getPageNum(), condition.getPageSize(), ConditionUtils.searchWrapper(condition, IamApp.class));
    }

    /**
     * 新增IAM应用
     *
     * @param iamApp IAM应用
     * @since 2024-09-30
     */
    public void add(@NotNull IamApp iamApp) {
        LockUtil.lock(LOCK_KEY, new LockWrapper<>(), true, () -> {
            OnlyFieldCheck.checkInsert(mapper, iamApp);
            mapper.insert(iamApp);
        });
    }

    /**
     * 更新IAM应用
     *
     * @param iamApp IAM应用
     * @return 如果更新成功返回true，否则返回false
     * @since 2024-09-30
     */
    public boolean update(@NotNull IamApp iamApp) {
        return LockUtil.lock(LOCK_KEY, new LockWrapper<>(), true, () -> {
            OnlyFieldCheck.checkUpdate(mapper, iamApp);
            int changeRowCount = mapper.update(iamApp);
            return changeRowCount > 0;
        });
    }

    /**
     * 根据应用ID集合删除IAM应用
     *
     * @param appIds 应用ID集合
     * @return 删除的应用数量
     * @since 2024-09-30
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteByIds(@Nullable Collection<String> appIds) {
        if (CollectionUtils.isEmpty(appIds)) {
            return 0;
        }
        return mapper.deleteBatchByIds(appIds, 500);
    }

    /**
     * 禁用IAM应用
     *
     * @param appIds 应用ID集合
     * @return 如果禁用成功返回true，否则返回false
     * @since 2024-09-30
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean disable(@Nullable Collection<String> appIds) {
        return mapper.disable(appIds);
    }

    /**
     * 启用IAM应用
     *
     * @param appIds 应用ID集合
     * @return 如果启用成功返回true，否则返回false
     * @since 2024-09-30
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean enable(@Nullable Collection<String> appIds) {
        return mapper.enable(appIds);
    }

    /**
     * 根据应用ID获取IAM应用的详细信息
     *
     * @param appId 应用ID
     * @return 包含IAM应用的Optional对象
     * @since 2024-09-30
     */
    @NotNull public Optional<IamApp> detail(@NotNull String appId) {
        return Optional.ofNullable(mapper.selectOneById(appId));
    }
}
