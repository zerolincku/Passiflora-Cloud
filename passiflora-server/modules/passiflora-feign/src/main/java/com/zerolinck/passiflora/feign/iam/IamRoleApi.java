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

import com.zerolinck.passiflora.common.api.Result;
import com.zerolinck.passiflora.common.util.QueryCondition;
import com.zerolinck.passiflora.feign.config.FeignConfiguration;
import com.zerolinck.passiflora.model.iam.args.RolePermissionSaveArgs;
import com.zerolinck.passiflora.model.iam.entity.IamRole;
import com.zerolinck.passiflora.model.valid.Insert;
import com.zerolinck.passiflora.model.valid.Update;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

/** @author 林常坤 on 2024-08-17 */
@Tag(name = "角色")
@FeignClient(
        value = "passiflora-iam-app",
        contextId = "iamRole",
        path = "/passiflora/iam-api/iam-role",
        configuration = FeignConfiguration.class)
public interface IamRoleApi {

    @NotNull @Operation(summary = "分页查询")
    @GetMapping("page")
    Result<List<IamRole>> page(@NotNull @SpringQueryMap QueryCondition<IamRole> condition);

    @NotNull @Operation(summary = "列表查询")
    @GetMapping("list")
    Result<List<IamRole>> list(@Nullable @SpringQueryMap QueryCondition<IamRole> condition);

    @NotNull @Operation(summary = "新增")
    @PostMapping("add")
    Result<String> add(@NotNull @RequestBody @Validated(Insert.class) IamRole iamRole);

    @NotNull @Operation(summary = "更新")
    @PostMapping("update")
    Result<String> update(@NotNull @RequestBody @Validated(Update.class) IamRole iamRole);

    @NotNull @Operation(summary = "详情")
    @GetMapping("detail")
    Result<IamRole> detail(@NotNull @RequestParam(value = "roleId") String roleId);

    @NotNull @Operation(summary = "删除")
    @PostMapping("delete")
    Result<Void> delete(@NotNull @RequestBody List<String> roleIds);

    @NotNull @Operation(summary = "根据角色ids获取权限ids")
    @PostMapping("permission-ids-by-role-ids")
    Result<List<String>> permissionIdsByRoleIds(@NotNull @RequestBody List<String> roleIds);

    @NotNull @Operation(summary = "保存角色权限")
    @PostMapping("save-role-permission")
    Result<Void> saveRolePermission(@NotNull @RequestBody @Validated RolePermissionSaveArgs args);

    @NotNull @Operation(summary = "禁用")
    @PostMapping("disable")
    Result<Void> disable(@NotNull @RequestBody List<String> roleIds);

    @NotNull @Operation(summary = "启用")
    @PostMapping("enable")
    Result<Void> enable(@NotNull @RequestBody List<String> roleIds);
}
