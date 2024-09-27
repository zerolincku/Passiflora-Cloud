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
import com.zerolinck.passiflora.common.util.*;
import com.zerolinck.passiflora.common.util.lock.LockUtil;
import com.zerolinck.passiflora.common.util.lock.LockWrapper;
import com.zerolinck.passiflora.iam.mapper.IamPositionPermissionMapper;
import com.zerolinck.passiflora.model.iam.args.PositionPermissionSaveArgs;
import com.zerolinck.passiflora.model.iam.entity.IamPositionPermission;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author linck
 * @since 2024-05-06
 */
@Slf4j
@Service
public class IamPositionPermissionService extends ServiceImpl<IamPositionPermissionMapper, IamPositionPermission> {

    private static final String LOCK_KEY = "passiflora:lock:iamPositionPermission:";

    @Nonnull
    public Page<IamPositionPermission> page(@Nullable QueryCondition<IamPositionPermission> condition) {
        condition = Objects.requireNonNullElse(condition, new QueryCondition<>());
        return baseMapper.page(
                condition.page(),
                condition.searchWrapper(IamPositionPermission.class),
                condition.sortWrapper(IamPositionPermission.class));
    }

    public void add(@Nonnull IamPositionPermission iamPositionPermission) {
        LockUtil.lock(LOCK_KEY, new LockWrapper<IamPositionPermission>(), true, () -> {
            OnlyFieldCheck.checkInsert(baseMapper, iamPositionPermission);
            baseMapper.insert(iamPositionPermission);
        });
    }

    public boolean update(@Nonnull IamPositionPermission iamPositionPermission) {
        return LockUtil.lock(LOCK_KEY, new LockWrapper<IamPositionPermission>(), true, () -> {
            OnlyFieldCheck.checkUpdate(baseMapper, iamPositionPermission);
            int changeRowCount = baseMapper.updateById(iamPositionPermission);
            return changeRowCount > 0;
        });
    }

    @Transactional(rollbackFor = Exception.class)
    public int deleteByIds(@Nullable Collection<String> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return 0;
        }
        return baseMapper.deleteByIds(ids, CurrentUtil.getCurrentUserId());
    }

    @SuppressWarnings("UnusedReturnValue")
    @Transactional(rollbackFor = Exception.class)
    public int deleteByPositionIds(@Nullable Collection<String> positionIds) {
        if (CollectionUtils.isEmpty(positionIds)) {
            return 0;
        }
        return baseMapper.deleteByPositionIds(positionIds, CurrentUtil.getCurrentUserId());
    }

    @Nonnull
    public Optional<IamPositionPermission> detail(@Nonnull String id) {
        return Optional.ofNullable(baseMapper.selectById(id));
    }

    @Nonnull
    public List<String> permissionIdsByPositionIds(@Nullable List<String> positionIds) {
        if (CollectionUtils.isEmpty(positionIds)) {
            return Collections.emptyList();
        }
        return baseMapper.permissionIdsByPositionIds(positionIds);
    }

    public void savePositionPermission(@Nonnull PositionPermissionSaveArgs args) {
        LockUtil.lock(
                LOCK_KEY,
                new LockWrapper<PositionPermissionSaveArgs>()
                        .lock(PositionPermissionSaveArgs::getPositionId, args.getPositionId()),
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
