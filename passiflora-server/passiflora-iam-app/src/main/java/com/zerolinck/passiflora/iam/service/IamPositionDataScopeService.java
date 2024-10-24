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
import com.zerolinck.passiflora.iam.mapper.IamPositionDataScopeMapper;
import com.zerolinck.passiflora.model.iam.entity.IamPositionDataScope;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** @author linck on 2024-05-14 */
@Slf4j
@Service
public class IamPositionDataScopeService extends ServiceImpl<IamPositionDataScopeMapper, IamPositionDataScope> {

    private static final String LOCK_KEY = "passiflora:lock:iamPositionDataScope:";

    @Nonnull
    public Page<IamPositionDataScope> page(@Nullable QueryCondition<IamPositionDataScope> condition) {
        condition = Objects.requireNonNullElse(condition, new QueryCondition<>());
        return baseMapper.page(
                condition.page(),
                condition.searchWrapper(IamPositionDataScope.class),
                condition.sortWrapper(IamPositionDataScope.class));
    }

    public void add(@Nonnull IamPositionDataScope iamPositionDataScope) {
        LockUtil.lock(LOCK_KEY, new LockWrapper<>(), true, () -> {
            OnlyFieldCheck.checkInsert(baseMapper, iamPositionDataScope);
            baseMapper.insert(iamPositionDataScope);
        });
    }

    public boolean update(@Nonnull IamPositionDataScope iamPositionDataScope) {
        return LockUtil.lock(LOCK_KEY, new LockWrapper<>(), true, () -> {
            OnlyFieldCheck.checkUpdate(baseMapper, iamPositionDataScope);
            int changeRowCount = baseMapper.updateById(iamPositionDataScope);
            return changeRowCount > 0;
        });
    }

    @Transactional(rollbackFor = Exception.class)
    public int deleteByIds(@Nullable Collection<String> scopeIds) {
        if (CollectionUtils.isEmpty(scopeIds)) {
            return 0;
        }
        return baseMapper.deleteByIds(scopeIds, CurrentUtil.getCurrentUserId());
    }

    @Nonnull
    public Optional<IamPositionDataScope> detail(@Nonnull String scopeId) {
        return Optional.ofNullable(baseMapper.selectById(scopeId));
    }
}
