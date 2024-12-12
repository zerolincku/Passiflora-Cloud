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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import com.zerolinck.passiflora.common.api.Result;
import com.zerolinck.passiflora.common.util.QueryCondition;
import com.zerolinck.passiflora.feign.config.FeignConfiguration;
import com.zerolinck.passiflora.model.iam.entity.IamOrg;
import com.zerolinck.passiflora.model.iam.resp.IamOrgResp;
import com.zerolinck.passiflora.model.valid.Insert;
import com.zerolinck.passiflora.model.valid.Update;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/** @author linck on 2024-04-09 */
@Tag(name = "机构")
@FeignClient(
        value = "passiflora-iam-app",
        contextId = "iamOrg",
        path = "/passiflora/iam-api/iam-org",
        configuration = FeignConfiguration.class)
public interface IamOrgApi {
    @Operation(summary = "分页查询")
    @GetMapping("page")
    Result<List<IamOrg>> page(@NotNull @SpringQueryMap QueryCondition<IamOrg> condition);

    @Operation(summary = "新增")
    @PostMapping("add")
    Result<String> add(@RequestBody @Validated(Insert.class) IamOrg iamOrg);

    @Operation(summary = "更新")
    @PostMapping("update")
    Result<String> update(@RequestBody @Validated(Update.class) IamOrg iamOrg);

    @Operation(summary = "详情")
    @GetMapping("detail")
    Result<IamOrg> detail(@Nullable @RequestParam(value = "orgId", required = false) String orgId);

    /** 此方法会级联删除下级机构 */
    @Operation(summary = "删除")
    @PostMapping("delete")
    Result<Void> delete(@RequestBody List<String> orgIds);

    @Operation(summary = "根据父级ID查询子机构列表")
    @GetMapping("list-by-parent-id")
    Result<List<IamOrgResp>> listByParentId(@RequestParam(required = false, value = "orgParentId") String orgParentId);

    @Operation(summary = "机构树")
    @GetMapping("org-tree")
    Result<List<IamOrgResp>> orgTree();
}
