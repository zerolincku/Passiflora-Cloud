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
import com.zerolinck.passiflora.common.util.QueryCondition;
import com.zerolinck.passiflora.feign.config.FeignConfiguration;
import com.zerolinck.passiflora.model.iam.entity.IamPermission;
import com.zerolinck.passiflora.model.iam.resp.IamPermissionResp;
import com.zerolinck.passiflora.model.iam.resp.IamPermissionTableResp;
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

/** @author linck on 2024-05-06 */
@Tag(name = "菜单")
@FeignClient(
        value = "passiflora-iam-app",
        contextId = "iamPermission",
        path = "/passiflora/iam-api/iam-permission",
        configuration = FeignConfiguration.class)
public interface IamPermissionApi {
    @Operation(summary = "分页查询")
    @GetMapping("page")
    Result<List<IamPermission>> page(@NotNull @SpringQueryMap QueryCondition<IamPermission> condition);

    @Operation(summary = "新增")
    @PostMapping("add")
    Result<String> add(@RequestBody @Validated(Insert.class) IamPermission iamPermission);

    @Operation(summary = "更新")
    @PostMapping("update")
    Result<String> update(@RequestBody @Validated(Update.class) IamPermission iamPermission);

    @Operation(summary = "详情")
    @GetMapping("detail")
    Result<IamPermission> detail(@Nullable @RequestParam(value = "permissionId", required = false) String permissionId);

    @Operation(summary = "删除")
    @PostMapping("delete")
    Result<Void> delete(@RequestBody List<String> permissionIds);

    @Operation(summary = "菜单树")
    @GetMapping("menu-tree")
    Result<List<IamPermissionResp>> menuTree();

    @Operation(summary = "权限树-列表使用")
    @GetMapping("permission-table-tree")
    Result<List<IamPermissionTableResp>> permissionTableTree();

    @Operation(summary = "更新排序")
    @PostMapping("update-order")
    Result<Void> updateOrder(
            @RequestBody @Validated(Update.class) List<IamPermissionTableResp> iamPermissionTableResps);

    @Operation(summary = "禁用")
    @PostMapping("disable")
    Result<Void> disable(@RequestBody List<String> permissionIds);

    @Operation(summary = "启用")
    @PostMapping("enable")
    Result<Void> enable(@RequestBody List<String> permissionIds);
}
