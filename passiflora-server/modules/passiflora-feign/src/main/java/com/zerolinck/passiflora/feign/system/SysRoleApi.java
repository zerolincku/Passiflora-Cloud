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
package com.zerolinck.passiflora.feign.system;

import com.zerolinck.passiflora.common.api.ListWithPage;
import com.zerolinck.passiflora.common.api.Result;
import com.zerolinck.passiflora.common.util.QueryCondition;
import com.zerolinck.passiflora.feign.config.FeignConfiguration;
import com.zerolinck.passiflora.model.system.args.RolePermissionSaveArgs;
import com.zerolinck.passiflora.model.system.entity.SysRole;
import com.zerolinck.passiflora.model.valid.Insert;
import com.zerolinck.passiflora.model.valid.Update;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author 林常坤
 * @since 2024-08-17
 */
@Tag(name = "角色")
@FeignClient(
        value = "sysRole",
        contextId = "sysRole",
        path = "/passiflora/system-api/sysRole",
        configuration = FeignConfiguration.class)
public interface SysRoleApi {

    @Nonnull
    @Operation(summary = "分页查询")
    @GetMapping("page")
    Result<ListWithPage<SysRole>> page(@Nullable QueryCondition<SysRole> condition);

    @Nonnull
    @Operation(summary = "列表查询")
    @GetMapping("list")
    Result<List<SysRole>> list(@Nullable QueryCondition<SysRole> condition);

    @Nonnull
    @Operation(summary = "新增")
    @PostMapping("add")
    Result<String> add(@Nonnull @RequestBody @Validated(Insert.class) SysRole sysRole);

    @Nonnull
    @Operation(summary = "更新")
    @PostMapping("update")
    Result<String> update(@Nonnull @RequestBody @Validated(Update.class) SysRole sysRole);

    @Nonnull
    @Operation(summary = "详情")
    @GetMapping("detail")
    Result<SysRole> detail(@Nonnull @RequestParam(value = "roleId") String roleId);

    @Nonnull
    @Operation(summary = "删除")
    @PostMapping("delete")
    Result<String> delete(@Nonnull @RequestBody List<String> roleIds);

    @Nonnull
    @Operation(summary = "根据角色ids获取权限ids")
    @PostMapping("permissionIdsByRoleIds")
    Result<List<String>> permissionIdsByRoleIds(@Nonnull @RequestBody List<String> roleIds);

    @Nonnull
    @Operation(summary = "保存角色权限")
    @PostMapping("saveRolePermission")
    Result<String> saveRolePermission(@Nonnull @RequestBody @Validated RolePermissionSaveArgs args);

    @Nonnull
    @Operation(summary = "禁用")
    @PostMapping("disable")
    Result<String> disable(@Nonnull @RequestBody List<String> roleIds);

    @Nonnull
    @Operation(summary = "启用")
    @PostMapping("enable")
    Result<String> enable(@Nonnull @RequestBody List<String> roleIds);
}
