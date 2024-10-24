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
import com.zerolinck.passiflora.model.iam.entity.IamApp;
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

/** @author linck on 2024-09-30 */
@Tag(name = "应用")
@FeignClient(
        value = "iamApp",
        contextId = "iamApp",
        path = "/passiflora/iam-api/iamApp",
        configuration = FeignConfiguration.class)
public interface IamAppApi {

    @Nonnull
    @Operation(summary = "分页查询")
    @GetMapping("page")
    Result<List<IamApp>> page(@Nullable QueryCondition<IamApp> condition);

    @Nonnull
    @Operation(summary = "新增")
    @PostMapping("add")
    Result<String> add(@Nonnull @RequestBody @Validated(Insert.class) IamApp iamApp);

    @Nonnull
    @Operation(summary = "更新")
    @PostMapping("update")
    Result<String> update(@Nonnull @RequestBody @Validated(Update.class) IamApp iamApp);

    @Nonnull
    @Operation(summary = "详情")
    @GetMapping("detail")
    Result<IamApp> detail(@Nonnull @RequestParam(value = "appId") String appId);

    @Nonnull
    @Operation(summary = "删除")
    @PostMapping("delete")
    Result<String> delete(@Nonnull @RequestBody List<String> appIds);

    @Nonnull
    @Operation(summary = "禁用")
    @PostMapping("disable")
    Result<String> disable(@Nonnull @RequestBody List<String> appIds);

    @Nonnull
    @Operation(summary = "启用")
    @PostMapping("enable")
    Result<String> enable(@Nonnull @RequestBody List<String> appIds);
}
