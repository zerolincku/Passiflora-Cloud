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
import java.util.stream.Collectors;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.zerolinck.passiflora.common.util.OnlyFieldCheck;
import com.zerolinck.passiflora.common.util.ProxyUtil;
import com.zerolinck.passiflora.common.util.QueryCondition;
import com.zerolinck.passiflora.common.util.SetUtil;
import com.zerolinck.passiflora.common.util.lock.LockUtil;
import com.zerolinck.passiflora.common.util.lock.LockWrapper;
import com.zerolinck.passiflora.iam.mapper.IamUserRoleMapper;
import com.zerolinck.passiflora.model.iam.args.IamUserArgs;
import com.zerolinck.passiflora.model.iam.entity.IamUserRole;
import com.zerolinck.passiflora.model.iam.resp.IamUserRoleResp;
import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

/**
 * 用户角色绑定 Service
 *
 * @author 林常坤 on 2024-08-17
 */
@Slf4j
@Service
public class IamUserRoleService extends ServiceImpl<IamUserRoleMapper, IamUserRole> {

    private static final String LOCK_KEY = "passiflora:lock:iamUserRole:";

    /**
     * 分页查询
     *
     * @param condition 搜索条件
     * @since 2024-08-17
     */
    @NotNull public Page<IamUserRole> page(@Nullable QueryCondition<IamUserRole> condition) {
        condition = Objects.requireNonNullElse(condition, new QueryCondition<>());
        return mapper.paginate(
                condition.getPageNumber(), condition.getPageSize(), condition.searchWrapper(IamUserRole.class));
    }

    /**
     * 新增用户角色绑定
     *
     * @param iamUserRole 用户角色绑定
     * @since 2024-08-17
     */
    public void add(@NotNull IamUserRole iamUserRole) {
        LockUtil.lock(LOCK_KEY, new LockWrapper<>(), true, () -> {
            OnlyFieldCheck.checkInsert(mapper, iamUserRole);
            mapper.insert(iamUserRole);
        });
    }

    /**
     * 更新用户角色绑定
     *
     * @param iamUserRole 用户角色绑定
     * @since 2024-08-17
     */
    public boolean update(@NotNull IamUserRole iamUserRole) {
        return LockUtil.lock(LOCK_KEY, new LockWrapper<>(), true, () -> {
            OnlyFieldCheck.checkUpdate(mapper, iamUserRole);
            int changeRowCount = mapper.update(iamUserRole);
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
        return mapper.deleteBatchByIds(ids, 500);
    }

    /**
     * 用户角色绑定详情
     *
     * @param id 用户角色绑定ID
     * @since 2024-08-17
     */
    @NotNull public Optional<IamUserRole> detail(@NotNull String id) {
        return Optional.ofNullable(mapper.selectOneById(id));
    }

    @SuppressWarnings("UnusedReturnValue")
    public int deleteByUserIds(@NotNull Collection<String> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return 0;
        }
        return mapper.deleteByUserIds(userIds);
    }

    @NotNull public List<IamUserRoleResp> selectByUserIds(@NotNull Collection<String> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return Collections.emptyList();
        }
        return mapper.selectByUserIds(userIds);
    }

    public void updateRelation(@NotNull IamUserArgs args) {
        LockUtil.lock(
                LOCK_KEY, new LockWrapper<IamUserRole>().lock(IamUserRole::getUserId, args.getUserId()), true, () -> {
                    if (CollectionUtils.isEmpty(args.getRoleIds())) {
                        this.deleteByUserIds(List.of(args.getUserId()));
                        return;
                    }
                    Set<String> existRoleIds = selectByUserIds(List.of(args.getUserId())).stream()
                            .map(IamUserRoleResp::getRoleId)
                            .collect(Collectors.toSet());
                    Set<String> newRoleIds = new HashSet<>(args.getRoleIds());
                    Set<String> addRoleIds = SetUtil.set2MoreOutSet1(existRoleIds, newRoleIds);
                    Set<String> delRoleIds = SetUtil.set2MoreOutSet1(newRoleIds, existRoleIds);
                    if (CollectionUtils.isNotEmpty(delRoleIds)) {
                        this.deleteByUserIdAndRoleIds(args.getUserId(), delRoleIds);
                    }
                    if (CollectionUtils.isNotEmpty(addRoleIds)) {
                        List<IamUserRole> addList = new ArrayList<>();
                        for (String roleId : addRoleIds) {
                            IamUserRole role = new IamUserRole();
                            role.setUserId(args.getUserId());
                            role.setRoleId(roleId);
                            addList.add(role);
                        }
                        ProxyUtil.proxy(this.getClass()).saveBatch(addList);
                    }
                });
    }

    @SuppressWarnings("UnusedReturnValue")
    public int deleteByUserIdAndRoleIds(@NotNull String userId, @Nullable Collection<String> roleIds) {
        if (CollectionUtils.isEmpty(roleIds)) {
            return 0;
        }
        return mapper.deleteByUserIdAndRoleIds(userId, roleIds);
    }
}
