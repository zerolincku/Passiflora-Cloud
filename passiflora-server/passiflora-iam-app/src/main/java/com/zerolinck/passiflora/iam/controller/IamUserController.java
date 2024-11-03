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
import com.zerolinck.passiflora.common.util.AssertUtil;
import com.zerolinck.passiflora.common.util.QueryCondition;
import com.zerolinck.passiflora.feign.iam.IamUserApi;
import com.zerolinck.passiflora.iam.service.IamUserService;
import com.zerolinck.passiflora.model.iam.args.IamUserSaveArgs;
import com.zerolinck.passiflora.model.iam.entity.IamUser;
import com.zerolinck.passiflora.model.iam.valid.Login;
import com.zerolinck.passiflora.model.iam.vo.IamUserInfo;
import com.zerolinck.passiflora.model.iam.vo.IamUserVo;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** @author linck on 2024-02-05 */
@RestController
@RequestMapping("iam-user")
@RequiredArgsConstructor
public class IamUserController implements IamUserApi {

    private final IamUserService iamUserService;

    @Override
    public Result<List<IamUserVo>> page(String orgId, @NotNull QueryCondition<IamUser> condition) {
        return Result.ok(iamUserService.page(orgId, condition));
    }

    @NotNull @Override
    public Result<String> add(@NotNull IamUserSaveArgs args) {
        args.setUserId(null);
        iamUserService.add(args);
        return Result.ok(args.getUserId());
    }

    @NotNull @Override
    public Result<String> update(@NotNull IamUserSaveArgs args) {
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
    public Result<IamUser> detail(@RequestParam(value = "userId") String userId) {
        AssertUtil.notBlank(userId, "用户 ID 不能为空");
        return Result.ok(iamUserService.detail(userId).orElseThrow(() -> new NoSuchElementException("用户不存在")));
    }

    @Override
    public Result<String> delete(@RequestBody List<String> userIds) {
        AssertUtil.notEmpty(userIds, "用户 ID 不能为空");
        iamUserService.deleteByIds(userIds);
        return Result.ok();
    }

    @Override
    public Result<String> login(@RequestBody @Validated(Login.class) IamUser iamUser) {
        return Result.ok(iamUserService.login(iamUser));
    }

    @Override
    public Result<String> logout() {
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
