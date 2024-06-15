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
import com.zerolinck.passiflora.model.system.entity.SysPositionPermission;
import com.zerolinck.passiflora.system.mapper.SysPositionPermissionMapper;
import java.util.Collection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author linck
 * @since 2024-05-06
 */
@Slf4j
@Service
public class SysPositionPermissionService
    extends ServiceImpl<SysPositionPermissionMapper, SysPositionPermission> {

    private static final String LOCK_KEY =
        "passiflora:lock:sysPositionPermission:";

    public Page<SysPositionPermission> page(
        QueryCondition<SysPositionPermission> condition
    ) {
        if (condition == null) {
            condition = new QueryCondition<>();
        }
        return baseMapper.page(
            condition.page(),
            condition.searchWrapper(SysPositionPermission.class),
            condition.sortWrapper(SysPositionPermission.class)
        );
    }

    public void add(SysPositionPermission sysPositionPermission) {
        LockUtil.lockAndTransactionalLogic(
            LOCK_KEY,
            new LockWrapper<SysPositionPermission>(),
            () -> {
                OnlyFieldCheck.checkInsert(baseMapper, sysPositionPermission);
                baseMapper.insert(sysPositionPermission);
                return null;
            }
        );
    }

    public boolean update(SysPositionPermission sysPositionPermission) {
        return (boolean) LockUtil.lockAndTransactionalLogic(
            LOCK_KEY,
            new LockWrapper<SysPositionPermission>(),
            () -> {
                OnlyFieldCheck.checkUpdate(baseMapper, sysPositionPermission);
                int changeRowCount = baseMapper.updateById(
                    sysPositionPermission
                );
                return changeRowCount > 0;
            }
        );
    }

    @Transactional(rollbackFor = Exception.class)
    public int deleteByIds(Collection<String> bindIds) {
        return baseMapper.deleteByIds(bindIds, CurrentUtil.getCurrentUserId());
    }

    @Transactional(rollbackFor = Exception.class)
    public int deleteByPositionIds(Collection<String> positionIds) {
        return baseMapper.deleteByPositionIds(
            positionIds,
            CurrentUtil.getCurrentUserId()
        );
    }

    public SysPositionPermission detail(String bindId) {
        SysPositionPermission sysPositionPermission = baseMapper.selectById(
            bindId
        );
        if (sysPositionPermission == null) {
            throw new BizException("无对应系统职位菜单绑定数据，请刷新后重试");
        }
        return sysPositionPermission;
    }
}
