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

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zerolinck.passiflora.common.util.CurrentUtil;
import com.zerolinck.passiflora.common.util.SetUtil;
import com.zerolinck.passiflora.common.util.lock.LockUtil;
import com.zerolinck.passiflora.common.util.lock.LockWrapper;
import com.zerolinck.passiflora.model.system.args.SysUserSaveArgs;
import com.zerolinck.passiflora.model.system.entity.SysUserPosition;
import com.zerolinck.passiflora.model.system.vo.SysUserPositionVo;
import com.zerolinck.passiflora.system.mapper.SysUserPositionMapper;
import jakarta.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
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
    public List<SysUserPositionVo> selectByUserIds(@Nonnull Collection<String> userIds) {
        if (CollectionUtil.isEmpty(userIds)) {
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
                    if (CollectionUtil.isEmpty(args.getPositionIds())) {
                        this.deleteByUserIds(List.of(args.getUserId()));
                        return null;
                    }
                    Set<String> existPositionIds = findByUserIds(List.of(args.getUserId())).stream()
                            .map(SysUserPosition::getPositionId)
                            .collect(Collectors.toSet());
                    Set<String> newPositionIds = new HashSet<>(args.getPositionIds());
                    Set<String> addPositionIds = SetUtil.differenceSet2FromSet1(existPositionIds, newPositionIds);
                    Set<String> delPositionIds = SetUtil.differenceSet2FromSet1(newPositionIds, existPositionIds);
                    if (CollectionUtil.isNotEmpty(delPositionIds)) {
                        this.deleteByUserIdAndPositionIds(args.getUserId(), delPositionIds);
                    }
                    if (CollectionUtil.isNotEmpty(addPositionIds)) {
                        List<SysUserPosition> addList = new ArrayList<>();
                        for (String positionId : addPositionIds) {
                            SysUserPosition position = new SysUserPosition();
                            position.setUserId(args.getUserId());
                            position.setPositionId(positionId);
                            addList.add(position);
                        }
                        this.saveBatch(addList);
                    }
                    return null;
                });
    }

    @Nonnull
    public List<SysUserPosition> findByUserIds(@Nonnull List<String> userIds) {
        if (CollectionUtil.isEmpty(userIds)) {
            return Collections.emptyList();
        }
        return baseMapper.selectList(new LambdaQueryWrapper<SysUserPosition>().in(SysUserPosition::getUserId, userIds));
    }

    @Nonnull
    public List<SysUserPosition> findByPositionIds(@Nonnull List<String> positionIds) {
        if (CollectionUtil.isEmpty(positionIds)) {
            return Collections.emptyList();
        }
        return baseMapper.selectList(
                new LambdaQueryWrapper<SysUserPosition>().eq(SysUserPosition::getPositionId, positionIds));
    }

    public int deleteByUserIds(@Nonnull Collection<String> userIds) {
        if (CollectionUtil.isEmpty(userIds)) {
            return 0;
        }
        return baseMapper.deleteByUserIds(userIds, CurrentUtil.getCurrentUserId());
    }

    public int deleteByPositionIds(@Nonnull Collection<String> positionIds) {
        if (CollectionUtil.isEmpty(positionIds)) {
            return 0;
        }
        return baseMapper.deleteByPositionIds(positionIds, CurrentUtil.getCurrentUserId());
    }

    public int deleteByUserIdAndPositionIds(@Nonnull String userId, @Nonnull Collection<String> positionIds) {
        if (CollectionUtil.isEmpty(positionIds)) {
            return 0;
        }
        return baseMapper.deleteByUserIdAndPositionIds(userId, positionIds, CurrentUtil.getCurrentUserId());
    }
}
