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

import com.zerolinck.passiflora.base.enums.StatusEnum;
import com.zerolinck.passiflora.common.api.Result;
import com.zerolinck.passiflora.common.api.ResultCode;
import com.zerolinck.passiflora.common.util.Asserts;
import com.zerolinck.passiflora.common.util.Condition;
import com.zerolinck.passiflora.common.util.RandomUtils;
import com.zerolinck.passiflora.feign.iam.IamAppApi;
import com.zerolinck.passiflora.iam.service.IamAppService;
import com.zerolinck.passiflora.model.iam.entity.IamApp;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 应用 Controller
 *
 * @author linck on 2024-09-30
 */
@Slf4j
@RestController
@RequestMapping("iam-app")
@RequiredArgsConstructor
public class IamAppController implements IamAppApi {

    private final IamAppService iamAppService;

    @NotNull @Override
    public Result<List<IamApp>> page(@NotNull Condition<IamApp> condition) {
        return Result.ok(iamAppService.page(condition));
    }

    @NotNull @Override
    public Result<String> add(@NotNull IamApp iamApp) {
        iamApp.setAppId(null);
        iamApp.setAppKey(RandomUtils.lowerCharAndNum(32));
        iamApp.setAppSecret(RandomUtils.lowerCharAndNum(32));
        iamApp.setAppStatus(StatusEnum.ENABLE);
        iamAppService.add(iamApp);
        return Result.ok(iamApp.getAppId());
    }

    @NotNull @Override
    public Result<String> update(@NotNull IamApp iamApp) {
        iamApp.setAppKey(null);
        iamApp.setAppSecret(null);
        boolean success = iamAppService.update(iamApp);
        if (success) {
            return Result.ok(iamApp.getAppId());
        } else {
            return Result.failed(ResultCode.COMPETE_FAILED);
        }
    }

    @NotNull @Override
    public Result<IamApp> detail(@Nullable String appId) {
        Asserts.notBlank(appId, "应用 ID 不能为空");
        return Result.ok(iamAppService.detail(appId).orElseThrow(() -> new NoSuchElementException("应用不存在")));
    }

    @NotNull @Override
    public Result<Void> delete(@NotNull List<String> appIds) {
        Asserts.notEmpty(appIds, "应用 ID 不能为空");
        iamAppService.deleteByIds(appIds);
        return Result.ok();
    }

    @NotNull @Override
    public Result<Void> disable(@NotNull List<String> appIds) {
        Asserts.notEmpty(appIds, "应用 ID 不能为空");
        iamAppService.disable(appIds);
        return Result.ok();
    }

    @NotNull @Override
    public Result<Void> enable(@NotNull List<String> appIds) {
        Asserts.notEmpty(appIds, "应用 ID 不能为空");
        iamAppService.enable(appIds);
        return Result.ok();
    }
}
