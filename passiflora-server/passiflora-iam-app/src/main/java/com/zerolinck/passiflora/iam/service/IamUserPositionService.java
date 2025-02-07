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

import com.zerolinck.passiflora.common.util.ListUtils;
import com.zerolinck.passiflora.common.util.SetUtils;
import com.zerolinck.passiflora.common.util.lock.LockUtils;
import com.zerolinck.passiflora.common.util.lock.LockWrapper;
import com.zerolinck.passiflora.iam.mapper.IamUserPositionMapper;
import com.zerolinck.passiflora.model.iam.args.IamUserArgs;
import com.zerolinck.passiflora.model.iam.entity.IamUserPosition;
import com.zerolinck.passiflora.model.iam.resp.IamUserPositionResp;
import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * IAM用户职位服务类
 *
 * @author linck
 * @since 2024-05-14
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IamUserPositionService {
    private final IamUserPositionMapper mapper;
    private static final String LOCK_KEY = "passiflora:lock:iamUserPosition:";

    /**
     * 根据用户ID集合查询用户职位
     *
     * @param userIds 用户ID集合
     * @return 用户职位响应列表
     * @since 2024-05-14
     */
    @NotNull public List<IamUserPositionResp> selectByUserIds(@Nullable Collection<String> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return ListUtils.emptyList();
        }
        return mapper.selectByUserIds(userIds);
    }

    /**
     * 更新用户职位关系
     *
     * @param args 用户参数
     * @since 2024-05-14
     */
    public void updateRelation(@NotNull IamUserArgs args) {
        LockUtils.lock(
                LOCK_KEY,
                new LockWrapper<IamUserPosition>().lock(IamUserPosition::getUserId, args.getUserId()),
                true,
                () -> {
                    if (CollectionUtils.isEmpty(args.getPositionIds())) {
                        this.deleteByUserIds(List.of(args.getUserId()));
                        return;
                    }
                    Set<String> existPositionIds = findByUserIds(List.of(args.getUserId())).stream()
                            .map(IamUserPosition::getPositionId)
                            .collect(Collectors.toSet());
                    Set<String> newPositionIds = new HashSet<>(args.getPositionIds());
                    Set<String> addPositionIds = SetUtils.set2MoreOutSet1(existPositionIds, newPositionIds);
                    Set<String> delPositionIds = SetUtils.set2MoreOutSet1(newPositionIds, existPositionIds);
                    if (CollectionUtils.isNotEmpty(delPositionIds)) {
                        this.deleteByUserIdAndPositionIds(args.getUserId(), delPositionIds);
                    }
                    if (CollectionUtils.isNotEmpty(addPositionIds)) {
                        List<IamUserPosition> addList = new ArrayList<>();
                        for (String positionId : addPositionIds) {
                            IamUserPosition position = new IamUserPosition();
                            position.setUserId(args.getUserId());
                            position.setPositionId(positionId);
                            addList.add(position);
                        }
                        mapper.insertBatch(addList, 500);
                    }
                });
    }

    /**
     * 根据用户ID集合查询用户职位
     *
     * @param userIds 用户ID集合
     * @return 用户职位列表
     * @since 2024-05-14
     */
    @NotNull public List<IamUserPosition> findByUserIds(@Nullable Collection<String> userIds) {
        return mapper.listByUserIds(userIds);
    }

    /**
     * 根据职位ID集合查询用户职位
     *
     * @param positionIds 职位ID集合
     * @return 用户职位列表
     * @since 2024-05-14
     */
    @NotNull @SuppressWarnings("unused")
    public List<IamUserPosition> findByPositionIds(@Nullable List<String> positionIds) {
        return mapper.listByPositionIds(positionIds);
    }

    /**
     * 根据用户ID集合删除用户职位
     *
     * @param userIds 用户ID集合
     * @return 删除的用户职位数量
     * @since 2024-05-14
     */
    @SuppressWarnings("UnusedReturnValue")
    public int deleteByUserIds(@Nullable Collection<String> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return 0;
        }
        return mapper.deleteByUserIds(userIds);
    }

    /**
     * 根据职位ID集合删除用户职位
     *
     * @param positionIds 职位ID集合
     * @return 删除的用户职位数量
     * @since 2024-05-14
     */
    @SuppressWarnings({"UnusedReturnValue", "unused"})
    public int deleteByPositionIds(@Nullable Collection<String> positionIds) {
        if (CollectionUtils.isEmpty(positionIds)) {
            return 0;
        }
        return mapper.deleteByPositionIds(positionIds);
    }

    /**
     * 根据用户ID和职位ID集合删除用户职位
     *
     * @param userId 用户ID
     * @param positionIds 职位ID集合
     * @return 删除的用户职位数量
     * @since 2024-05-14
     */
    @SuppressWarnings("UnusedReturnValue")
    public int deleteByUserIdAndPositionIds(@NotNull String userId, @Nullable Collection<String> positionIds) {
        if (CollectionUtils.isEmpty(positionIds)) {
            return 0;
        }
        return mapper.deleteByUserIdAndPositionIds(userId, positionIds);
    }
}
