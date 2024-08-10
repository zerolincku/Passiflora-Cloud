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
import com.zerolinck.passiflora.model.system.entity.SysPositionDataScope;
import com.zerolinck.passiflora.system.mapper.SysPositionDataScopeMapper;
import jakarta.annotation.Nonnull;
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
public class SysPositionDataScopeService extends ServiceImpl<SysPositionDataScopeMapper, SysPositionDataScope> {

    private static final String LOCK_KEY = "passiflora:lock:sysPositionDataScope:";

    @Nonnull
    public Page<SysPositionDataScope> page(@Nonnull QueryCondition<SysPositionDataScope> condition) {
        return baseMapper.page(
                condition.page(),
                condition.searchWrapper(SysPositionDataScope.class),
                condition.sortWrapper(SysPositionDataScope.class));
    }

    public void add(@Nonnull SysPositionDataScope sysPositionDataScope) {
        LockUtil.lock(LOCK_KEY, new LockWrapper<SysPositionDataScope>(), true, () -> {
            OnlyFieldCheck.checkInsert(baseMapper, sysPositionDataScope);
            baseMapper.insert(sysPositionDataScope);
            return null;
        });
    }

    public boolean update(@Nonnull SysPositionDataScope sysPositionDataScope) {
        return LockUtil.lock(LOCK_KEY, new LockWrapper<SysPositionDataScope>(), true, () -> {
            OnlyFieldCheck.checkUpdate(baseMapper, sysPositionDataScope);
            int changeRowCount = baseMapper.updateById(sysPositionDataScope);
            return changeRowCount > 0;
        });
    }

    @Transactional(rollbackFor = Exception.class)
    public int deleteByIds(@Nonnull Collection<String> scopeIds) {
        return baseMapper.deleteByIds(scopeIds, CurrentUtil.getCurrentUserId());
    }

    @Nonnull
    public SysPositionDataScope detail(@Nonnull String scopeId) {
        SysPositionDataScope sysPositionDataScope = baseMapper.selectById(scopeId);
        if (sysPositionDataScope == null) {
            throw new BizException("无对应职位数据权限数据，请刷新后重试");
        }
        return sysPositionDataScope;
    }
}
