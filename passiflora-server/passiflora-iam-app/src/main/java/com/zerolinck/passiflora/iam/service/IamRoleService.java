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
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.zerolinck.passiflora.common.util.OnlyFieldCheck;
import com.zerolinck.passiflora.common.util.QueryCondition;
import com.zerolinck.passiflora.common.util.lock.LockUtil;
import com.zerolinck.passiflora.common.util.lock.LockWrapper;
import com.zerolinck.passiflora.iam.mapper.IamRoleMapper;
import com.zerolinck.passiflora.model.iam.entity.IamRole;
import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 角色 Service
 *
 * @author 林常坤 on 2024-08-17
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IamRoleService extends ServiceImpl<IamRoleMapper, IamRole> {

    private static final String LOCK_KEY = "passiflora:lock:iamRole:";

    private final IamRolePermissionService iamRolePermissionService;

    /**
     * 分页查询
     *
     * @param condition 搜索条件
     * @since 2024-08-17
     */
    @NotNull public Page<IamRole> page(@Nullable QueryCondition<IamRole> condition) {
        condition = Objects.requireNonNullElse(condition, new QueryCondition<>());
        return mapper.paginate(
                condition.getPageNumber(), condition.getPageSize(), condition.searchWrapper(IamRole.class));
    }

    /**
     * 列表查询
     *
     * @param condition 搜索条件
     * @since 2024-08-18
     */
    @NotNull public List<IamRole> list(@Nullable QueryCondition<IamRole> condition) {
        condition = Objects.requireNonNullElse(condition, new QueryCondition<>());
        return mapper.selectListByQuery(condition.searchWrapper(IamRole.class));
    }

    /**
     * 新增角色
     *
     * @param iamRole 角色
     * @since 2024-08-17
     */
    public void add(@NotNull IamRole iamRole) {
        LockUtil.lock(LOCK_KEY, new LockWrapper<>(), true, () -> {
            OnlyFieldCheck.checkInsert(mapper, iamRole);
            mapper.insert(iamRole);
        });
    }

    /**
     * 更新角色
     *
     * @param iamRole 角色
     * @since 2024-08-17
     */
    public boolean update(@NotNull IamRole iamRole) {
        return LockUtil.lock(LOCK_KEY, new LockWrapper<>(), true, () -> {
            OnlyFieldCheck.checkUpdate(mapper, iamRole);
            int changeRowCount = mapper.update(iamRole);
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
        int changeRowNum = mapper.deleteBatchByIds(roleIds, 500);
        iamRolePermissionService.deleteByRoleIds(roleIds);
        return changeRowNum;
    }

    /**
     * 角色详情
     *
     * @param roleId 角色ID
     * @since 2024-08-17
     */
    @NotNull public Optional<IamRole> detail(@NotNull String roleId) {
        return Optional.ofNullable(mapper.selectOneById(roleId));
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
        mapper.disable(roleIds);
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
        mapper.enable(roleIds);
    }
}
