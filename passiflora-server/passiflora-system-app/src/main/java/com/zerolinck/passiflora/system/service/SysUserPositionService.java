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
import com.zerolinck.passiflora.common.exception.BizException;
import com.zerolinck.passiflora.common.util.CurrentUtil;
import com.zerolinck.passiflora.common.util.OnlyFieldCheck;
import com.zerolinck.passiflora.common.util.QueryCondition;
import com.zerolinck.passiflora.common.util.lock.LockUtil;
import com.zerolinck.passiflora.common.util.lock.LockWrapper;
import com.zerolinck.passiflora.model.system.entity.SysUserPosition;
import com.zerolinck.passiflora.system.mapper.SysUserPositionMapper;
import java.util.Collection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author linck
 * @since 2024-05-14
 */
@Slf4j
@Service
public class SysUserPositionService
    extends ServiceImpl<SysUserPositionMapper, SysUserPosition> {

    private static final String LOCK_KEY = "passiflora:lock:sysUserPosition:";

    public Page<SysUserPosition> page(
        QueryCondition<SysUserPosition> condition
    ) {
        if (condition == null) {
            condition = new QueryCondition<>();
        }
        return baseMapper.page(
            condition.page(),
            condition.searchWrapper(SysUserPosition.class),
            condition.sortWrapper(SysUserPosition.class)
        );
    }

    public void add(SysUserPosition sysUserPosition) {
        LockUtil.lockAndTransactionalLogic(
            LOCK_KEY,
            new LockWrapper<SysUserPosition>(),
            () -> {
                OnlyFieldCheck.checkInsert(baseMapper, sysUserPosition);
                baseMapper.insert(sysUserPosition);
                return null;
            }
        );
    }

    public boolean update(SysUserPosition sysUserPosition) {
        return LockUtil.lockAndTransactionalLogic(
            LOCK_KEY,
            new LockWrapper<SysUserPosition>(),
            () -> {
                OnlyFieldCheck.checkUpdate(baseMapper, sysUserPosition);
                int changeRowCount = baseMapper.updateById(sysUserPosition);
                return changeRowCount > 0;
            }
        );
    }

    @Transactional(rollbackFor = Exception.class)
    public int deleteByIds(Collection<String> bindIds) {
        return baseMapper.deleteByIds(bindIds, CurrentUtil.getCurrentUserId());
    }

    public SysUserPosition detail(String bindId) {
        SysUserPosition sysUserPosition = baseMapper.selectById(bindId);
        if (sysUserPosition == null) {
            throw new BizException("无对应用户职位绑定数据，请刷新后重试");
        }
        return sysUserPosition;
    }
}
