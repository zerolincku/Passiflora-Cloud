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

import com.zerolinck.passiflora.common.api.ListWithPage;
import com.zerolinck.passiflora.common.api.Result;
import com.zerolinck.passiflora.common.util.QueryCondition;
import com.zerolinck.passiflora.feign.config.FeignConfiguration;
import com.zerolinck.passiflora.model.iam.entity.SysConfig;
import com.zerolinck.passiflora.model.valid.Insert;
import com.zerolinck.passiflora.model.valid.Update;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author 林常坤
 * @since 2024-08-24
 */
@Tag(name = "系统配置")
@FeignClient(
        value = "sysConfig",
        contextId = "sysConfig",
        path = "/passiflora/iam-api/sysConfig",
        configuration = FeignConfiguration.class)
public interface SysConfigApi {

    @Nonnull
    @Operation(summary = "分页查询")
    @GetMapping("page")
    Result<ListWithPage<SysConfig>> page(@Nullable QueryCondition<SysConfig> condition);

    @Nonnull
    @Operation(summary = "列表查询")
    @GetMapping("list")
    Result<List<SysConfig>> list(@Nullable QueryCondition<SysConfig> condition);

    @Nonnull
    @Operation(summary = "新增")
    @PostMapping("add")
    Result<String> add(@Nonnull @RequestBody @Validated(Insert.class) SysConfig sysConfig);

    @Nonnull
    @Operation(summary = "更新")
    @PostMapping("update")
    Result<String> update(@Nonnull @RequestBody @Validated(Update.class) SysConfig sysConfig);

    @Nonnull
    @Operation(summary = "详情")
    @GetMapping("detail")
    Result<SysConfig> detail(@Nonnull @RequestParam(value = "configId") String configId);

    @Nonnull
    @Operation(summary = "删除")
    @PostMapping("delete")
    Result<String> delete(@Nonnull @RequestBody List<String> configIds);
}
