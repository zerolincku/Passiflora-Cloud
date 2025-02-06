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
import java.util.Optional;

import com.zerolinck.passiflora.common.util.lock.LockUtil;
import com.zerolinck.passiflora.common.util.lock.LockWrapper;
import com.zerolinck.passiflora.iam.mapper.IamPositionDataScopeMapper;
import com.zerolinck.passiflora.model.iam.entity.IamPositionDataScope;
import com.zerolinck.passiflora.mybatis.util.UniqueFieldCheck;
import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** @author linck on 2024-05-14 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IamPositionDataScopeService {
    private final IamPositionDataScopeMapper mapper;
    private static final String LOCK_KEY = "passiflora:lock:iamPositionDataScope:";

    /**
     * 新增职位数据范围
     *
     * @param iamPositionDataScope 职位数据范围实体
     * @since 2024-05-14
     */
    public void add(@NotNull IamPositionDataScope iamPositionDataScope) {
        LockUtil.lock(LOCK_KEY, new LockWrapper<>(), true, () -> {
            UniqueFieldCheck.checkInsert(mapper, iamPositionDataScope);
            mapper.insert(iamPositionDataScope);
        });
    }

    /**
     * 更新职位数据范围
     *
     * @param iamPositionDataScope 职位数据范围实体
     * @return 如果更新成功返回true，否则返回false
     * @since 2024-05-14
     */
    public boolean update(@NotNull IamPositionDataScope iamPositionDataScope) {
        return LockUtil.lock(LOCK_KEY, new LockWrapper<>(), true, () -> {
            UniqueFieldCheck.checkUpdate(mapper, iamPositionDataScope);
            int changeRowCount = mapper.update(iamPositionDataScope);
            return changeRowCount > 0;
        });
    }

    /**
     * 根据职位数据范围ID集合删除职位数据范围
     *
     * @param scopeIds 职位数据范围ID集合
     * @return 删除的职位数据范围数量
     * @since 2024-05-14
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteByIds(@Nullable Collection<String> scopeIds) {
        if (CollectionUtils.isEmpty(scopeIds)) {
            return 0;
        }
        return mapper.deleteBatchByIds(scopeIds, 500);
    }

    /**
     * 根据职位数据范围ID获取职位数据范围的详细信息
     *
     * @param scopeId 职位数据范围ID
     * @return 包含职位数据范围的Optional对象
     * @since 2024-05-14
     */
    @NotNull public Optional<IamPositionDataScope> detail(@NotNull String scopeId) {
        return Optional.ofNullable(mapper.selectOneById(scopeId));
    }
}
