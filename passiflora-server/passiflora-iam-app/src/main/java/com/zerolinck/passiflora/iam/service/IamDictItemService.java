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

import java.util.*;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.zerolinck.passiflora.base.enums.YesOrNoEnum;
import com.zerolinck.passiflora.common.api.ResultCode;
import com.zerolinck.passiflora.common.exception.BizException;
import com.zerolinck.passiflora.common.util.QueryCondition;
import com.zerolinck.passiflora.common.util.lock.LockUtil;
import com.zerolinck.passiflora.common.util.lock.LockWrapper;
import com.zerolinck.passiflora.iam.mapper.IamDictItemMapper;
import com.zerolinck.passiflora.model.iam.entity.IamDict;
import com.zerolinck.passiflora.model.iam.entity.IamDictItem;
import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** @author linck on 2024-04-01 */
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
    @NotNull public Page<IamDictItem> page(@Nullable QueryCondition<IamDictItem> condition) {
        condition = Objects.requireNonNullElse(condition, new QueryCondition<>());
        return mapper.paginate(
                condition.getPageNumber(), condition.getPageSize(), condition.searchWrapper(IamDictItem.class));
    }

    @CacheEvict(cacheNames = "passiflora:dict", allEntries = true)
    public void add(@NotNull IamDictItem iamDictItem) {
        IamDict iamDict =
                iamDictService.detail(iamDictItem.getDictId()).orElseThrow(() -> new NoSuchElementException("无此字典"));

        LockWrapper<IamDictItem> lockWrapper =
                new LockWrapper<IamDictItem>().lock(IamDictItem::getLabel, iamDictItem.getLabel());
        if (YesOrNoEnum.NO.equals(iamDict.getValueIsOnly())) {
            lockWrapper.lock(IamDictItem::getValue, iamDictItem.getValue());
        }

        LockUtil.lock(LOCK_KEY + iamDict.getDictTag(), lockWrapper, true, () -> {
            long count = mapper.selectCountByQuery(new QueryWrapper()
                    .eq(IamDictItem::getLabel, iamDictItem.getLabel())
                    .eq(IamDictItem::getDictId, iamDictItem.getDictId()));
            if (count > 0) {
                throw new BizException("字典项标签重复，请重新填写");
            }

            IamDict dict = iamDictService.getById(iamDictItem.getDictId());
            if (YesOrNoEnum.NO.equals(dict.getValueIsOnly())) {
                count = mapper.selectCountByQuery(new QueryWrapper()
                        .eq(IamDictItem::getValue, iamDictItem.getValue())
                        .eq(IamDictItem::getDictId, iamDictItem.getDictId()));
                if (count > 0) {
                    throw new BizException("字典项标签重复，请重新填写");
                }
            }

            mapper.insert(iamDictItem);
        });
    }

    @CacheEvict(cacheNames = "passiflora:dict", allEntries = true)
    public boolean update(@NotNull IamDictItem iamDictItem) {
        IamDict iamDict = iamDictService
                .detail(iamDictItem.getDictId())
                .orElseThrow(() -> new BizException(ResultCode.ILLEGAL_ARGUMENT, "无此字典"));

        LockWrapper<IamDictItem> lockWrapper =
                new LockWrapper<IamDictItem>().lock(IamDictItem::getLabel, iamDictItem.getLabel());
        if (YesOrNoEnum.NO.equals(iamDict.getValueIsOnly())) {
            lockWrapper.lock(IamDictItem::getValue, iamDictItem.getValue());
        }

        return LockUtil.lock(LOCK_KEY + iamDict.getDictTag(), lockWrapper, true, () -> {
            IamDictItem dbIamDictItem = mapper.selectOneById(iamDictItem.getDictItemId());
            if (YesOrNoEnum.YES.equals(iamDictItem.getIsSystem())) {
                throw new BizException("系统内置数据，不允许修改");
            }

            if (iamDictItem.getLabel() != null && !iamDictItem.getLabel().equals(dbIamDictItem.getLabel())) {
                long count = mapper.selectCountByQuery(new QueryWrapper()
                        .eq(IamDictItem::getLabel, iamDictItem.getLabel())
                        .eq(IamDictItem::getDictId, iamDictItem.getDictId())
                        .ne(IamDictItem::getDictId, iamDictItem.getDictId()));
                if (count > 0) {
                    throw new BizException("字典值名称重复，请重新填写");
                }
            }

            IamDict dict = iamDictService.getById(iamDictItem.getDictId());
            if (YesOrNoEnum.NO.equals(dict.getValueIsOnly())) {
                long count = mapper.selectCountByQuery(new QueryWrapper()
                        .eq(IamDictItem::getValue, iamDictItem.getValue())
                        .eq(IamDictItem::getDictId, iamDictItem.getDictId())
                        .ne(IamDictItem::getDictItemId, iamDictItem.getDictItemId()));
                if (count > 0) {
                    throw new BizException("字典项标签重复，请重新填写");
                }
            }

            int changeRowCount = mapper.update(iamDictItem);
            return changeRowCount > 0;
        });
    }

    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "passiflora:dict", allEntries = true)
    public int deleteByIds(@NotNull Collection<String> dictItemIds) {
        if (CollectionUtils.isEmpty(dictItemIds)) {
            return 0;
        }
        List<IamDictItem> iamDictItems = mapper.selectListByIds(dictItemIds);
        iamDictItems.forEach(iamDictItem -> {
            if (YesOrNoEnum.YES.equals(iamDictItem.getIsSystem())) {
                throw new BizException("系统内置数据，不允许删除");
            }
        });
        return mapper.deleteBatchByIds(dictItemIds, 500);
    }

    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "passiflora:dict", allEntries = true)
    public void deleteByDictIds(@Nullable Collection<String> dictIds) {
        if (CollectionUtils.isEmpty(dictIds)) {
            return;
        }
        mapper.deleteByDictIds(dictIds);
    }

    @NotNull public Optional<IamDictItem> detail(@NotNull String dictItemId) {
        return Optional.ofNullable(mapper.selectOneById(dictItemId));
    }

    @NotNull @Cacheable(value = "passiflora:dict:id", key = "#dictId")
    public List<IamDictItem> listByDictId(@NotNull String dictId) {
        return mapper.listByDictId(dictId);
    }

    @NotNull @Cacheable(value = "passiflora:dict:name", key = "#dictName")
    public List<IamDictItem> listByDictName(@NotNull String dictName) {
        return mapper.listByDictName(dictName);
    }

    @NotNull @Cacheable(value = "passiflora:dict:tag", key = "#dictTag")
    public List<IamDictItem> listByDictTag(@NotNull String dictTag) {
        return mapper.listByDictTag(dictTag);
    }
}
