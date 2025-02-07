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

import com.zerolinck.passiflora.common.util.ListUtils;
import com.zerolinck.passiflora.common.util.ProxyUtils;
import com.zerolinck.passiflora.common.util.SetUtils;
import com.zerolinck.passiflora.common.util.lock.LockUtils;
import com.zerolinck.passiflora.common.util.lock.LockWrapper;
import com.zerolinck.passiflora.iam.mapper.IamPositionPermissionMapper;
import com.zerolinck.passiflora.model.iam.args.PositionPermissionArgs;
import com.zerolinck.passiflora.model.iam.entity.IamPositionPermission;
import com.zerolinck.passiflora.mybatis.util.UniqueFieldChecker;
import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * IAM职位权限服务类
 *
 * @author linck
 * @since 2024-05-06
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IamPositionPermissionService {
    private final IamPositionPermissionMapper mapper;
    private static final String LOCK_KEY = "passiflora:lock:iamPositionPermission:";

    /**
     * 新增职位权限
     *
     * @param iamPositionPermission 职位权限实体
     * @since 2024-05-06
     */
    public void add(@NotNull IamPositionPermission iamPositionPermission) {
        LockUtils.lock(LOCK_KEY, new LockWrapper<>(), true, () -> {
            UniqueFieldChecker.checkInsert(mapper, iamPositionPermission);
            mapper.insert(iamPositionPermission);
        });
    }

    /**
     * 更新职位权限
     *
     * @param iamPositionPermission 职位权限实体
     * @return 如果更新成功返回true，否则返回false
     * @since 2024-05-06
     */
    public boolean update(@NotNull IamPositionPermission iamPositionPermission) {
        return LockUtils.lock(LOCK_KEY, new LockWrapper<>(), true, () -> {
            UniqueFieldChecker.checkUpdate(mapper, iamPositionPermission);
            int changeRowCount = mapper.update(iamPositionPermission);
            return changeRowCount > 0;
        });
    }

    /**
     * 根据职位权限ID集合删除职位权限
     *
     * @param ids 职位权限ID集合
     * @return 删除的职位权限数量
     * @since 2024-05-06
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteByIds(@Nullable Collection<String> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return 0;
        }
        return mapper.deleteBatchByIds(ids, 500);
    }

    /**
     * 根据职位ID集合删除职位权限
     *
     * @param positionIds 职位ID集合
     * @return 删除的职位权限数量
     * @since 2024-05-06
     */
    @SuppressWarnings({"UnusedReturnValue", "unused"})
    @Transactional(rollbackFor = Exception.class)
    public int deleteByPositionIds(@Nullable Collection<String> positionIds) {
        if (CollectionUtils.isEmpty(positionIds)) {
            return 0;
        }
        return mapper.deleteByPositionIds(positionIds);
    }

    /**
     * 根据职位权限ID获取职位权限的详细信息
     *
     * @param id 职位权限ID
     * @return 包含职位权限的Optional对象
     * @since 2024-05-06
     */
    @NotNull public Optional<IamPositionPermission> detail(@NotNull String id) {
        return Optional.ofNullable(mapper.selectOneById(id));
    }

    /**
     * 根据职位ID集合获取权限ID集合
     *
     * @param positionIds 职位ID集合
     * @return 权限ID集合
     * @since 2024-05-06
     */
    @NotNull public List<String> permissionIdsByPositionIds(@Nullable List<String> positionIds) {
        if (CollectionUtils.isEmpty(positionIds)) {
            return ListUtils.emptyList();
        }
        return mapper.permissionIdsByPositionIds(positionIds);
    }

    /**
     * 保存职位权限
     *
     * @param args 职位权限参数
     * @since 2024-05-06
     */
    public void savePositionPermission(@NotNull PositionPermissionArgs args) {
        LockUtils.lock(
                LOCK_KEY,
                new LockWrapper<PositionPermissionArgs>()
                        .lock(PositionPermissionArgs::getPositionId, args.getPositionId()),
                true,
                () -> {
                    Set<String> exitPermissionIdSet =
                            new HashSet<>(this.permissionIdsByPositionIds(List.of(args.getPositionId())));
                    Set<String> newPermissionIdSet = new HashSet<>(args.getPermissionIds());
                    Set<String> needAdd = SetUtils.set2MoreOutSet1(exitPermissionIdSet, newPermissionIdSet);
                    Set<String> needDelete = SetUtils.set2MoreOutSet1(newPermissionIdSet, exitPermissionIdSet);
                    if (CollectionUtils.isNotEmpty(needDelete)) {
                        ProxyUtils.proxy(this.getClass()).deleteByIds(needDelete);
                    }
                    if (CollectionUtils.isNotEmpty(needAdd)) {
                        List<IamPositionPermission> addList = new ArrayList<>();
                        needAdd.forEach(permissionId -> {
                            IamPositionPermission iamPositionPermission = new IamPositionPermission();
                            iamPositionPermission.setPositionId(args.getPositionId());
                            iamPositionPermission.setPermissionId(permissionId);
                            addList.add(iamPositionPermission);
                        });
                        mapper.insertBatch(addList, 500);
                    }
                });
    }
}
