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

import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zerolinck.passiflora.common.util.*;
import com.zerolinck.passiflora.common.util.lock.LockUtil;
import com.zerolinck.passiflora.common.util.lock.LockWrapper;
import com.zerolinck.passiflora.iam.mapper.IamRolePermissionMapper;
import com.zerolinck.passiflora.model.iam.args.RolePermissionArgs;
import com.zerolinck.passiflora.model.iam.entity.IamRolePermission;

import lombok.extern.slf4j.Slf4j;

/**
 * 角色权限 Service
 *
 * @author 林常坤 on 2024-08-17
 */
@Slf4j
@Service
public class IamRolePermissionService extends ServiceImpl<IamRolePermissionMapper, IamRolePermission> {

    private static final String LOCK_KEY = "passiflora:lock:iamRolePermission:";

    /**
     * 分页查询
     *
     * @param condition 搜索条件
     * @since 2024-08-17
     */
    @NotNull public Page<IamRolePermission> page(@Nullable QueryCondition<IamRolePermission> condition) {
        condition = Objects.requireNonNullElse(condition, new QueryCondition<>());
        return baseMapper.page(
                condition.page(),
                condition.searchWrapper(IamRolePermission.class),
                condition.sortWrapper(IamRolePermission.class));
    }

    /**
     * 新增角色权限
     *
     * @param iamRolePermission 角色权限
     * @since 2024-08-17
     */
    public void add(@NotNull IamRolePermission iamRolePermission) {
        LockUtil.lock(LOCK_KEY, new LockWrapper<>(), true, () -> {
            OnlyFieldCheck.checkInsert(baseMapper, iamRolePermission);
            baseMapper.insert(iamRolePermission);
        });
    }

    /**
     * 更新角色权限
     *
     * @param iamRolePermission 角色权限
     * @since 2024-08-17
     */
    public boolean update(@NotNull IamRolePermission iamRolePermission) {
        return LockUtil.lock(LOCK_KEY, new LockWrapper<>(), true, () -> {
            OnlyFieldCheck.checkUpdate(baseMapper, iamRolePermission);
            int changeRowCount = baseMapper.updateById(iamRolePermission);
            return changeRowCount > 0;
        });
    }

    /**
     * 删除角色权限
     *
     * @param ids 角色权限ID集合
     * @since 2024-08-17
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteByIds(@Nullable Collection<String> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return 0;
        }
        return baseMapper.deleteByIds(ids, CurrentUtil.getCurrentUserId());
    }

    /**
     * 角色权限详情
     *
     * @param id 角色权限ID
     * @since 2024-08-17
     */
    @NotNull public Optional<IamRolePermission> detail(@NotNull String id) {
        return Optional.ofNullable(baseMapper.selectById(id));
    }

    @SuppressWarnings("UnusedReturnValue")
    public int deleteByRoleIds(@Nullable Collection<String> roleIds) {
        if (CollectionUtils.isEmpty(roleIds)) {
            return 0;
        }
        return baseMapper.deleteByRoleIds(roleIds, CurrentUtil.getCurrentUserId());
    }

    @NotNull public List<String> permissionIdsByRoleIds(@Nullable List<String> roleIds) {
        if (CollectionUtils.isEmpty(roleIds)) {
            return Collections.emptyList();
        }
        return baseMapper.permissionIdsByRoleIds(roleIds);
    }

    public void saveRolePermission(@NotNull RolePermissionArgs args) {
        LockUtil.lock(
                LOCK_KEY,
                new LockWrapper<RolePermissionArgs>().lock(RolePermissionArgs::getRoleId, args.getRoleId()),
                true,
                () -> {
                    Set<String> exitPermissionIdSet =
                            new HashSet<>(this.permissionIdsByRoleIds(List.of(args.getRoleId())));
                    Set<String> newPermissionIdSet = new HashSet<>(args.getPermissionIds());
                    Set<String> needAdd = SetUtil.set2MoreOutSet1(exitPermissionIdSet, newPermissionIdSet);
                    Set<String> needDelete = SetUtil.set2MoreOutSet1(newPermissionIdSet, exitPermissionIdSet);
                    if (CollectionUtils.isNotEmpty(needDelete)) {
                        ProxyUtil.proxy(this.getClass()).deleteByIds(needDelete);
                    }
                    if (CollectionUtils.isNotEmpty(needAdd)) {
                        List<IamRolePermission> addList = new ArrayList<>();
                        needAdd.forEach(permissionId -> {
                            IamRolePermission iamRolePermission = new IamRolePermission();
                            iamRolePermission.setRoleId(args.getRoleId());
                            iamRolePermission.setPermissionId(permissionId);
                            addList.add(iamRolePermission);
                        });
                        ProxyUtil.proxy(this.getClass()).saveBatch(addList);
                    }
                });
    }
}
