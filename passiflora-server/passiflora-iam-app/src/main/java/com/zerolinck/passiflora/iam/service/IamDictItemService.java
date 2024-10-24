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
package com.zerolinck.passiflora.iam.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zerolinck.passiflora.common.api.ResultCodeEnum;
import com.zerolinck.passiflora.common.exception.BizException;
import com.zerolinck.passiflora.common.util.CurrentUtil;
import com.zerolinck.passiflora.common.util.QueryCondition;
import com.zerolinck.passiflora.common.util.lock.LockUtil;
import com.zerolinck.passiflora.common.util.lock.LockWrapper;
import com.zerolinck.passiflora.iam.mapper.IamDictItemMapper;
import com.zerolinck.passiflora.model.common.enums.YesOrNoEnum;
import com.zerolinck.passiflora.model.iam.entity.IamDict;
import com.zerolinck.passiflora.model.iam.entity.IamDictItem;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author linck on 2024-04-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IamDictItemService extends ServiceImpl<IamDictItemMapper, IamDictItem> {

    private final IamDictService iamDictService;
    private static final String LOCK_KEY = "passiflora:lock:iamDictItem:";

    /**
     * 分页查询
     *
     * @param condition 搜索条件
     * @since 2024-08-12
     */
    @Nonnull
    public Page<IamDictItem> page(@Nullable QueryCondition<IamDictItem> condition) {
        condition = Objects.requireNonNullElse(condition, new QueryCondition<>());
        return baseMapper.page(
                condition.page(), condition.searchWrapper(IamDictItem.class), condition.sortWrapper(IamDictItem.class));
    }

    @CacheEvict(cacheNames = "passiflora:dict", allEntries = true)
    public void add(@Nonnull IamDictItem iamDictItem) {
        IamDict iamDict =
                iamDictService.detail(iamDictItem.getDictId()).orElseThrow(() -> new NoSuchElementException("无此字典"));

        LockWrapper<IamDictItem> lockWrapper =
                new LockWrapper<IamDictItem>().lock(IamDictItem::getLabel, iamDictItem.getLabel());
        if (YesOrNoEnum.NO.equals(iamDict.getValueIsOnly())) {
            lockWrapper.lock(IamDictItem::getValue, iamDictItem.getValue());
        }

        LockUtil.lock(LOCK_KEY + iamDict.getDictTag(), lockWrapper, true, () -> {
            Long count = baseMapper.selectCount(new LambdaQueryWrapper<IamDictItem>()
                    .eq(IamDictItem::getLabel, iamDictItem.getLabel())
                    .eq(IamDictItem::getDictId, iamDictItem.getDictId()));
            if (count > 0) {
                throw new BizException("字典项标签重复，请重新填写");
            }

            IamDict dict = iamDictService.getById(iamDictItem.getDictId());
            if (YesOrNoEnum.NO.equals(dict.getValueIsOnly())) {
                count = baseMapper.selectCount(new LambdaQueryWrapper<IamDictItem>()
                        .eq(IamDictItem::getValue, iamDictItem.getValue())
                        .eq(IamDictItem::getDictId, iamDictItem.getDictId()));
                if (count > 0) {
                    throw new BizException("字典项标签重复，请重新填写");
                }
            }

            baseMapper.insert(iamDictItem);
        });
    }

    @CacheEvict(cacheNames = "passiflora:dict", allEntries = true)
    public boolean update(@Nonnull IamDictItem iamDictItem) {
        IamDict iamDict = iamDictService
                .detail(iamDictItem.getDictId())
                .orElseThrow(() -> new BizException(ResultCodeEnum.ILLEGAL_ARGUMENT, "无此字典"));

        LockWrapper<IamDictItem> lockWrapper =
                new LockWrapper<IamDictItem>().lock(IamDictItem::getLabel, iamDictItem.getLabel());
        if (YesOrNoEnum.NO.equals(iamDict.getValueIsOnly())) {
            lockWrapper.lock(IamDictItem::getValue, iamDictItem.getValue());
        }

        return LockUtil.lock(LOCK_KEY + iamDict.getDictTag(), lockWrapper, true, () -> {
            IamDictItem dbIamDictItem = baseMapper.selectById(iamDictItem.getDictItemId());
            if (YesOrNoEnum.YES.equals(iamDictItem.getIsSystem())) {
                throw new BizException("系统内置数据，不允许修改");
            }

            if (iamDictItem.getLabel() != null && !iamDictItem.getLabel().equals(dbIamDictItem.getLabel())) {
                Long count = baseMapper.selectCount(new LambdaQueryWrapper<IamDictItem>()
                        .eq(IamDictItem::getLabel, iamDictItem.getLabel())
                        .eq(IamDictItem::getDictId, iamDictItem.getDictId())
                        .ne(IamDictItem::getDictId, iamDictItem.getDictId()));
                if (count > 0) {
                    throw new BizException("字典值名称重复，请重新填写");
                }
            }

            IamDict dict = iamDictService.getById(iamDictItem.getDictId());
            if (YesOrNoEnum.NO.equals(dict.getValueIsOnly())) {
                Long count = baseMapper.selectCount(new LambdaQueryWrapper<IamDictItem>()
                        .eq(IamDictItem::getValue, iamDictItem.getValue())
                        .eq(IamDictItem::getDictId, iamDictItem.getDictId())
                        .ne(IamDictItem::getDictItemId, iamDictItem.getDictItemId()));
                if (count > 0) {
                    throw new BizException("字典项标签重复，请重新填写");
                }
            }

            int changeRowCount = baseMapper.updateById(iamDictItem);
            return changeRowCount > 0;
        });
    }

    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "passiflora:dict", allEntries = true)
    public int deleteByIds(@Nonnull Collection<String> dictItemIds) {
        List<IamDictItem> iamDictItems = baseMapper.selectBatchIds(dictItemIds);
        iamDictItems.forEach(iamDictItem -> {
            if (YesOrNoEnum.YES.equals(iamDictItem.getIsSystem())) {
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
    public Optional<IamDictItem> detail(@Nonnull String dictItemId) {
        return Optional.ofNullable(baseMapper.selectById(dictItemId));
    }

    @Nonnull
    @Cacheable(value = "passiflora:dict:id", key = "#dictId")
    public List<IamDictItem> listByDictId(@Nonnull String dictId) {
        return baseMapper.listByDictId(dictId);
    }

    @Nonnull
    @Cacheable(value = "passiflora:dict:name", key = "#dictName")
    public List<IamDictItem> listByDictName(@Nonnull String dictName) {
        return baseMapper.listByDictName(dictName);
    }

    @Nonnull
    @Cacheable(value = "passiflora:dict:tag", key = "#dictTag")
    public List<IamDictItem> listByDictTag(@Nonnull String dictTag) {
        return baseMapper.listByDictTag(dictTag);
    }
}
