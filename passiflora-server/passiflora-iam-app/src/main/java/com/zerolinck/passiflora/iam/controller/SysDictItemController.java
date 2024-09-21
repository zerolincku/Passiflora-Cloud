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
package com.zerolinck.passiflora.iam.controller;

import com.zerolinck.passiflora.common.api.ListWithPage;
import com.zerolinck.passiflora.common.api.Result;
import com.zerolinck.passiflora.common.api.ResultCodeEnum;
import com.zerolinck.passiflora.common.exception.BizException;
import com.zerolinck.passiflora.common.util.AssertUtil;
import com.zerolinck.passiflora.common.util.QueryCondition;
import com.zerolinck.passiflora.feign.iam.SysDictItemApi;
import com.zerolinck.passiflora.iam.service.SysDictItemService;
import com.zerolinck.passiflora.model.iam.entity.SysDictItem;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public Result<ListWithPage<SysDictItem>> page(QueryCondition<SysDictItem> condition) {
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
        AssertUtil.notBlank(dictItemId, "字典项 ID 不能为空");
        return Result.ok(sysDictItemService
                .detail(dictItemId)
                .orElseThrow(() -> new BizException(ResultCodeEnum.NO_MATCH_DATA)));
    }

    @Override
    public Result<String> delete(List<String> dictItemIds) {
        AssertUtil.notEmpty(dictItemIds, "字典项 ID 不能为空");
        sysDictItemService.deleteByIds(dictItemIds);
        return Result.ok();
    }

    @Override
    public Result<List<SysDictItem>> listByDictId(String dictId) {
        AssertUtil.notBlank(dictId, "字典 ID 不能为空");
        return Result.ok(sysDictItemService.listByDictId(dictId));
    }

    @Override
    public Result<List<SysDictItem>> listByDictName(String dictName) {
        AssertUtil.notBlank(dictName, "字典名称不能为空");
        return Result.ok(sysDictItemService.listByDictName(dictName));
    }

    @Override
    public Result<List<SysDictItem>> listByDictTag(String dictTag) {
        AssertUtil.notBlank(dictTag, "字典标签不能为空");
        return Result.ok(sysDictItemService.listByDictTag(dictTag));
    }
}
