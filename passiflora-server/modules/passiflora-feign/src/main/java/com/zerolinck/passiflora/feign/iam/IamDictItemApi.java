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
package com.zerolinck.passiflora.feign.iam;

import java.util.List;

import com.zerolinck.passiflora.base.valid.Insert;
import com.zerolinck.passiflora.base.valid.Update;
import com.zerolinck.passiflora.common.api.Result;
import com.zerolinck.passiflora.common.util.Condition;
import com.zerolinck.passiflora.feign.config.FeignConfiguration;
import com.zerolinck.passiflora.model.iam.entity.IamDictItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/** @author linck on 2024-04-01 */
@Tag(name = "字典项")
@FeignClient(
        value = "passiflora-iam-app",
        contextId = "iamDictItem",
        path = "/passiflora/iam-api/iam-dict-item",
        configuration = FeignConfiguration.class)
public interface IamDictItemApi {
    @Operation(summary = "分页查询")
    @GetMapping("page")
    Result<List<IamDictItem>> page(@NotNull @SpringQueryMap Condition<IamDictItem> condition);

    @Operation(summary = "新增")
    @PostMapping("add")
    Result<String> add(@RequestBody @Validated(Insert.class) IamDictItem iamDictItem);

    @Operation(summary = "更新")
    @PostMapping("update")
    Result<String> update(@RequestBody @Validated(Update.class) IamDictItem iamDictItem);

    @Operation(summary = "详情")
    @GetMapping("detail")
    Result<IamDictItem> detail(@Nullable @RequestParam(value = "dictItemId", required = false) String dictItemId);

    @Operation(summary = "删除")
    @PostMapping("delete")
    Result<Void> delete(@RequestBody List<String> dictItemIds);

    @Operation(summary = "根据字典ID查询")
    @GetMapping("list-by-dict-id")
    Result<List<IamDictItem>> listByDictId(@Nullable @RequestParam(value = "dictId", required = false) String dictId);

    @Operation(summary = "根据字典名称查询")
    @GetMapping("list-by-dict-name")
    Result<List<IamDictItem>> listByDictName(
            @Nullable @RequestParam(value = "dictName", required = false) String dictName);

    @Operation(summary = "根据字典标识查询")
    @GetMapping("list-by-dict-tag")
    Result<List<IamDictItem>> listByDictTag(
            @Nullable @RequestParam(value = "dictTag", required = false) String dictTag);
}
