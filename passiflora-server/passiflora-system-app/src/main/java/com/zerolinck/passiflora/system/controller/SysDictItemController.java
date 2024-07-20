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
package com.zerolinck.passiflora.system.controller;

import com.zerolinck.passiflora.common.api.ListWithPage;
import com.zerolinck.passiflora.common.api.Result;
import com.zerolinck.passiflora.common.api.ResultCodeEnum;
import com.zerolinck.passiflora.common.util.AssertUtil;
import com.zerolinck.passiflora.common.util.QueryCondition;
import com.zerolinck.passiflora.feign.system.SysDictItemApi;
import com.zerolinck.passiflora.model.system.entity.SysDictItem;
import com.zerolinck.passiflora.system.service.SysDictItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author linck
 * @since 2024-04-01
 */
@RestController
@RequestMapping("sysDictItem")
@RequiredArgsConstructor
public class SysDictItemController implements SysDictItemApi {
    private final SysDictItemService sysDictItemService;

    @Override
    public Result<ListWithPage<SysDictItem>> page(
        QueryCondition<SysDictItem> condition
    ) {
        return Result.page(sysDictItemService.page(condition));
    }

    @Override
    public Result<String> add(SysDictItem sysDictItem) {
        sysDictItem.setDictItemId(null);
        sysDictItemService.add(sysDictItem);
        return Result.ok(sysDictItem.getDictItemId());
    }

    @Override
    public Result<String> update(SysDictItem sysDictItem) {
        boolean success = sysDictItemService.update(sysDictItem);
        if (success) {
            return Result.ok(sysDictItem.getDictItemId());
        } else {
            return Result.failed(ResultCodeEnum.COMPETE_FAILED);
        }
    }

    @Override
    public Result<SysDictItem> detail(String dictItemId) {
        AssertUtil.notBlank(dictItemId);
        return Result.ok(sysDictItemService.detail(dictItemId));
    }

    @Override
    public Result<String> delete(List<String> dictItemIds) {
        AssertUtil.notEmpty(dictItemIds);
        sysDictItemService.deleteByIds(dictItemIds);
        return Result.ok();
    }

    @Override
    public Result<List<SysDictItem>> listByDictId(String dictId) {
        AssertUtil.notBlank(dictId);
        return Result.ok(sysDictItemService.listByDictId(dictId));
    }

    @Override
    public Result<List<SysDictItem>> listByDictName(String dictName) {
        AssertUtil.notBlank(dictName);
        return Result.ok(sysDictItemService.listByDictName(dictName));
    }

    @Override
    public Result<List<SysDictItem>> listByDictTag(String dictTag) {
        AssertUtil.notBlank(dictTag);
        return Result.ok(sysDictItemService.listByDictTag(dictTag));
    }
}
