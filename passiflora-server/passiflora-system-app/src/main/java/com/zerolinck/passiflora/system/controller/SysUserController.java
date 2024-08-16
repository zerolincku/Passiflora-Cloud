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
import com.zerolinck.passiflora.feign.system.SysUserApi;
import com.zerolinck.passiflora.model.system.args.SysUserSaveArgs;
import com.zerolinck.passiflora.model.system.entity.SysUser;
import com.zerolinck.passiflora.model.system.valid.Login;
import com.zerolinck.passiflora.model.system.vo.SysUserInfo;
import com.zerolinck.passiflora.model.system.vo.SysUserVo;
import com.zerolinck.passiflora.system.service.SysUserService;
import jakarta.annotation.Nonnull;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author linck
 * @since 2024-02-05
 */
@RestController
@RequestMapping("sysUser")
@RequiredArgsConstructor
public class SysUserController implements SysUserApi {

    private final SysUserService sysUserService;

    @Override
    public Result<ListWithPage<SysUserVo>> page(String orgId, QueryCondition<SysUser> condition) {
        return Result.page(sysUserService.page(orgId, condition));
    }

    @Nonnull
    @Override
    public Result<String> add(@Nonnull SysUserSaveArgs args) {
        args.setUserId(null);
        sysUserService.add(args);
        return Result.ok(args.getUserId());
    }

    @Nonnull
    @Override
    public Result<String> update(@Nonnull SysUserSaveArgs args) {
        args.setUserPassword(null);
        args.setUserName(null);
        args.setSalt(null);
        boolean success = sysUserService.update(args);
        if (success) {
            return Result.ok(args.getUserId());
        } else {
            return Result.failed(ResultCodeEnum.COMPETE_FAILED);
        }
    }

    @Override
    public Result<SysUser> detail(@RequestParam(value = "userId") String userId) {
        AssertUtil.notBlank(userId, "用户 ID 不能为空");
        return Result.ok(
                sysUserService.detail(userId).orElseThrow(() -> new BizException(ResultCodeEnum.NO_MATCH_DATA)));
    }

    @Override
    public Result<String> delete(@RequestBody List<String> userIds) {
        AssertUtil.notEmpty(userIds, "用户 ID 不能为空");
        sysUserService.deleteByIds(userIds);
        return Result.ok();
    }

    @Override
    public Result<String> login(@RequestBody @Validated(Login.class) SysUser sysUser) {
        return Result.ok(sysUserService.login(sysUser));
    }

    @Override
    public Result<String> logout() {
        sysUserService.logout();
        return Result.ok();
    }

    @Override
    public Result<SysUserInfo> currentUserInfo() {
        return Result.ok(sysUserService.currentUserInfo());
    }

    @Override
    public Result<Boolean> checkToken() {
        return Result.ok(sysUserService.checkToken());
    }
}
