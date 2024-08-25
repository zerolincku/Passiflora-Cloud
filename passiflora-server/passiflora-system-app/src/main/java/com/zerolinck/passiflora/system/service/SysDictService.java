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
import com.zerolinck.passiflora.model.common.enums.YesOrNoEnum;
import com.zerolinck.passiflora.model.system.entity.SysDict;
import com.zerolinck.passiflora.system.mapper.SysDictMapper;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author linck
 * @since 2024-04-01
 */
@Slf4j
@Service
public class SysDictService extends ServiceImpl<SysDictMapper, SysDict> {

    @Lazy
    @Resource
    private SysDictItemService sysDictItemService;

    private static final String LOCK_KEY = "passiflora:lock:sysDict:";

    @Nonnull
    public Page<SysDict> page(@Nullable QueryCondition<SysDict> condition) {
        condition = Objects.requireNonNullElse(condition, new QueryCondition<>());
        return baseMapper.page(
                condition.page(), condition.searchWrapper(SysDict.class), condition.sortWrapper(SysDict.class));
    }

    public void add(@Nonnull SysDict sysDict) {
        LockUtil.lock(
                LOCK_KEY,
                new LockWrapper<SysDict>()
                        .lock(SysDict::getDictName, sysDict.getDictName())
                        .lock(SysDict::getDictTag, sysDict.getDictTag()),
                true,
                () -> {
                    OnlyFieldCheck.checkInsert(baseMapper, sysDict);
                    baseMapper.insert(sysDict);
                });
    }

    @CacheEvict(cacheNames = "passiflora:dict", allEntries = true)
    public boolean update(@Nonnull SysDict sysDict) {
        return LockUtil.lock(
                LOCK_KEY,
                new LockWrapper<SysDict>()
                        .lock(SysDict::getDictName, sysDict.getDictName())
                        .lock(SysDict::getDictTag, sysDict.getDictTag()),
                true,
                () -> {
                    SysDict dbSysDict = baseMapper.selectById(sysDict.getDictId());
                    if (YesOrNoEnum.YES.equals(dbSysDict.getIsSystem())) {
                        throw new BizException("系统内置数据，不允许修改");
                    }

                    OnlyFieldCheck.checkUpdate(baseMapper, sysDict);
                    int changeRowCount = baseMapper.updateById(sysDict);
                    return changeRowCount > 0;
                });
    }

    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "passiflora:dict", allEntries = true)
    public int deleteByIds(@Nonnull Collection<String> dictIds) {
        List<SysDict> sysDicts = baseMapper.selectBatchIds(dictIds);
        sysDicts.forEach(sysDict -> {
            if (YesOrNoEnum.YES.equals(sysDict.getIsSystem())) {
                throw new BizException("系统内置数据，不允许删除");
            }
        });
        int rowCount = baseMapper.deleteByIds(dictIds, CurrentUtil.getCurrentUserId());
        sysDictItemService.deleteByDictIds(dictIds);
        return rowCount;
    }

    @Nonnull
    public Optional<SysDict> detail(@Nonnull String dictId) {
        return Optional.ofNullable(baseMapper.selectById(dictId));
    }
}
