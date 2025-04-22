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
package com.zerolinck.passiflora.iam.controller;

import java.util.List;
import java.util.NoSuchElementException;

import com.zerolinck.passiflora.common.api.Result;
import com.zerolinck.passiflora.common.api.ResultCode;
import com.zerolinck.passiflora.common.util.Asserts;
import com.zerolinck.passiflora.common.util.Condition;
import com.zerolinck.passiflora.feign.iam.IamDictApi;
import com.zerolinck.passiflora.iam.service.IamDictService;
import com.zerolinck.passiflora.model.iam.entity.IamDict;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

/** @author linck on 2024-04-01 */
@RestController
@RequestMapping("iam-dict")
@RequiredArgsConstructor
public class IamDictController implements IamDictApi {

    private final IamDictService iamDictService;

    @Override
    public Result<List<IamDict>> page(@NotNull Condition<IamDict> condition) {
        return Result.ok(iamDictService.page(condition));
    }

    @Override
    public Result<String> add(IamDict iamDict) {
        iamDict.setDictId(null);
        iamDictService.add(iamDict);
        return Result.ok(iamDict.getDictId());
    }

    @Override
    public Result<String> update(IamDict iamDict) {
        boolean success = iamDictService.update(iamDict);
        if (success) {
            return Result.ok(iamDict.getDictId());
        } else {
            return Result.failed(ResultCode.COMPETE_FAILED);
        }
    }

    @Override
    public Result<IamDict> detail(@Nullable String dictId) {
        Asserts.notBlank(dictId, "字典 ID 不能为空");
        return Result.ok(iamDictService.detail(dictId).orElseThrow(() -> new NoSuchElementException("字典不存在")));
    }

    @Override
    public Result<Void> delete(List<String> dictIds) {
        Asserts.notEmpty(dictIds, "字典 ID 不能为空");
        iamDictService.deleteByIds(dictIds);
        return Result.ok();
    }
}
