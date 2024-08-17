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

import com.zerolinck.passiflora.common.api.ListWithPage;
import com.zerolinck.passiflora.common.api.Result;
import com.zerolinck.passiflora.common.api.ResultCodeEnum;
import com.zerolinck.passiflora.common.exception.BizException;
import com.zerolinck.passiflora.common.util.AssertUtil;
import com.zerolinck.passiflora.common.util.QueryCondition;
import com.zerolinck.passiflora.feign.system.SysRoleApi;
import com.zerolinck.passiflora.model.system.entity.SysRole;
import com.zerolinck.passiflora.system.service.SysRoleService;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.annotation.Resource;
import java.util.List;
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
public class SysRoleController implements SysRoleApi {

    @Resource
    private SysRoleService sysRoleService;

    @Nonnull
    @Override
    public Result<ListWithPage<SysRole>> page(@Nullable QueryCondition<SysRole> condition) {
        return Result.page(sysRoleService.page(condition));
    }

    @Nonnull
    @Override
    public Result<String> add(@Nonnull SysRole sysRole) {
        sysRole.setRoleId(null);
        sysRoleService.add(sysRole);
        return Result.ok(sysRole.getRoleId());
    }

    @Nonnull
    @Override
    public Result<String> update(@Nonnull SysRole sysRole) {
        boolean success = sysRoleService.update(sysRole);
        if (success) {
            return Result.ok(sysRole.getRoleId());
        } else {
            return Result.failed(ResultCodeEnum.COMPETE_FAILED);
        }
    }

    @Nonnull
    @Override
    public Result<SysRole> detail(@Nonnull String roleId) {
        AssertUtil.notBlank(roleId, "角色 ID 不能为空");
        return Result.ok(
                sysRoleService.detail(roleId).orElseThrow(() -> new BizException(ResultCodeEnum.NO_MATCH_DATA)));
    }

    @Nonnull
    @Override
    public Result<String> delete(@Nonnull List<String> roleIds) {
        AssertUtil.notEmpty(roleIds, "角色 ID 不能为空");
        sysRoleService.deleteByIds(roleIds);
        return Result.ok();
    }
}
