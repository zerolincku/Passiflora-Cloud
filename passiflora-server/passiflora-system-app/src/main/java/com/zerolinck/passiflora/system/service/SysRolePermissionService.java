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
package com.zerolinck.passiflora.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zerolinck.passiflora.common.util.CurrentUtil;
import com.zerolinck.passiflora.common.util.OnlyFieldCheck;
import com.zerolinck.passiflora.common.util.QueryCondition;
import com.zerolinck.passiflora.common.util.lock.LockUtil;
import com.zerolinck.passiflora.common.util.lock.LockWrapper;
import com.zerolinck.passiflora.model.system.entity.SysRolePermission;
import com.zerolinck.passiflora.system.mapper.SysRolePermissionMapper;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 角色权限 Service
 *
 * @author 林常坤
 * @since 2024-08-17
 */
@Slf4j
@Service
public class SysRolePermissionService extends ServiceImpl<SysRolePermissionMapper, SysRolePermission> {

    private static final String LOCK_KEY = "passiflora:lock:sysRolePermission:";

    /**
     * 分页查询
     *
     * @param condition 搜索条件
     * @since 2024-08-17
     */
    @Nonnull
    public Page<SysRolePermission> page(@Nullable QueryCondition<SysRolePermission> condition) {
        condition = Objects.requireNonNullElse(condition, new QueryCondition<>());
        return baseMapper.page(
                condition.page(),
                condition.searchWrapper(SysRolePermission.class),
                condition.sortWrapper(SysRolePermission.class));
    }

    /**
     * 新增角色权限
     *
     * @param sysRolePermission 角色权限
     * @since 2024-08-17
     */
    public void add(@Nonnull SysRolePermission sysRolePermission) {
        LockUtil.lock(LOCK_KEY, new LockWrapper<SysRolePermission>(), true, () -> {
            OnlyFieldCheck.checkInsert(baseMapper, sysRolePermission);
            baseMapper.insert(sysRolePermission);
            return null;
        });
    }

    /**
     * 更新角色权限
     *
     * @param sysRolePermission 角色权限
     * @since 2024-08-17
     */
    public boolean update(@Nonnull SysRolePermission sysRolePermission) {
        return LockUtil.lock(LOCK_KEY, new LockWrapper<SysRolePermission>(), true, () -> {
            OnlyFieldCheck.checkUpdate(baseMapper, sysRolePermission);
            int changeRowCount = baseMapper.updateById(sysRolePermission);
            return changeRowCount > 0;
        });
    }

    /**
     * 删除角色权限
     *
     * @param bindIds 角色权限ID集合
     * @since 2024-08-17
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteByIds(@Nullable Collection<String> bindIds) {
        if (CollectionUtils.isEmpty(bindIds)) {
            return 0;
        }
        return baseMapper.deleteByIds(bindIds, CurrentUtil.getCurrentUserId());
    }

    /**
     * 角色权限详情
     *
     * @param bindId 角色权限ID
     * @since 2024-08-17
     */
    @Nonnull
    public Optional<SysRolePermission> detail(@Nonnull String bindId) {
        return Optional.ofNullable(baseMapper.selectById(bindId));
    }
}
