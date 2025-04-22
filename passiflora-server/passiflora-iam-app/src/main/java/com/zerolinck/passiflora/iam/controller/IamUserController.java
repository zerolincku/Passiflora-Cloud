/* 
 * Copyright (C) 2025 Linck. <zerolinck@foxmail.com>
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

import com.zerolinck.passiflora.common.api.Result;
import com.zerolinck.passiflora.common.api.ResultCode;
import com.zerolinck.passiflora.common.util.Asserts;
import com.zerolinck.passiflora.common.util.Condition;
import com.zerolinck.passiflora.feign.iam.IamUserApi;
import com.zerolinck.passiflora.iam.service.IamUserService;
import com.zerolinck.passiflora.model.iam.args.IamUserArgs;
import com.zerolinck.passiflora.model.iam.entity.IamUser;
import com.zerolinck.passiflora.model.iam.resp.IamUserInfo;
import com.zerolinck.passiflora.model.iam.resp.IamUserResp;
import com.zerolinck.passiflora.model.iam.valid.Login;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

/** @author linck on 2024-02-05 */
@RestController
@RequestMapping("iam-user")
@RequiredArgsConstructor
public class IamUserController implements IamUserApi {

    private final IamUserService iamUserService;

    @Override
    public Result<List<IamUserResp>> page(String orgId, @NotNull Condition<IamUser> condition) {
        return Result.ok(iamUserService.page(orgId, condition));
    }

    @NotNull @Override
    public Result<String> add(@NotNull IamUserArgs args) {
        args.setUserId(null);
        iamUserService.add(args);
        return Result.ok(args.getUserId());
    }

    @NotNull @Override
    public Result<String> update(@NotNull IamUserArgs args) {
        args.setUserPassword(null);
        args.setUserName(null);
        boolean success = iamUserService.update(args);
        if (success) {
            return Result.ok(args.getUserId());
        } else {
            return Result.failed(ResultCode.COMPETE_FAILED);
        }
    }

    @Override
    public Result<IamUserResp> detail(@Nullable @RequestParam(value = "userId", required = false) String userId) {
        Asserts.notBlank(userId, "用户 ID 不能为空");
        return Result.ok(iamUserService.detail(userId).orElseThrow(() -> new NoSuchElementException("用户不存在")));
    }

    @Override
    public Result<Void> delete(@RequestBody List<String> userIds) {
        Asserts.notEmpty(userIds, "用户 ID 不能为空");
        iamUserService.deleteByIds(userIds);
        return Result.ok();
    }

    @Override
    public Result<String> login(@RequestBody @Validated(Login.class) IamUser iamUser) {
        return Result.ok(iamUserService.login(iamUser));
    }

    @Override
    public Result<Void> logout() {
        iamUserService.logout();
        return Result.ok();
    }

    @Override
    public Result<IamUserInfo> currentUserInfo() {
        return Result.ok(iamUserService.currentUserInfo());
    }

    @Override
    public Result<Boolean> checkToken() {
        return Result.ok(iamUserService.checkToken());
    }
}
