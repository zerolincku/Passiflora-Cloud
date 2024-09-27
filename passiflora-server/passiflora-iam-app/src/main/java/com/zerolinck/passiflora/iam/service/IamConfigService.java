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
import com.zerolinck.passiflora.iam.mapper.IamConfigMapper;
import com.zerolinck.passiflora.model.iam.entity.IamConfig;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 系统配置 Service
 *
 * @author 林常坤
 * @since 2024-08-24
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IamConfigService extends ServiceImpl<IamConfigMapper, IamConfig> {

    private static final String LOCK_KEY = "passiflora:lock:iamConfig:";

    /**
     * 分页查询
     *
     * @param condition 搜索条件
     * @since 2024-08-24
     */
    @Nonnull
    public Page<IamConfig> page(@Nullable QueryCondition<IamConfig> condition) {
        condition = Objects.requireNonNullElse(condition, new QueryCondition<>());
        return baseMapper.page(
                condition.page(), condition.searchWrapper(IamConfig.class), condition.sortWrapper(IamConfig.class));
    }

    /**
     * 列表查询
     *
     * @param condition 搜索条件
     * @since 2024-08-24
     */
    @Nonnull
    public List<IamConfig> list(@Nullable QueryCondition<IamConfig> condition) {
        condition = Objects.requireNonNullElse(condition, new QueryCondition<>());
        return baseMapper.list(condition.searchWrapper(IamConfig.class), condition.sortWrapper(IamConfig.class));
    }

    /**
     * 新增系统配置
     *
     * @param iamConfig 系统配置
     * @since 2024-08-24
     */
    public void add(@Nonnull IamConfig iamConfig) {
        LockUtil.lock(LOCK_KEY, new LockWrapper<IamConfig>(), true, () -> {
            OnlyFieldCheck.checkInsert(baseMapper, iamConfig);
            baseMapper.insert(iamConfig);
        });
    }

    /**
     * 更新系统配置
     *
     * @param iamConfig 系统配置
     * @since 2024-08-24
     */
    public boolean update(@Nonnull IamConfig iamConfig) {
        return LockUtil.lock(LOCK_KEY, new LockWrapper<IamConfig>(), true, () -> {
            OnlyFieldCheck.checkUpdate(baseMapper, iamConfig);
            int changeRowCount = baseMapper.updateById(iamConfig);
            return changeRowCount > 0;
        });
    }

    /**
     * 删除系统配置
     *
     * @param configIds 系统配置ID集合
     * @since 2024-08-24
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteByIds(@Nullable Collection<String> configIds) {
        if (CollectionUtils.isEmpty(configIds)) {
            return 0;
        }
        return baseMapper.deleteByIds(configIds, CurrentUtil.getCurrentUserId());
    }

    /**
     * 系统配置详情
     *
     * @param configId 系统配置ID
     * @since 2024-08-24
     */
    @Nonnull
    public Optional<IamConfig> detail(@Nonnull String configId) {
        return Optional.ofNullable(baseMapper.selectById(configId));
    }
}