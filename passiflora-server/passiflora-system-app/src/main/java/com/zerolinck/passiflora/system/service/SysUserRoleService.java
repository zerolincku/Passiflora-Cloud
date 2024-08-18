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
import com.zerolinck.passiflora.common.util.*;
import com.zerolinck.passiflora.common.util.lock.LockUtil;
import com.zerolinck.passiflora.common.util.lock.LockWrapper;
import com.zerolinck.passiflora.model.system.args.SysUserSaveArgs;
import com.zerolinck.passiflora.model.system.entity.SysUserRole;
import com.zerolinck.passiflora.model.system.vo.SysUserRoleVo;
import com.zerolinck.passiflora.system.mapper.SysUserRoleMapper;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户角色绑定 Service
 *
 * @author 林常坤
 * @since 2024-08-17
 */
@Slf4j
@Service
public class SysUserRoleService extends ServiceImpl<SysUserRoleMapper, SysUserRole> {

    private static final String LOCK_KEY = "passiflora:lock:sysUserRole:";

    /**
     * 分页查询
     *
     * @param condition 搜索条件
     * @since 2024-08-17
     */
    @Nonnull
    public Page<SysUserRole> page(@Nullable QueryCondition<SysUserRole> condition) {
        condition = Objects.requireNonNullElse(condition, new QueryCondition<>());
        return baseMapper.page(
                condition.page(), condition.searchWrapper(SysUserRole.class), condition.sortWrapper(SysUserRole.class));
    }

    /**
     * 新增用户角色绑定
     *
     * @param sysUserRole 用户角色绑定
     * @since 2024-08-17
     */
    public void add(@Nonnull SysUserRole sysUserRole) {
        LockUtil.lock(LOCK_KEY, new LockWrapper<SysUserRole>(), true, () -> {
            OnlyFieldCheck.checkInsert(baseMapper, sysUserRole);
            baseMapper.insert(sysUserRole);
            return null;
        });
    }

    /**
     * 更新用户角色绑定
     *
     * @param sysUserRole 用户角色绑定
     * @since 2024-08-17
     */
    public boolean update(@Nonnull SysUserRole sysUserRole) {
        return LockUtil.lock(LOCK_KEY, new LockWrapper<SysUserRole>(), true, () -> {
            OnlyFieldCheck.checkUpdate(baseMapper, sysUserRole);
            int changeRowCount = baseMapper.updateById(sysUserRole);
            return changeRowCount > 0;
        });
    }

    /**
     * 删除用户角色绑定
     *
     * @param ids 用户角色绑定ID集合
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
     * 用户角色绑定详情
     *
     * @param id 用户角色绑定ID
     * @since 2024-08-17
     */
    @Nonnull
    public Optional<SysUserRole> detail(@Nonnull String id) {
        return Optional.ofNullable(baseMapper.selectById(id));
    }

    @SuppressWarnings("UnusedReturnValue")
    public int deleteByUserIds(@Nonnull Collection<String> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return 0;
        }
        return baseMapper.deleteByUserIds(userIds, CurrentUtil.getCurrentUserId());
    }

    @Nonnull
    public List<SysUserRoleVo> selectByUserIds(@Nonnull Collection<String> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return Collections.emptyList();
        }
        return baseMapper.selectByUserIds(userIds);
    }

    public void updateRelation(@Nonnull SysUserSaveArgs args) {
        LockUtil.lock(
                LOCK_KEY, new LockWrapper<SysUserRole>().lock(SysUserRole::getUserId, args.getUserId()), true, () -> {
                    if (CollectionUtils.isEmpty(args.getRoleIds())) {
                        this.deleteByUserIds(List.of(args.getUserId()));
                        return null;
                    }
                    Set<String> existRoleIds = selectByUserIds(List.of(args.getUserId())).stream()
                            .map(SysUserRoleVo::getRoleId)
                            .collect(Collectors.toSet());
                    Set<String> newRoleIds = new HashSet<>(args.getRoleIds());
                    Set<String> addRoleIds = SetUtil.set2MoreOutSet1(existRoleIds, newRoleIds);
                    Set<String> delRoleIds = SetUtil.set2MoreOutSet1(newRoleIds, existRoleIds);
                    if (CollectionUtils.isNotEmpty(delRoleIds)) {
                        this.deleteByUserIdAndRoleIds(args.getUserId(), delRoleIds);
                    }
                    if (CollectionUtils.isNotEmpty(addRoleIds)) {
                        List<SysUserRole> addList = new ArrayList<>();
                        for (String roleId : addRoleIds) {
                            SysUserRole role = new SysUserRole();
                            role.setUserId(args.getUserId());
                            role.setRoleId(roleId);
                            addList.add(role);
                        }
                        ProxyUtil.proxy(this.getClass()).saveBatch(addList);
                    }
                    return null;
                });
    }

    @SuppressWarnings("UnusedReturnValue")
    public int deleteByUserIdAndRoleIds(@Nonnull String userId, @Nullable Collection<String> roleIds) {
        if (CollectionUtils.isEmpty(roleIds)) {
            return 0;
        }
        return baseMapper.deleteByUserIdAndRoleIds(userId, roleIds, CurrentUtil.getCurrentUserId());
    }
}
