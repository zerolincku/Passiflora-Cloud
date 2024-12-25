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
import java.util.Objects;
import java.util.Optional;
import jakarta.annotation.Resource;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.zerolinck.passiflora.base.enums.YesOrNoEnum;
import com.zerolinck.passiflora.common.exception.BizException;
import com.zerolinck.passiflora.common.util.OnlyFieldCheck;
import com.zerolinck.passiflora.common.util.QueryCondition;
import com.zerolinck.passiflora.common.util.lock.LockUtil;
import com.zerolinck.passiflora.common.util.lock.LockWrapper;
import com.zerolinck.passiflora.iam.mapper.IamDictMapper;
import com.zerolinck.passiflora.model.iam.entity.IamDict;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

/** @author linck on 2024-04-01 */
@Slf4j
@Service
public class IamDictService extends ServiceImpl<IamDictMapper, IamDict> {

    @Lazy
    @Resource
    private IamDictItemService iamDictItemService;

    private static final String LOCK_KEY = "passiflora:lock:iamDict:";

    @NotNull public Page<IamDict> page(@Nullable QueryCondition<IamDict> condition) {
        condition = Objects.requireNonNullElse(condition, new QueryCondition<>());
        return mapper.paginate(
                condition.getPageNumber(), condition.getPageSize(), condition.searchWrapper(IamDict.class));
    }

    public void add(@NotNull IamDict iamDict) {
        LockUtil.lock(
                LOCK_KEY,
                new LockWrapper<IamDict>()
                        .lock(IamDict::getDictName, iamDict.getDictName())
                        .lock(IamDict::getDictTag, iamDict.getDictTag()),
                true,
                () -> {
                    OnlyFieldCheck.checkInsert(mapper, iamDict);
                    mapper.insert(iamDict);
                });
    }

    @CacheEvict(cacheNames = "passiflora:dict", allEntries = true)
    public boolean update(@NotNull IamDict iamDict) {
        return LockUtil.lock(
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

                    OnlyFieldCheck.checkUpdate(mapper, iamDict);
                    int changeRowCount = mapper.update(iamDict);
                    return changeRowCount > 0;
                });
    }

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
        iamDictItemService.deleteByDictIds(dictIds);
        return rowCount;
    }

    @NotNull public Optional<IamDict> detail(@NotNull String dictId) {
        return Optional.ofNullable(mapper.selectOneById(dictId));
    }
}
