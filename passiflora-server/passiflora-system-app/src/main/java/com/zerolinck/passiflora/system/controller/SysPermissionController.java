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
package com.zerolinck.passiflora.system.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.zerolinck.passiflora.common.api.ListWithPage;
import com.zerolinck.passiflora.common.api.Result;
import com.zerolinck.passiflora.common.api.ResultCodeEnum;
import com.zerolinck.passiflora.common.util.AssertUtil;
import com.zerolinck.passiflora.common.util.QueryCondition;
import com.zerolinck.passiflora.feign.system.SysPermissionApi;
import com.zerolinck.passiflora.model.system.entity.SysPermission;
import com.zerolinck.passiflora.model.system.vo.SysPermissionTableVo;
import com.zerolinck.passiflora.model.system.vo.SysPermissionVo;
import com.zerolinck.passiflora.system.service.SysPermissionService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author linck
 * @since 2024-05-06
 */
@Slf4j
@RestController
@RequestMapping("sysPermission")
@RequiredArgsConstructor
public class SysPermissionController implements SysPermissionApi {

    private final SysPermissionService sysPermissionService;

    @Override
    public Result<ListWithPage<SysPermission>> page(
        QueryCondition<SysPermission> condition
    ) {
        return Result.page(sysPermissionService.page(condition));
    }

    @Override
    public Result<String> add(SysPermission sysPermission) {
        sysPermission.setPermissionId(IdWorker.getIdStr());
        if (StrUtil.isBlank(sysPermission.getPermissionParentId())) {
            sysPermission.setPermissionParentId("0");
        }
        sysPermissionService.add(sysPermission);
        return Result.ok(sysPermission.getPermissionId());
    }

    @Override
    public Result<String> update(SysPermission sysPermission) {
        boolean success = sysPermissionService.update(sysPermission);
        if (StrUtil.isBlank(sysPermission.getPermissionParentId())) {
            sysPermission.setPermissionParentId("0");
        }
        if (success) {
            return Result.ok(sysPermission.getPermissionId());
        } else {
            return Result.failed(ResultCodeEnum.COMPETE_FAILED);
        }
    }

    @Override
    public Result<SysPermission> detail(String permissionId) {
        AssertUtil.notBlank(permissionId, "权限 ID 不能为空");
        return Result.ok(sysPermissionService.detail(permissionId));
    }

    @Override
    public Result<String> delete(List<String> permissionIds) {
        AssertUtil.notEmpty(permissionIds, "权限 ID 不能为空");
        sysPermissionService.deleteByIds(permissionIds);
        return Result.ok();
    }

    @Override
    public Result<List<SysPermissionVo>> menuTree() {
        return Result.ok(sysPermissionService.menuTree());
    }

    @Override
    public Result<List<SysPermissionTableVo>> permissionTableTree() {
        return Result.ok(sysPermissionService.permissionTableTree());
    }

    @Override
    public Result<String> updateOrder(
        List<SysPermissionTableVo> sysPermissionTableVos
    ) {
        sysPermissionService.updateOrder(sysPermissionTableVos);
        return Result.ok();
    }

    @Override
    public Result<String> disable(List<String> permissionIds) {
        AssertUtil.notEmpty(permissionIds, "权限 ID 不能为空");
        sysPermissionService.disable(permissionIds);
        return Result.ok();
    }

    @Override
    public Result<String> enable(List<String> permissionIds) {
        AssertUtil.notEmpty(permissionIds, "权限 ID 不能为空");
        sysPermissionService.enable(permissionIds);
        return Result.ok();
    }
}
