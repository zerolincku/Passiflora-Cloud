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

import java.util.List;
import java.util.NoSuchElementException;

import com.mybatisflex.core.keygen.impl.FlexIDKeyGenerator;
import com.zerolinck.passiflora.common.api.Result;
import com.zerolinck.passiflora.common.api.ResultCode;
import com.zerolinck.passiflora.common.util.Asserts;
import com.zerolinck.passiflora.common.util.Condition;
import com.zerolinck.passiflora.feign.iam.IamPermissionApi;
import com.zerolinck.passiflora.iam.service.IamPermissionService;
import com.zerolinck.passiflora.model.iam.entity.IamPermission;
import com.zerolinck.passiflora.model.iam.resp.IamPermissionResp;
import com.zerolinck.passiflora.model.iam.resp.IamPermissionTableResp;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** @author linck on 2024-05-06 */
@Slf4j
@RestController
@RequestMapping("iam-permission")
@RequiredArgsConstructor
public class IamPermissionController implements IamPermissionApi {

    private final IamPermissionService iamPermissionService;
    private static final FlexIDKeyGenerator flexIDKeyGenerator = new FlexIDKeyGenerator();

    @Override
    public Result<List<IamPermission>> page(@NotNull Condition<IamPermission> condition) {
        return Result.ok(iamPermissionService.page(condition));
    }

    @Override
    public Result<String> add(IamPermission iamPermission) {
        iamPermission.setPermissionId(String.valueOf(flexIDKeyGenerator.generate(IamPermission.class, "permissionId")));
        if (StringUtils.isBlank(iamPermission.getPermissionParentId())) {
            iamPermission.setPermissionParentId("0");
        }
        iamPermissionService.add(iamPermission);
        return Result.ok(iamPermission.getPermissionId());
    }

    @Override
    public Result<String> update(IamPermission iamPermission) {
        boolean success = iamPermissionService.update(iamPermission);
        if (StringUtils.isBlank(iamPermission.getPermissionParentId())) {
            iamPermission.setPermissionParentId("0");
        }
        if (success) {
            return Result.ok(iamPermission.getPermissionId());
        } else {
            return Result.failed(ResultCode.COMPETE_FAILED);
        }
    }

    @Override
    public Result<IamPermission> detail(@Nullable String permissionId) {
        Asserts.notBlank(permissionId, "权限 ID 不能为空");
        return Result.ok(
                iamPermissionService.detail(permissionId).orElseThrow(() -> new NoSuchElementException("权限不存在")));
    }

    @Override
    public Result<Void> delete(List<String> permissionIds) {
        Asserts.notEmpty(permissionIds, "权限 ID 不能为空");
        iamPermissionService.deleteByIds(permissionIds);
        return Result.ok();
    }

    @Override
    public Result<List<IamPermissionResp>> menuTree() {
        return Result.ok(iamPermissionService.menuTree());
    }

    @Override
    public Result<List<IamPermissionTableResp>> permissionTableTree() {
        return Result.ok(iamPermissionService.permissionTableTree());
    }

    @Override
    public Result<Void> updateOrder(List<IamPermissionTableResp> iamPermissionTableResps) {
        iamPermissionService.updateOrder(iamPermissionTableResps);
        return Result.ok();
    }

    @Override
    public Result<Void> disable(List<String> permissionIds) {
        Asserts.notEmpty(permissionIds, "权限 ID 不能为空");
        iamPermissionService.disable(permissionIds);
        return Result.ok();
    }

    @Override
    public Result<Void> enable(List<String> permissionIds) {
        Asserts.notEmpty(permissionIds, "权限 ID 不能为空");
        iamPermissionService.enable(permissionIds);
        return Result.ok();
    }
}
