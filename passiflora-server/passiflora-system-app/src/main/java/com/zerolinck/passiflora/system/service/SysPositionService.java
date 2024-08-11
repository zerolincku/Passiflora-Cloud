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
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zerolinck.passiflora.common.exception.BizException;
import com.zerolinck.passiflora.common.util.CurrentUtil;
import com.zerolinck.passiflora.common.util.OnlyFieldCheck;
import com.zerolinck.passiflora.common.util.QueryCondition;
import com.zerolinck.passiflora.common.util.lock.LockUtil;
import com.zerolinck.passiflora.common.util.lock.LockWrapper;
import com.zerolinck.passiflora.model.system.entity.SysPosition;
import com.zerolinck.passiflora.model.system.vo.SysPositionVo;
import com.zerolinck.passiflora.system.mapper.SysPositionMapper;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author linck
 * @since 2024-05-14
 */
@Slf4j
@Service
public class SysPositionService extends ServiceImpl<SysPositionMapper, SysPosition> {

    private static final String LOCK_KEY = "passiflora:lock:sysPosition:";

    @Nonnull
    public Page<SysPosition> page(@Nullable QueryCondition<SysPosition> condition) {
        condition = Objects.requireNonNullElse(condition, new QueryCondition<>());
        return baseMapper.page(
                condition.page(), condition.searchWrapper(SysPosition.class), condition.sortWrapper(SysPosition.class));
    }

    @Nonnull
    public List<SysPosition> listByIds(@Nullable List<String> ids) {
        ids = Objects.requireNonNullElse(ids, Collections.emptyList());
        return baseMapper.selectList(new LambdaQueryWrapper<SysPosition>().in(SysPosition::getPositionId, ids));
    }

    public void add(@Nonnull SysPosition sysPosition) {
        LockUtil.lock(
                LOCK_KEY,
                new LockWrapper<SysPosition>().lock(SysPosition::getPositionName, sysPosition.getPositionName()),
                true,
                () -> {
                    OnlyFieldCheck.checkInsert(baseMapper, sysPosition);
                    generateIadPathAndLevel(sysPosition);
                    baseMapper.insert(sysPosition);
                    return null;
                });
    }

    public boolean update(@Nonnull SysPosition sysPosition) {
        return LockUtil.lock(
                LOCK_KEY,
                new LockWrapper<SysPosition>().lock(SysPosition::getPositionName, sysPosition.getPositionName()),
                true,
                () -> {
                    OnlyFieldCheck.checkUpdate(baseMapper, sysPosition);
                    generateIadPathAndLevel(sysPosition);
                    int changeRowCount = baseMapper.updateById(sysPosition);
                    // 子机构数据变更
                    List<SysPositionVo> positionVoList = baseMapper.listByParentId(sysPosition.getPositionId());
                    positionVoList.forEach(positionVo -> {
                        generateIadPathAndLevel(positionVo);
                        baseMapper.updateById(positionVo);
                    });
                    return changeRowCount > 0;
                });
    }

    @Transactional(rollbackFor = Exception.class)
    public int deleteByIds(@Nullable Collection<String> positionIds) {
        if (CollectionUtil.isEmpty(positionIds)) {
            return 0;
        }
        return baseMapper.deleteByIds(positionIds, CurrentUtil.getCurrentUserId());
    }

    @Nonnull
    public SysPosition detail(@Nonnull String positionId) {
        SysPosition sysPosition = baseMapper.selectById(positionId);
        if (sysPosition == null) {
            throw new BizException("无对应数据，请刷新后重试");
        }
        return sysPosition;
    }

    @Nonnull
    public List<SysPositionVo> positionTree() {
        List<SysPositionVo> sysPositionVos = baseMapper.listByParentId("0");
        sysPositionVos.forEach(this::recursionTree);
        return sysPositionVos;
    }

    @Transactional(rollbackFor = Exception.class)
    public void disable(@Nullable List<String> positionIds) {
        if (CollectionUtil.isEmpty(positionIds)) {
            return;
        }
        baseMapper.disable(positionIds, CurrentUtil.getCurrentUserId());
    }

    @Transactional(rollbackFor = Exception.class)
    public void enable(@Nullable List<String> positionIds) {
        if (CollectionUtil.isEmpty(positionIds)) {
            return;
        }
        List<String> pathIds = new ArrayList<>();
        positionIds.forEach(positionId -> {
            SysPosition sysPosition = baseMapper.selectById(positionId);
            String[] positionIdList = sysPosition.getPositionIdPath().split("/");
            Collections.addAll(pathIds, positionIdList);
        });
        baseMapper.enable(pathIds, CurrentUtil.getCurrentUserId());
    }

    public void updateOrder(@Nullable List<SysPositionVo> sysPositionVos) {
        if (CollectionUtil.isEmpty(sysPositionVos)) {
            return;
        }
        for (SysPositionVo sysPositionTableVo : sysPositionVos) {
            baseMapper.updateOrder(sysPositionTableVo);
            updateOrder(sysPositionTableVo.getChildren());
        }
    }

    private void recursionTree(@Nonnull SysPositionVo sysPositionVo) {
        sysPositionVo.setChildren(baseMapper.listByParentId(sysPositionVo.getPositionId()));
        sysPositionVo.getChildren().forEach(this::recursionTree);
    }

    private void generateIadPathAndLevel(@Nonnull SysPosition sysPosition) {
        StringBuilder codeBuffer = new StringBuilder();
        String positionParentId = sysPosition.getParentPositionId();
        int level = 0;
        if (!"0".equals(positionParentId)) {
            SysPosition parentOrg = detail(positionParentId);
            codeBuffer.append(parentOrg.getPositionIdPath()).append("/");
            level = parentOrg.getPositionLevel() + 1;
        }
        codeBuffer.append(sysPosition.getPositionId());
        sysPosition.setPositionLevel(level);
        sysPosition.setPositionIdPath(codeBuffer.toString());
    }
}
