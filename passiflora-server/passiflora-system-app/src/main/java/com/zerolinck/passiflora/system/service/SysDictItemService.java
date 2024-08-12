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
import com.zerolinck.passiflora.common.api.ResultCodeEnum;
import com.zerolinck.passiflora.common.exception.BizException;
import com.zerolinck.passiflora.common.util.CurrentUtil;
import com.zerolinck.passiflora.common.util.QueryCondition;
import com.zerolinck.passiflora.common.util.lock.LockUtil;
import com.zerolinck.passiflora.common.util.lock.LockWrapper;
import com.zerolinck.passiflora.model.common.enums.YesOrNoEnum;
import com.zerolinck.passiflora.model.system.entity.SysDict;
import com.zerolinck.passiflora.model.system.entity.SysDictItem;
import com.zerolinck.passiflora.system.mapper.SysDictItemMapper;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
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
@RequiredArgsConstructor
public class SysDictItemService extends ServiceImpl<SysDictItemMapper, SysDictItem> {

    private final SysDictService sysDictService;
    private static final String LOCK_KEY = "passiflora:lock:sysDictItem:";

    /**
     * 分页查询
     *
     * @param condition 搜索条件
     * @since 2024-08-12
     */
    @Nonnull
    public Page<SysDictItem> page(@Nullable QueryCondition<SysDictItem> condition) {
        condition = Objects.requireNonNullElse(condition, new QueryCondition<>());
        return baseMapper.page(
                condition.page(), condition.searchWrapper(SysDictItem.class), condition.sortWrapper(SysDictItem.class));
    }

    @CacheEvict(cacheNames = "passiflora:dict", allEntries = true)
    public void add(@Nonnull SysDictItem sysDictItem) {
        SysDict sysDict = sysDictService
                .detail(sysDictItem.getDictId())
                .orElseThrow(() -> new BizException(ResultCodeEnum.INVALID_PARAM_FAILED, "无此字典"));

        LockWrapper<SysDictItem> lockWrapper =
                new LockWrapper<SysDictItem>().lock(SysDictItem::getLabel, sysDictItem.getLabel());
        if (YesOrNoEnum.NO.equals(sysDict.getValueIsOnly())) {
            lockWrapper.lock(SysDictItem::getValue, sysDictItem.getValue());
        }

        LockUtil.lock(LOCK_KEY + sysDict.getDictTag(), lockWrapper, true, () -> {
            Long count = baseMapper.selectCount(new LambdaQueryWrapper<SysDictItem>()
                    .eq(SysDictItem::getLabel, sysDictItem.getLabel())
                    .eq(SysDictItem::getDictId, sysDictItem.getDictId()));
            if (count > 0) {
                throw new BizException("字典项标签重复，请重新填写");
            }

            SysDict dict = sysDictService.getById(sysDictItem.getDictId());
            if (YesOrNoEnum.NO.equals(dict.getValueIsOnly())) {
                count = baseMapper.selectCount(new LambdaQueryWrapper<SysDictItem>()
                        .eq(SysDictItem::getValue, sysDictItem.getValue())
                        .eq(SysDictItem::getDictId, sysDictItem.getDictId()));
                if (count > 0) {
                    throw new BizException("字典项标签重复，请重新填写");
                }
            }

            baseMapper.insert(sysDictItem);
            return null;
        });
    }

    @CacheEvict(cacheNames = "passiflora:dict", allEntries = true)
    public boolean update(@Nonnull SysDictItem sysDictItem) {
        SysDict sysDict = sysDictService
                .detail(sysDictItem.getDictId())
                .orElseThrow(() -> new BizException(ResultCodeEnum.INVALID_PARAM_FAILED, "无此字典"));

        LockWrapper<SysDictItem> lockWrapper =
                new LockWrapper<SysDictItem>().lock(SysDictItem::getLabel, sysDictItem.getLabel());
        if (YesOrNoEnum.NO.equals(sysDict.getValueIsOnly())) {
            lockWrapper.lock(SysDictItem::getValue, sysDictItem.getValue());
        }

        return LockUtil.lock(LOCK_KEY + sysDict.getDictTag(), lockWrapper, true, () -> {
            SysDictItem dbSysDictItem = baseMapper.selectById(sysDictItem.getDictItemId());
            if (YesOrNoEnum.YES.equals(sysDictItem.getIsSystem())) {
                throw new BizException("系统内置数据，不允许修改");
            }

            if (sysDictItem.getLabel() != null && !sysDictItem.getLabel().equals(dbSysDictItem.getLabel())) {
                Long count = baseMapper.selectCount(new LambdaQueryWrapper<SysDictItem>()
                        .eq(SysDictItem::getLabel, sysDictItem.getLabel())
                        .eq(SysDictItem::getDictId, sysDictItem.getDictId())
                        .ne(SysDictItem::getDictId, sysDictItem.getDictId()));
                if (count > 0) {
                    throw new BizException("字典值名称重复，请重新填写");
                }
            }

            SysDict dict = sysDictService.getById(sysDictItem.getDictId());
            if (YesOrNoEnum.NO.equals(dict.getValueIsOnly())) {
                Long count = baseMapper.selectCount(new LambdaQueryWrapper<SysDictItem>()
                        .eq(SysDictItem::getValue, sysDictItem.getValue())
                        .eq(SysDictItem::getDictId, sysDictItem.getDictId())
                        .ne(SysDictItem::getDictItemId, sysDictItem.getDictItemId()));
                if (count > 0) {
                    throw new BizException("字典项标签重复，请重新填写");
                }
            }

            int changeRowCount = baseMapper.updateById(sysDictItem);
            return changeRowCount > 0;
        });
    }

    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "passiflora:dict", allEntries = true)
    public int deleteByIds(@Nonnull Collection<String> dictItemIds) {
        List<SysDictItem> sysDictItems = baseMapper.selectBatchIds(dictItemIds);
        sysDictItems.forEach(sysDictItem -> {
            if (YesOrNoEnum.YES.equals(sysDictItem.getIsSystem())) {
                throw new BizException("系统内置数据，不允许删除");
            }
        });
        return baseMapper.deleteByIds(dictItemIds, CurrentUtil.getCurrentUserId());
    }

    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "passiflora:dict", allEntries = true)
    public void deleteByDictIds(@Nullable Collection<String> dictIds) {
        if (CollectionUtils.isEmpty(dictIds)) {
            return;
        }
        baseMapper.deleteByDictIds(dictIds, CurrentUtil.getCurrentUserId());
    }

    @Nonnull
    public Optional<SysDictItem> detail(@Nonnull String dictItemId) {
        return Optional.ofNullable(baseMapper.selectById(dictItemId));
    }

    @Nonnull
    @Cacheable(value = "passiflora:dict:id", key = "#dictId")
    public List<SysDictItem> listByDictId(@Nonnull String dictId) {
        return baseMapper.listByDictId(dictId);
    }

    @Nonnull
    @Cacheable(value = "passiflora:dict:name", key = "#dictName")
    public List<SysDictItem> listByDictName(@Nonnull String dictName) {
        return baseMapper.listByDictName(dictName);
    }

    @Nonnull
    @Cacheable(value = "passiflora:dict:tag", key = "#dictTag")
    public List<SysDictItem> listByDictTag(@Nonnull String dictTag) {
        return baseMapper.listByDictTag(dictTag);
    }
}
