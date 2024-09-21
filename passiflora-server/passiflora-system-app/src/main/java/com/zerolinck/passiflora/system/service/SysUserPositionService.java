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

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zerolinck.passiflora.common.util.CurrentUtil;
import com.zerolinck.passiflora.common.util.ProxyUtil;
import com.zerolinck.passiflora.common.util.SetUtil;
import com.zerolinck.passiflora.common.util.lock.LockUtil;
import com.zerolinck.passiflora.common.util.lock.LockWrapper;
import com.zerolinck.passiflora.model.system.args.SysUserSaveArgs;
import com.zerolinck.passiflora.model.system.entity.SysUserPosition;
import com.zerolinck.passiflora.model.system.vo.SysUserPositionVo;
import com.zerolinck.passiflora.system.mapper.SysUserPositionMapper;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

/**
 * @author linck
 * @since 2024-05-14
 */
@Slf4j
@Service
public class SysUserPositionService extends ServiceImpl<SysUserPositionMapper, SysUserPosition> {

    private static final String LOCK_KEY = "passiflora:lock:sysUserPosition:";

    @Nonnull
    public List<SysUserPositionVo> selectByUserIds(@Nullable Collection<String> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return Collections.emptyList();
        }
        return baseMapper.selectByUserIds(userIds);
    }

    public void updateRelation(@Nonnull SysUserSaveArgs args) {
        LockUtil.lock(
                LOCK_KEY,
                new LockWrapper<SysUserPosition>().lock(SysUserPosition::getUserId, args.getUserId()),
                true,
                () -> {
                    if (CollectionUtils.isEmpty(args.getPositionIds())) {
                        this.deleteByUserIds(List.of(args.getUserId()));
                        return;
                    }
                    Set<String> existPositionIds = findByUserIds(List.of(args.getUserId())).stream()
                            .map(SysUserPosition::getPositionId)
                            .collect(Collectors.toSet());
                    Set<String> newPositionIds = new HashSet<>(args.getPositionIds());
                    Set<String> addPositionIds = SetUtil.set2MoreOutSet1(existPositionIds, newPositionIds);
                    Set<String> delPositionIds = SetUtil.set2MoreOutSet1(newPositionIds, existPositionIds);
                    if (CollectionUtils.isNotEmpty(delPositionIds)) {
                        this.deleteByUserIdAndPositionIds(args.getUserId(), delPositionIds);
                    }
                    if (CollectionUtils.isNotEmpty(addPositionIds)) {
                        List<SysUserPosition> addList = new ArrayList<>();
                        for (String positionId : addPositionIds) {
                            SysUserPosition position = new SysUserPosition();
                            position.setUserId(args.getUserId());
                            position.setPositionId(positionId);
                            addList.add(position);
                        }
                        ProxyUtil.proxy(this.getClass()).saveBatch(addList);
                    }
                });
    }

    @Nonnull
    public List<SysUserPosition> findByUserIds(@Nullable List<String> userIds) {
        userIds = Objects.requireNonNullElse(userIds, Collections.emptyList());
        return baseMapper.selectList(new LambdaQueryWrapper<SysUserPosition>().in(SysUserPosition::getUserId, userIds));
    }

    @Nonnull
    @SuppressWarnings("unused")
    public List<SysUserPosition> findByPositionIds(@Nullable List<String> positionIds) {
        positionIds = Objects.requireNonNullElse(positionIds, Collections.emptyList());
        return baseMapper.selectList(
                new LambdaQueryWrapper<SysUserPosition>().eq(SysUserPosition::getPositionId, positionIds));
    }

    @SuppressWarnings("UnusedReturnValue")
    public int deleteByUserIds(@Nullable Collection<String> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return 0;
        }
        return baseMapper.deleteByUserIds(userIds, CurrentUtil.getCurrentUserId());
    }

    @SuppressWarnings({"UnusedReturnValue", "unused"})
    public int deleteByPositionIds(@Nullable Collection<String> positionIds) {
        if (CollectionUtils.isEmpty(positionIds)) {
            return 0;
        }
        return baseMapper.deleteByPositionIds(positionIds, CurrentUtil.getCurrentUserId());
    }

    @SuppressWarnings("UnusedReturnValue")
    public int deleteByUserIdAndPositionIds(@Nonnull String userId, @Nullable Collection<String> positionIds) {
        if (CollectionUtils.isEmpty(positionIds)) {
            return 0;
        }
        return baseMapper.deleteByUserIdAndPositionIds(userId, positionIds, CurrentUtil.getCurrentUserId());
    }
}
