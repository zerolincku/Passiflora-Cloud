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
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zerolinck.passiflora.common.exception.BizException;
import com.zerolinck.passiflora.common.util.CurrentUtil;
import com.zerolinck.passiflora.common.util.QueryCondition;
import com.zerolinck.passiflora.common.util.lock.LockUtil;
import com.zerolinck.passiflora.common.util.lock.LockWrapper;
import com.zerolinck.passiflora.model.common.enums.YesOrNoEnum;
import com.zerolinck.passiflora.model.system.entity.SysDict;
import com.zerolinck.passiflora.model.system.entity.SysDictItem;
import com.zerolinck.passiflora.system.mapper.SysDictItemMapper;
import jakarta.annotation.Resource;
import java.util.Collection;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author linck
 * @since 2024-04-01
 */
@Slf4j
@Service
public class SysDictItemService
    extends ServiceImpl<SysDictItemMapper, SysDictItem> {

    @Resource
    private SysDictService sysDictService;

    private static final String LOCK_KEY = "passiflora:lock:sysDictItem:";

    public Page<SysDictItem> page(QueryCondition<SysDictItem> condition) {
        if (condition == null) {
            condition = new QueryCondition<>();
        }
        return baseMapper.page(
            condition.page(),
            condition.searchWrapper(SysDictItem.class),
            condition.sortWrapper(SysDictItem.class)
        );
    }

    @CacheEvict(cacheNames = "passiflora:dict", allEntries = true)
    public void add(SysDictItem sysDictItem) {
        SysDict sysDict = sysDictService.detail(sysDictItem.getDictId());

        LockWrapper<SysDictItem> lockWrapper = new LockWrapper<SysDictItem>()
            .lock(SysDictItem::getLabel, sysDictItem.getLabel());
        if (YesOrNoEnum.NO.equals(sysDict.getValueIsOnly())) {
            lockWrapper.lock(SysDictItem::getValue, sysDictItem.getValue());
        }

        LockUtil.lockAndTransactionalLogic(
            LOCK_KEY + sysDict.getDictTag(),
            lockWrapper,
            () -> {
                Long count = baseMapper.selectCount(
                    new LambdaQueryWrapper<SysDictItem>()
                        .eq(SysDictItem::getLabel, sysDictItem.getLabel())
                        .eq(SysDictItem::getDictId, sysDictItem.getDictId())
                );
                if (count > 0) {
                    throw new BizException("字典项标签重复，请重新填写");
                }

                SysDict dict = sysDictService.getById(sysDictItem.getDictId());
                if (YesOrNoEnum.NO.equals(dict.getValueIsOnly())) {
                    count =
                    baseMapper.selectCount(
                        new LambdaQueryWrapper<SysDictItem>()
                            .eq(SysDictItem::getValue, sysDictItem.getValue())
                            .eq(SysDictItem::getDictId, sysDictItem.getDictId())
                    );
                    if (count > 0) {
                        throw new BizException("字典项标签重复，请重新填写");
                    }
                }

                baseMapper.insert(sysDictItem);
                return null;
            }
        );
    }

    @CacheEvict(cacheNames = "passiflora:dict", allEntries = true)
    public boolean update(SysDictItem sysDictItem) {
        SysDict sysDict = sysDictService.detail(sysDictItem.getDictId());

        LockWrapper<SysDictItem> lockWrapper = new LockWrapper<SysDictItem>()
            .lock(SysDictItem::getLabel, sysDictItem.getLabel());
        if (YesOrNoEnum.NO.equals(sysDict.getValueIsOnly())) {
            lockWrapper.lock(SysDictItem::getValue, sysDictItem.getValue());
        }

        return (boolean) LockUtil.lockAndTransactionalLogic(
            LOCK_KEY + sysDict.getDictTag(),
            lockWrapper,
            () -> {
                SysDictItem dbSysDictItem = baseMapper.selectById(
                    sysDictItem.getDictItemId()
                );
                if (YesOrNoEnum.YES.equals(sysDictItem.getIsSystem())) {
                    throw new BizException("系统内置数据，不允许修改");
                }

                if (
                    sysDictItem.getLabel() != null &&
                    !sysDictItem.getLabel().equals(dbSysDictItem.getLabel())
                ) {
                    Long count = baseMapper.selectCount(
                        new LambdaQueryWrapper<SysDictItem>()
                            .eq(SysDictItem::getLabel, sysDictItem.getLabel())
                            .eq(SysDictItem::getDictId, sysDictItem.getDictId())
                            .ne(SysDictItem::getDictId, sysDictItem.getDictId())
                    );
                    if (count > 0) {
                        throw new BizException("字典值名称重复，请重新填写");
                    }
                }

                SysDict dict = sysDictService.getById(sysDictItem.getDictId());
                if (YesOrNoEnum.NO.equals(dict.getValueIsOnly())) {
                    Long count = baseMapper.selectCount(
                        new LambdaQueryWrapper<SysDictItem>()
                            .eq(SysDictItem::getValue, sysDictItem.getValue())
                            .eq(SysDictItem::getDictId, sysDictItem.getDictId())
                            .ne(
                                SysDictItem::getDictItemId,
                                sysDictItem.getDictItemId()
                            )
                    );
                    if (count > 0) {
                        throw new BizException("字典项标签重复，请重新填写");
                    }
                }

                int changeRowCount = baseMapper.updateById(sysDictItem);
                return changeRowCount > 0;
            }
        );
    }

    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "passiflora:dict", allEntries = true)
    public int deleteByIds(Collection<String> dictItemIds) {
        List<SysDictItem> sysDictItems = baseMapper.selectBatchIds(dictItemIds);
        sysDictItems.forEach(sysDictItem -> {
            if (YesOrNoEnum.YES.equals(sysDictItem.getIsSystem())) {
                throw new BizException("系统内置数据，不允许删除");
            }
        });
        return baseMapper.deleteByIds(
            dictItemIds,
            CurrentUtil.getCurrentUserId()
        );
    }

    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "passiflora:dict", allEntries = true)
    public void deleteByDictIds(Collection<String> dictIds) {
        baseMapper.deleteByDictIds(dictIds, CurrentUtil.getCurrentUserId());
    }

    public SysDictItem detail(String dictItemId) {
        SysDictItem sysDictItem = baseMapper.selectById(dictItemId);
        if (sysDictItem == null) {
            throw new BizException("无对应字典项数据，请刷新后重试");
        }
        return sysDictItem;
    }

    @Cacheable(value = "passiflora:dict:id", key = "#dictId")
    public List<SysDictItem> listByDictId(String dictId) {
        return baseMapper.listByDictId(dictId);
    }

    @Cacheable(value = "passiflora:dict:name", key = "#dictName")
    public List<SysDictItem> listByDictName(String dictName) {
        return baseMapper.listByDictName(dictName);
    }

    @Cacheable(value = "passiflora:dict:tag", key = "#dictTag")
    public List<SysDictItem> listByDictTag(String dictTag) {
        return baseMapper.listByDictTag(dictTag);
    }
}
