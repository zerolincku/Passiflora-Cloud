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

import java.util.Collection;
import java.util.List;
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
import com.zerolinck.passiflora.mybatis.util.UniqueFieldChecker;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** @author linck on 2024-04-01 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IamDictService {
    private final IamDictMapper mapper;
    private final IamDictItemMapper dictItemMapper;

    private static final String LOCK_KEY = "passiflora:lock:iamDict:";

    /**
     * 分页查询字典
     *
     * @param condition 查询条件
     * @return 字典的分页结果
     * @since 2024-04-01
     */
    @NotNull public Page<IamDict> page(@Nullable Condition<IamDict> condition) {
        return mapper.page(condition);
    }

    /**
     * 新增字典
     *
     * @param iamDict 字典
     * @since 2024-04-01
     */
    public void add(@NotNull IamDict iamDict) {
        LockUtils.lock(
                LOCK_KEY,
                new LockWrapper<IamDict>()
                        .lock(IamDict::getDictName, iamDict.getDictName())
                        .lock(IamDict::getDictTag, iamDict.getDictTag()),
                true,
                () -> {
                    UniqueFieldChecker.checkInsert(mapper, iamDict);
                    mapper.insert(iamDict);
                });
    }

    /**
     * 更新字典
     *
     * @param iamDict 字典
     * @return 如果更新成功返回true，否则返回false
     * @since 2024-04-01
     */
    @CacheEvict(cacheNames = "passiflora:dict", allEntries = true)
    public boolean update(@NotNull IamDict iamDict) {
        return LockUtils.lock(
                LOCK_KEY,
                new LockWrapper<IamDict>()
                        .lock(IamDict::getDictName, iamDict.getDictName())
                        .lock(IamDict::getDictTag, iamDict.getDictTag()),
                true,
                () -> {
                    IamDict dbIamDict = mapper.selectOneById(iamDict.getDictId());
                    if (YesOrNoEnum.YES.equals(dbIamDict.getIsSystem())) {
                        throw new BizException("系统内置数据，不允许修改");
                    }

                    UniqueFieldChecker.checkUpdate(mapper, iamDict);
                    int changeRowCount = mapper.update(iamDict);
                    return changeRowCount > 0;
                });
    }

    /**
     * 根据字典ID集合删除字典
     *
     * @param dictIds 字典ID集合
     * @return 删除的字典数量
     * @since 2024-04-01
     */
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "passiflora:dict", allEntries = true)
    public int deleteByIds(@NotNull Collection<String> dictIds) {
        List<IamDict> iamDicts = mapper.selectListByIds(dictIds);
        iamDicts.forEach(iamDict -> {
            if (YesOrNoEnum.YES.equals(iamDict.getIsSystem())) {
                throw new BizException("系统内置数据，不允许删除");
            }
        });
        int rowCount = mapper.deleteBatchByIds(dictIds, 500);
        dictItemMapper.deleteByDictIds(dictIds);
        return rowCount;
    }

    /**
     * 根据字典ID获取字典的详细信息
     *
     * @param dictId 字典ID
     * @return 包含字典的Optional对象
     * @since 2024-04-01
     */
    @NotNull public Optional<IamDict> detail(@NotNull String dictId) {
        return Optional.ofNullable(mapper.selectOneById(dictId));
    }
}
