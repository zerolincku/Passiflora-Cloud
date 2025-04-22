/* 
 * Copyright (C) 2025 Linck. <zerolinck@foxmail.com>
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

import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import com.zerolinck.passiflora.base.enums.YesOrNoEnum;
import com.zerolinck.passiflora.common.api.Page;
import com.zerolinck.passiflora.common.exception.BizException;
import com.zerolinck.passiflora.common.util.Condition;
import com.zerolinck.passiflora.common.util.lock.LockUtils;
import com.zerolinck.passiflora.common.util.lock.LockWrapper;
import com.zerolinck.passiflora.iam.mapper.IamDictItemMapper;
import com.zerolinck.passiflora.iam.mapper.IamDictMapper;
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
public class IamDictItemService {
    private final IamDictItemMapper mapper;
    private final IamDictMapper dictMapper;
    private static final String LOCK_KEY = "passiflora:lock:iamDictItem:";

    /**
     * 分页查询字典项
     *
     * @param condition 搜索条件
     * @return 字典项的分页结果
     * @since 2024-08-12
     */
    @NotNull public Page<IamDictItem> page(@Nullable Condition<IamDictItem> condition) {
        return mapper.page(condition);
    }

    /**
     * 新增字典项
     *
     * @param iamDictItem 字典项
     */
    @CacheEvict(cacheNames = "passiflora:dict", allEntries = true)
    public void add(@NotNull IamDictItem iamDictItem) {
        IamDict iamDict = dictMapper.selectOneById(iamDictItem.getDictId());
        if (iamDict == null) {
            throw new NoSuchElementException("无此字典");
        }
        LockWrapper<IamDictItem> lockWrapper =
                new LockWrapper<IamDictItem>().lock(IamDictItem::getLabel, iamDictItem.getLabel());
        if (YesOrNoEnum.NO.equals(iamDict.getValueIsOnly())) {
            lockWrapper.lock(IamDictItem::getValue, iamDictItem.getValue());
        }

        LockUtils.lock(LOCK_KEY + iamDict.getDictTag(), lockWrapper, true, () -> {
            long count = mapper.countByLabel(iamDictItem.getLabel(), iamDictItem.getDictId(), null);
            if (count > 0) {
                throw new BizException("字典项标签重复，请重新填写");
            }

            IamDict dict = dictMapper.selectOneById(iamDictItem.getDictId());
            if (YesOrNoEnum.NO.equals(dict.getValueIsOnly())) {
                count = mapper.countByValue(iamDictItem.getValue(), iamDictItem.getDictId(), null);
                if (count > 0) {
                    throw new BizException("字典项值重复，请重新填写");
                }
            }

            mapper.insert(iamDictItem);
        });
    }

    /**
     * 更新字典项
     *
     * @param iamDictItem 字典项
     * @return 如果更新成功返回true，否则返回false
     */
    @CacheEvict(cacheNames = "passiflora:dict", allEntries = true)
    public boolean update(@NotNull IamDictItem iamDictItem) {
        IamDict dict = dictMapper.selectOneById(iamDictItem.getDictId());
        if (dict == null) {
            throw new NoSuchElementException("无此字典");
        }

        LockWrapper<IamDictItem> lockWrapper =
                new LockWrapper<IamDictItem>().lock(IamDictItem::getLabel, iamDictItem.getLabel());
        if (YesOrNoEnum.NO.equals(dict.getValueIsOnly())) {
            lockWrapper.lock(IamDictItem::getValue, iamDictItem.getValue());
        }

        return LockUtils.lock(LOCK_KEY + dict.getDictTag(), lockWrapper, true, () -> {
            IamDictItem dbIamDictItem = mapper.selectOneById(iamDictItem.getDictItemId());
            if (YesOrNoEnum.YES.equals(iamDictItem.getIsSystem())) {
                throw new BizException("系统内置数据，不允许修改");
            }

            if (iamDictItem.getLabel() != null && !iamDictItem.getLabel().equals(dbIamDictItem.getLabel())) {
                long count = mapper.countByLabel(
                        iamDictItem.getLabel(), iamDictItem.getDictId(), iamDictItem.getDictItemId());
                if (count > 0) {
                    throw new BizException("字典项标签重复，请重新填写");
                }
            }

            if (YesOrNoEnum.NO.equals(dict.getValueIsOnly())) {
                long count = mapper.countByValue(
                        iamDictItem.getValue(), iamDictItem.getDictId(), iamDictItem.getDictItemId());
                if (count > 0) {
                    throw new BizException("字典项值重复，请重新填写");
                }
            }

            int changeRowCount = mapper.update(iamDictItem);
            return changeRowCount > 0;
        });
    }

    /**
     * 根据字典项ID集合删除字典项
     *
     * @param dictItemIds 字典项ID集合
     * @return 删除的字典项数量
     */
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

    /**
     * 根据字典ID集合删除字典项
     *
     * @param dictIds 字典ID集合
     */
    @SuppressWarnings("unused")
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "passiflora:dict", allEntries = true)
    public void deleteByDictIds(@Nullable Collection<String> dictIds) {
        mapper.deleteByDictIds(dictIds);
    }

    /**
     * 根据字典项ID获取字典项的详细信息
     *
     * @param dictItemId 字典项ID
     * @return 包含字典项的Optional对象
     */
    @NotNull public Optional<IamDictItem> detail(@NotNull String dictItemId) {
        return Optional.ofNullable(mapper.selectOneById(dictItemId));
    }

    /**
     * 根据字典ID获取字典项列表
     *
     * @param dictId 字典ID
     * @return 字典项列表
     */
    @NotNull @Cacheable(value = "passiflora:dict:id", key = "#dictId")
    public List<IamDictItem> listByDictId(@NotNull String dictId) {
        return mapper.listByDictId(dictId);
    }

    /**
     * 根据字典名称获取字典项列表
     *
     * @param dictName 字典名称
     * @return 字典项列表
     */
    @NotNull @Cacheable(value = "passiflora:dict:name", key = "#dictName")
    public List<IamDictItem> listByDictName(@NotNull String dictName) {
        return mapper.listByDictName(dictName);
    }

    /**
     * 根据字典标签获取字典项列表
     *
     * @param dictTag 字典标签
     * @return 字典项列表
     */
    @NotNull @Cacheable(value = "passiflora:dict:tag", key = "#dictTag")
    public List<IamDictItem> listByDictTag(@NotNull String dictTag) {
        return mapper.listByDictTag(dictTag);
    }
}
