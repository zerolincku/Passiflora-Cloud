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

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.zerolinck.passiflora.common.util.OnlyFieldCheck;
import com.zerolinck.passiflora.common.util.QueryCondition;
import com.zerolinck.passiflora.common.util.lock.LockUtil;
import com.zerolinck.passiflora.common.util.lock.LockWrapper;
import com.zerolinck.passiflora.iam.mapper.IamPositionDataScopeMapper;
import com.zerolinck.passiflora.model.iam.entity.IamPositionDataScope;
import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

/** @author linck on 2024-05-14 */
@Slf4j
@Service
public class IamPositionDataScopeService extends ServiceImpl<IamPositionDataScopeMapper, IamPositionDataScope> {

    private static final String LOCK_KEY = "passiflora:lock:iamPositionDataScope:";

    @NotNull public Page<IamPositionDataScope> page(@Nullable QueryCondition<IamPositionDataScope> condition) {
        condition = Objects.requireNonNullElse(condition, new QueryCondition<>());
        return mapper.paginate(
                condition.getPageNumber(),
                condition.getPageSize(),
                condition.searchWrapper(IamPositionDataScope.class));
    }

    public void add(@NotNull IamPositionDataScope iamPositionDataScope) {
        LockUtil.lock(LOCK_KEY, new LockWrapper<>(), true, () -> {
            OnlyFieldCheck.checkInsert(mapper, iamPositionDataScope);
            mapper.insert(iamPositionDataScope);
        });
    }

    public boolean update(@NotNull IamPositionDataScope iamPositionDataScope) {
        return LockUtil.lock(LOCK_KEY, new LockWrapper<>(), true, () -> {
            OnlyFieldCheck.checkUpdate(mapper, iamPositionDataScope);
            int changeRowCount = mapper.update(iamPositionDataScope);
            return changeRowCount > 0;
        });
    }

    @Transactional(rollbackFor = Exception.class)
    public int deleteByIds(@Nullable Collection<String> scopeIds) {
        if (CollectionUtils.isEmpty(scopeIds)) {
            return 0;
        }
        return mapper.deleteBatchByIds(scopeIds, 500);
    }

    @NotNull public Optional<IamPositionDataScope> detail(@NotNull String scopeId) {
        return Optional.ofNullable(mapper.selectOneById(scopeId));
    }
}
