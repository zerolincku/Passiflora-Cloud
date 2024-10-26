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
import com.zerolinck.passiflora.common.util.RandomUtil;
import com.zerolinck.passiflora.feign.iam.IamAppApi;
import com.zerolinck.passiflora.iam.service.IamAppService;
import com.zerolinck.passiflora.model.common.enums.StatusEnum;
import com.zerolinck.passiflora.model.iam.entity.IamApp;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 应用 Controller
 *
 * @author linck on 2024-09-30
 */
@Slf4j
@RestController
@RequestMapping("iamApp")
@RequiredArgsConstructor
public class IamAppController implements IamAppApi {

    private final IamAppService iamAppService;

    @Nonnull
    @Override
    public Result<List<IamApp>> page(@Nullable QueryCondition<IamApp> condition) {
        return Result.ok(iamAppService.page(condition));
    }

    @Nonnull
    @Override
    public Result<String> add(@Nonnull IamApp iamApp) {
        iamApp.setAppId(null);
        iamApp.setAppKey(RandomUtil.lowerCharAndNum(32));
        iamApp.setAppSecret(RandomUtil.lowerCharAndNum(32));
        iamApp.setAppStatus(StatusEnum.ENABLE);
        iamAppService.add(iamApp);
        return Result.ok(iamApp.getAppId());
    }

    @Nonnull
    @Override
    public Result<String> update(@Nonnull IamApp iamApp) {
        iamApp.setAppKey(null);
        iamApp.setAppSecret(null);
        boolean success = iamAppService.update(iamApp);
        if (success) {
            return Result.ok(iamApp.getAppId());
        } else {
            return Result.failed(ResultCode.COMPETE_FAILED);
        }
    }

    @Nonnull
    @Override
    public Result<IamApp> detail(@Nonnull String appId) {
        AssertUtil.notBlank(appId, "应用 ID 不能为空");
        return Result.ok(iamAppService.detail(appId).orElseThrow(() -> new NoSuchElementException("应用不存在")));
    }

    @Nonnull
    @Override
    public Result<String> delete(@Nonnull List<String> appIds) {
        AssertUtil.notEmpty(appIds, "应用 ID 不能为空");
        iamAppService.deleteByIds(appIds);
        return Result.ok();
    }

    @Nonnull
    @Override
    public Result<String> disable(@Nonnull List<String> appIds) {
        AssertUtil.notEmpty(appIds, "应用 ID 不能为空");
        iamAppService.disable(appIds);
        return Result.ok();
    }

    @Nonnull
    @Override
    public Result<String> enable(@Nonnull List<String> appIds) {
        AssertUtil.notEmpty(appIds, "应用 ID 不能为空");
        iamAppService.enable(appIds);
        return Result.ok();
    }
}
