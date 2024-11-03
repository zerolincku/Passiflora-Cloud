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
import com.zerolinck.passiflora.model.iam.args.IamUserSaveArgs;
import com.zerolinck.passiflora.model.iam.entity.IamUser;
import com.zerolinck.passiflora.model.iam.vo.IamUserInfo;
import com.zerolinck.passiflora.model.iam.vo.IamUserVo;
import com.zerolinck.passiflora.model.valid.Insert;
import com.zerolinck.passiflora.model.valid.Update;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/** @author linck on 2024-03-19 */
@Tag(name = "用户")
@FeignClient(
        value = "passiflora-iam-app",
        contextId = "iamUser",
        path = "/passiflora/iam-api/iam-user",
        configuration = FeignConfiguration.class)
public interface IamUserApi {
    @Operation(summary = "分页查询")
    @GetMapping("page")
    Result<List<IamUserVo>> page(
            @RequestParam(value = "orgId", required = false) String orgId,
            @NotNull @SpringQueryMap QueryCondition<IamUser> condition);

    @NotNull @Operation(summary = "新增")
    @PostMapping("add")
    Result<String> add(@NotNull @RequestBody @Validated(Insert.class) IamUserSaveArgs iamUser);

    @NotNull @Operation(summary = "更新")
    @PostMapping("update")
    Result<String> update(@NotNull @RequestBody @Validated(Update.class) IamUserSaveArgs iamUser);

    @Operation(summary = "详情")
    @GetMapping("detail")
    Result<IamUser> detail(@RequestParam(value = "userId") String userId);

    @Operation(summary = "删除")
    @PostMapping("delete")
    Result<Void> delete(List<String> userIds);

    @Operation(summary = "登录", description = "需要参数：userName，userPassword")
    @PostMapping("login")
    Result<String> login(IamUser iamUser);

    @Operation(summary = "退出登录")
    @GetMapping("logout")
    Result<Void> logout();

    @Operation(summary = "获取当前登录用户信息")
    @GetMapping("current-user-info")
    Result<IamUserInfo> currentUserInfo();

    /**
     * 网关校验 Token 有效性调用
     *
     * @return true-有效，false-无效
     */
    @Operation(summary = "验证token")
    @GetMapping("check-token")
    Result<Boolean> checkToken();
}
