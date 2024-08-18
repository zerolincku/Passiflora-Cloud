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
import com.zerolinck.passiflora.model.system.entity.SysRole;
import com.zerolinck.passiflora.system.mapper.SysRoleMapper;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 角色 Service
 *
 * @author 林常坤
 * @since 2024-08-17
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysRoleService extends ServiceImpl<SysRoleMapper, SysRole> {

    private static final String LOCK_KEY = "passiflora:lock:sysRole:";

    private final SysRolePermissionService sysRolePermissionService;

    /**
     * 分页查询
     *
     * @param condition 搜索条件
     * @since 2024-08-17
     */
    @Nonnull
    public Page<SysRole> page(@Nullable QueryCondition<SysRole> condition) {
        condition = Objects.requireNonNullElse(condition, new QueryCondition<>());
        return baseMapper.page(
                condition.page(), condition.searchWrapper(SysRole.class), condition.sortWrapper(SysRole.class));
    }

    /**
     * 列表查询
     *
     * @param condition 搜索条件
     * @since 2024-08-18
     */
    @Nonnull
    public List<SysRole> list(@Nullable QueryCondition<SysRole> condition) {
        condition = Objects.requireNonNullElse(condition, new QueryCondition<>());
        return baseMapper.selectList(condition.searchWrapper(SysRole.class));
    }

    /**
     * 新增角色
     *
     * @param sysRole 角色
     * @since 2024-08-17
     */
    public void add(@Nonnull SysRole sysRole) {
        LockUtil.lock(LOCK_KEY, new LockWrapper<SysRole>(), true, () -> {
            OnlyFieldCheck.checkInsert(baseMapper, sysRole);
            baseMapper.insert(sysRole);
            return null;
        });
    }

    /**
     * 更新角色
     *
     * @param sysRole 角色
     * @since 2024-08-17
     */
    public boolean update(@Nonnull SysRole sysRole) {
        return LockUtil.lock(LOCK_KEY, new LockWrapper<SysRole>(), true, () -> {
            OnlyFieldCheck.checkUpdate(baseMapper, sysRole);
            int changeRowCount = baseMapper.updateById(sysRole);
            return changeRowCount > 0;
        });
    }

    /**
     * 删除角色
     *
     * @param roleIds 角色ID集合
     * @since 2024-08-17
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteByIds(@Nullable Collection<String> roleIds) {
        if (CollectionUtils.isEmpty(roleIds)) {
            return 0;
        }
        int changeRowNum = baseMapper.deleteByIds(roleIds, CurrentUtil.getCurrentUserId());
        sysRolePermissionService.deleteByRoleIds(roleIds);
        return changeRowNum;
    }

    /**
     * 角色详情
     *
     * @param roleId 角色ID
     * @since 2024-08-17
     */
    @Nonnull
    public Optional<SysRole> detail(@Nonnull String roleId) {
        return Optional.ofNullable(baseMapper.selectById(roleId));
    }

    /**
     * 禁用角色
     *
     * @param roleIds 角色ID集合
     * @since 2024-08-18
     */
    @Transactional(rollbackFor = Exception.class)
    public void disable(@Nullable List<String> roleIds) {
        if (CollectionUtils.isEmpty(roleIds)) {
            return;
        }
        baseMapper.disable(roleIds, CurrentUtil.getCurrentUserId());
    }

    /**
     * 启用角色
     *
     * @param roleIds 角色ID集合
     * @since 2024-08-18
     */
    @Transactional(rollbackFor = Exception.class)
    public void enable(@Nullable List<String> roleIds) {
        if (CollectionUtils.isEmpty(roleIds)) {
            return;
        }
        baseMapper.enable(roleIds, CurrentUtil.getCurrentUserId());
    }
}
