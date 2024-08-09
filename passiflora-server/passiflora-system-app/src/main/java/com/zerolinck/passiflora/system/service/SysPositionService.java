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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author linck
 * @since 2024-05-14
 */
@Slf4j
@Service
public class SysPositionService
    extends ServiceImpl<SysPositionMapper, SysPosition> {

    private static final String LOCK_KEY = "passiflora:lock:sysPosition:";

    public Page<SysPosition> page(QueryCondition<SysPosition> condition) {
        if (condition == null) {
            condition = new QueryCondition<>();
        }
        return baseMapper.page(
            condition.page(),
            condition.searchWrapper(SysPosition.class),
            condition.sortWrapper(SysPosition.class)
        );
    }

    public void add(SysPosition sysPosition) {
        LockUtil.lock(
            LOCK_KEY,
            new LockWrapper<SysPosition>()
                .lock(
                    SysPosition::getPositionName,
                    sysPosition.getPositionName()
                ),
            true,
            () -> {
                OnlyFieldCheck.checkInsert(baseMapper, sysPosition);
                generateIadPathAndLevel(sysPosition);
                baseMapper.insert(sysPosition);
                return null;
            }
        );
    }

    public boolean update(SysPosition sysPosition) {
        return LockUtil.lock(
            LOCK_KEY,
            new LockWrapper<SysPosition>()
                .lock(
                    SysPosition::getPositionName,
                    sysPosition.getPositionName()
                ),
            true,
            () -> {
                OnlyFieldCheck.checkUpdate(baseMapper, sysPosition);
                generateIadPathAndLevel(sysPosition);
                int changeRowCount = baseMapper.updateById(sysPosition);
                // 子机构数据变更
                List<SysPositionVo> positionVoList = baseMapper.listByParentId(
                    sysPosition.getPositionId()
                );
                positionVoList.forEach(positionVo -> {
                    generateIadPathAndLevel(positionVo);
                    baseMapper.updateById(positionVo);
                });
                return changeRowCount > 0;
            }
        );
    }

    @Transactional(rollbackFor = Exception.class)
    public int deleteByIds(Collection<String> positionIds) {
        return baseMapper.deleteByIds(
            positionIds,
            CurrentUtil.getCurrentUserId()
        );
    }

    public SysPosition detail(String positionId) {
        SysPosition sysPosition = baseMapper.selectById(positionId);
        if (sysPosition == null) {
            throw new BizException("无对应数据，请刷新后重试");
        }
        return sysPosition;
    }

    public List<SysPositionVo> positionTree() {
        List<SysPositionVo> sysPositionVos = baseMapper.listByParentId("0");
        sysPositionVos.forEach(this::recursionTree);
        return sysPositionVos;
    }

    @Transactional(rollbackFor = Exception.class)
    public void disable(List<String> positionIds) {
        baseMapper.disable(positionIds, CurrentUtil.getCurrentUserId());
    }

    @Transactional(rollbackFor = Exception.class)
    public void enable(List<String> positionIds) {
        List<String> pathIds = new ArrayList<>();
        positionIds.forEach(positionId -> {
            SysPosition sysPosition = baseMapper.selectById(positionId);
            String[] positionIdList = sysPosition
                .getPositionIdPath()
                .split("/");
            Collections.addAll(pathIds, positionIdList);
        });
        baseMapper.enable(pathIds, CurrentUtil.getCurrentUserId());
    }

    public void updateOrder(List<SysPositionVo> sysPositionVos) {
        if (CollectionUtil.isEmpty(sysPositionVos)) {
            return;
        }
        for (SysPositionVo sysPositionTableVo : sysPositionVos) {
            baseMapper.updateOrder(sysPositionTableVo);
            updateOrder(sysPositionTableVo.getChildren());
        }
    }

    private void recursionTree(SysPositionVo sysPositionVo) {
        sysPositionVo.setChildren(
            baseMapper.listByParentId(sysPositionVo.getPositionId())
        );
        sysPositionVo.getChildren().forEach(this::recursionTree);
    }

    private void generateIadPathAndLevel(SysPosition sysPosition) {
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
