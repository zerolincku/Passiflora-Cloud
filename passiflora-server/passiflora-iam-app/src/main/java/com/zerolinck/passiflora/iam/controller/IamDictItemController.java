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

import com.zerolinck.passiflora.common.api.Result;
import com.zerolinck.passiflora.common.api.ResultCodeEnum;
import com.zerolinck.passiflora.common.util.AssertUtil;
import com.zerolinck.passiflora.common.util.QueryCondition;
import com.zerolinck.passiflora.feign.iam.IamDictItemApi;
import com.zerolinck.passiflora.iam.service.IamDictItemService;
import com.zerolinck.passiflora.model.iam.entity.IamDictItem;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author linck
 * @since 2024-04-01
 */
@RestController
@RequestMapping("iam-dict-item")
@RequiredArgsConstructor
public class IamDictItemController implements IamDictItemApi {

    private final IamDictItemService iamDictItemService;

    @Override
    public Result<List<IamDictItem>> page(QueryCondition<IamDictItem> condition) {
        return Result.ok(iamDictItemService.page(condition));
    }

    @Override
    public Result<String> add(IamDictItem iamDictItem) {
        iamDictItem.setDictItemId(null);
        iamDictItemService.add(iamDictItem);
        return Result.ok(iamDictItem.getDictItemId());
    }

    @Override
    public Result<String> update(IamDictItem iamDictItem) {
        boolean success = iamDictItemService.update(iamDictItem);
        if (success) {
            return Result.ok(iamDictItem.getDictItemId());
        } else {
            return Result.failed(ResultCodeEnum.COMPETE_FAILED);
        }
    }

    @Override
    public Result<IamDictItem> detail(String dictItemId) {
        AssertUtil.notBlank(dictItemId, "字典项 ID 不能为空");
        return Result.ok(iamDictItemService.detail(dictItemId).orElseThrow(() -> new NoSuchElementException("字典项不存在")));
    }

    @Override
    public Result<String> delete(List<String> dictItemIds) {
        AssertUtil.notEmpty(dictItemIds, "字典项 ID 不能为空");
        iamDictItemService.deleteByIds(dictItemIds);
        return Result.ok();
    }

    @Override
    public Result<List<IamDictItem>> listByDictId(String dictId) {
        AssertUtil.notBlank(dictId, "字典 ID 不能为空");
        return Result.ok(iamDictItemService.listByDictId(dictId));
    }

    @Override
    public Result<List<IamDictItem>> listByDictName(String dictName) {
        AssertUtil.notBlank(dictName, "字典名称不能为空");
        return Result.ok(iamDictItemService.listByDictName(dictName));
    }

    @Override
    public Result<List<IamDictItem>> listByDictTag(String dictTag) {
        AssertUtil.notBlank(dictTag, "字典标签不能为空");
        return Result.ok(iamDictItemService.listByDictTag(dictTag));
    }
}
