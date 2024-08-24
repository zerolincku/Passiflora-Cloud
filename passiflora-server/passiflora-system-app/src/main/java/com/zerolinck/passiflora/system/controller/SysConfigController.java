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
import com.zerolinck.passiflora.common.util.CurrentUtil;
import com.zerolinck.passiflora.common.util.QueryCondition;
import com.zerolinck.passiflora.feign.system.SysConfigApi;
import com.zerolinck.passiflora.model.common.constant.Constants;
import com.zerolinck.passiflora.model.system.entity.SysConfig;
import com.zerolinck.passiflora.system.service.SysConfigService;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统配置 Controller
 *
 * @author 林常坤
 * @since 2024-08-24
 */
@Slf4j
@RestController
@RequestMapping("sysConfig")
@RequiredArgsConstructor
public class SysConfigController implements SysConfigApi {

    private final SysConfigService sysConfigService;

    @Nonnull
    @Override
    public Result<ListWithPage<SysConfig>> page(@Nullable QueryCondition<SysConfig> condition) {
        return Result.page(sysConfigService.page(condition));
    }

    @Nonnull
    @Override
    public Result<List<SysConfig>> list(@Nullable QueryCondition<SysConfig> condition) {
        return Result.ok(sysConfigService.list(condition));
    }

    @Nonnull
    @Override
    public Result<String> add(@Nonnull SysConfig sysConfig) {
        sysConfig.setConfigId(null);
        sysConfigService.add(sysConfig);
        return Result.ok(sysConfig.getConfigId());
    }

    @Nonnull
    @Override
    public Result<String> update(@Nonnull SysConfig sysConfig) {
        boolean success = sysConfigService.update(sysConfig);
        if (success) {
            return Result.ok(sysConfig.getConfigId());
        } else {
            return Result.failed(ResultCodeEnum.COMPETE_FAILED);
        }
    }

    @Nonnull
    @Override
    public Result<SysConfig> detail(@Nonnull String configId) {
        AssertUtil.notBlank(configId, "系统配置 ID 不能为空");
        return Result.ok(
                sysConfigService.detail(configId).orElseThrow(() -> new BizException(ResultCodeEnum.NO_MATCH_DATA)));
    }

    @Nonnull
    @Override
    public Result<String> delete(@Nonnull List<String> configIds) {
        if (!Constants.SUPER_ADMIN_ID.equals(CurrentUtil.getCurrentUserId())) {
            throw new BizException(ResultCodeEnum.FORBIDDEN, "非超级管理员无法删除配置");
        }
        AssertUtil.notEmpty(configIds, "系统配置 ID 不能为空");
        sysConfigService.deleteByIds(configIds);
        return Result.ok();
    }
}
