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

import java.util.*;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.zerolinck.passiflora.common.util.OnlyFieldCheck;
import com.zerolinck.passiflora.common.util.ProxyUtil;
import com.zerolinck.passiflora.common.util.QueryCondition;
import com.zerolinck.passiflora.common.util.SetUtil;
import com.zerolinck.passiflora.common.util.lock.LockUtil;
import com.zerolinck.passiflora.common.util.lock.LockWrapper;
import com.zerolinck.passiflora.iam.mapper.IamPositionPermissionMapper;
import com.zerolinck.passiflora.model.iam.args.PositionPermissionArgs;
import com.zerolinck.passiflora.model.iam.entity.IamPositionPermission;
import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

/** @author linck on 2024-05-06 */
@Slf4j
@Service
public class IamPositionPermissionService extends ServiceImpl<IamPositionPermissionMapper, IamPositionPermission> {

    private static final String LOCK_KEY = "passiflora:lock:iamPositionPermission:";

    @NotNull public Page<IamPositionPermission> page(@Nullable QueryCondition<IamPositionPermission> condition) {
        condition = Objects.requireNonNullElse(condition, new QueryCondition<>());
        return mapper.paginate(
                condition.getPageNumber(),
                condition.getPageSize(),
                condition.searchWrapper(IamPositionPermission.class));
    }

    public void add(@NotNull IamPositionPermission iamPositionPermission) {
        LockUtil.lock(LOCK_KEY, new LockWrapper<>(), true, () -> {
            OnlyFieldCheck.checkInsert(mapper, iamPositionPermission);
            mapper.insert(iamPositionPermission);
        });
    }

    public boolean update(@NotNull IamPositionPermission iamPositionPermission) {
        return LockUtil.lock(LOCK_KEY, new LockWrapper<>(), true, () -> {
            OnlyFieldCheck.checkUpdate(mapper, iamPositionPermission);
            int changeRowCount = mapper.update(iamPositionPermission);
            return changeRowCount > 0;
        });
    }

    @Transactional(rollbackFor = Exception.class)
    public int deleteByIds(@Nullable Collection<String> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return 0;
        }
        return mapper.deleteBatchByIds(ids, 500);
    }

    @SuppressWarnings("UnusedReturnValue")
    @Transactional(rollbackFor = Exception.class)
    public int deleteByPositionIds(@Nullable Collection<String> positionIds) {
        if (CollectionUtils.isEmpty(positionIds)) {
            return 0;
        }
        return mapper.deleteByPositionIds(positionIds);
    }

    @NotNull public Optional<IamPositionPermission> detail(@NotNull String id) {
        return Optional.ofNullable(mapper.selectOneById(id));
    }

    @NotNull public List<String> permissionIdsByPositionIds(@Nullable List<String> positionIds) {
        if (CollectionUtils.isEmpty(positionIds)) {
            return Collections.emptyList();
        }
        return mapper.permissionIdsByPositionIds(positionIds);
    }

    public void savePositionPermission(@NotNull PositionPermissionArgs args) {
        LockUtil.lock(
                LOCK_KEY,
                new LockWrapper<PositionPermissionArgs>()
                        .lock(PositionPermissionArgs::getPositionId, args.getPositionId()),
                true,
                () -> {
                    Set<String> exitPermissionIdSet =
                            new HashSet<>(this.permissionIdsByPositionIds(List.of(args.getPositionId())));
                    Set<String> newPermissionIdSet = new HashSet<>(args.getPermissionIds());
                    Set<String> needAdd = SetUtil.set2MoreOutSet1(exitPermissionIdSet, newPermissionIdSet);
                    Set<String> needDelete = SetUtil.set2MoreOutSet1(newPermissionIdSet, exitPermissionIdSet);
                    if (CollectionUtils.isNotEmpty(needDelete)) {
                        ProxyUtil.proxy(this.getClass()).deleteByIds(needDelete);
                    }
                    if (CollectionUtils.isNotEmpty(needAdd)) {
                        List<IamPositionPermission> addList = new ArrayList<>();
                        needAdd.forEach(permissionId -> {
                            IamPositionPermission iamPositionPermission = new IamPositionPermission();
                            iamPositionPermission.setPositionId(args.getPositionId());
                            iamPositionPermission.setPermissionId(permissionId);
                            addList.add(iamPositionPermission);
                        });
                        ProxyUtil.proxy(this.getClass()).saveBatch(addList);
                    }
                });
    }
}
