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
import com.zerolinck.passiflora.feign.iam.IamRoleApi;
import com.zerolinck.passiflora.iam.service.IamRolePermissionService;
import com.zerolinck.passiflora.iam.service.IamRoleService;
import com.zerolinck.passiflora.model.iam.args.RolePermissionSaveArgs;
import com.zerolinck.passiflora.model.iam.entity.IamRole;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 角色 Controller
 *
 * @author 林常坤
 * @since 2024-08-17
 */
@Slf4j
@RestController
@RequestMapping("sysRole")
@RequiredArgsConstructor
public class IamRoleController implements IamRoleApi {

    private final IamRoleService iamRoleService;
    private final IamRolePermissionService iamRolePermissionService;

    @Nonnull
    @Override
    public Result<ListWithPage<IamRole>> page(@Nullable QueryCondition<IamRole> condition) {
        return Result.page(iamRoleService.page(condition));
    }

    @Nonnull
    @Override
    public Result<List<IamRole>> list(@Nullable QueryCondition<IamRole> condition) {
        return Result.ok(iamRoleService.list(condition));
    }

    @Nonnull
    @Override
    public Result<String> add(@Nonnull IamRole iamRole) {
        iamRole.setRoleId(null);
        iamRoleService.add(iamRole);
        return Result.ok(iamRole.getRoleId());
    }

    @Nonnull
    @Override
    public Result<String> update(@Nonnull IamRole iamRole) {
        boolean success = iamRoleService.update(iamRole);
        if (success) {
            return Result.ok(iamRole.getRoleId());
        } else {
            return Result.failed(ResultCodeEnum.COMPETE_FAILED);
        }
    }

    @Nonnull
    @Override
    public Result<IamRole> detail(@Nonnull String roleId) {
        AssertUtil.notBlank(roleId, "角色 ID 不能为空");
        return Result.ok(
                iamRoleService.detail(roleId).orElseThrow(() -> new BizException(ResultCodeEnum.NO_MATCH_DATA)));
    }

    @Nonnull
    @Override
    public Result<String> delete(@Nonnull List<String> roleIds) {
        AssertUtil.notEmpty(roleIds, "角色 ID 不能为空");
        iamRoleService.deleteByIds(roleIds);
        return Result.ok();
    }

    @Nonnull
    @Override
    public Result<List<String>> permissionIdsByRoleIds(@Nonnull List<String> roleIds) {
        AssertUtil.notEmpty(roleIds, "角色 ID 不能为空");
        return Result.ok(iamRolePermissionService.permissionIdsByRoleIds(roleIds));
    }

    @Nonnull
    @Override
    public Result<String> saveRolePermission(@Nonnull RolePermissionSaveArgs args) {
        iamRolePermissionService.saveRolePermission(args);
        return Result.ok();
    }

    @Nonnull
    @Override
    public Result<String> disable(@Nonnull List<String> roleIds) {
        iamRoleService.disable(roleIds);
        return Result.ok();
    }

    @Nonnull
    @Override
    public Result<String> enable(@Nonnull List<String> roleIds) {
        iamRoleService.enable(roleIds);
        return Result.ok();
    }
}
