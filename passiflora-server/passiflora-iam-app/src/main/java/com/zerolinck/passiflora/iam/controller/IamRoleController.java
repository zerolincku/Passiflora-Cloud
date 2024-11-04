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
import com.zerolinck.passiflora.common.api.ResultCode;
import com.zerolinck.passiflora.common.util.Asserts;
import com.zerolinck.passiflora.common.util.QueryCondition;
import com.zerolinck.passiflora.feign.iam.IamRoleApi;
import com.zerolinck.passiflora.iam.service.IamRolePermissionService;
import com.zerolinck.passiflora.iam.service.IamRoleService;
import com.zerolinck.passiflora.model.iam.args.RolePermissionArgs;
import com.zerolinck.passiflora.model.iam.entity.IamRole;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 角色 Controller
 *
 * @author 林常坤 on 2024-08-17
 */
@Slf4j
@RestController
@RequestMapping("iam-role")
@RequiredArgsConstructor
public class IamRoleController implements IamRoleApi {

    private final IamRoleService iamRoleService;
    private final IamRolePermissionService iamRolePermissionService;

    @NotNull @Override
    public Result<List<IamRole>> page(@NotNull QueryCondition<IamRole> condition) {
        return Result.ok(iamRoleService.page(condition));
    }

    @NotNull @Override
    public Result<List<IamRole>> list(@Nullable QueryCondition<IamRole> condition) {
        return Result.ok(iamRoleService.list(condition));
    }

    @NotNull @Override
    public Result<String> add(@NotNull IamRole iamRole) {
        iamRole.setRoleId(null);
        iamRoleService.add(iamRole);
        return Result.ok(iamRole.getRoleId());
    }

    @NotNull @Override
    public Result<String> update(@NotNull IamRole iamRole) {
        boolean success = iamRoleService.update(iamRole);
        if (success) {
            return Result.ok(iamRole.getRoleId());
        } else {
            return Result.failed(ResultCode.COMPETE_FAILED);
        }
    }

    @NotNull @Override
    public Result<IamRole> detail(@Nullable String roleId) {
        Asserts.notBlank(roleId, "角色 ID 不能为空");
        return Result.ok(iamRoleService.detail(roleId).orElseThrow(() -> new NoSuchElementException("角色不存在")));
    }

    @NotNull @Override
    public Result<Void> delete(@NotNull List<String> roleIds) {
        Asserts.notEmpty(roleIds, "角色 ID 不能为空");
        iamRoleService.deleteByIds(roleIds);
        return Result.ok();
    }

    @NotNull @Override
    public Result<List<String>> permissionIdsByRoleIds(@NotNull List<String> roleIds) {
        Asserts.notEmpty(roleIds, "角色 ID 不能为空");
        return Result.ok(iamRolePermissionService.permissionIdsByRoleIds(roleIds));
    }

    @NotNull @Override
    public Result<Void> saveRolePermission(@NotNull RolePermissionArgs args) {
        iamRolePermissionService.saveRolePermission(args);
        return Result.ok();
    }

    @NotNull @Override
    public Result<Void> disable(@NotNull List<String> roleIds) {
        iamRoleService.disable(roleIds);
        return Result.ok();
    }

    @NotNull @Override
    public Result<Void> enable(@NotNull List<String> roleIds) {
        iamRoleService.enable(roleIds);
        return Result.ok();
    }
}
