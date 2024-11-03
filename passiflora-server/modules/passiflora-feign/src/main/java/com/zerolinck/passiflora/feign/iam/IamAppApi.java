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
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/** @author linck on 2024-09-30 */
@Tag(name = "应用")
@FeignClient(
        value = "passiflora-iam-app",
        contextId = "iamApp",
        path = "/passiflora/iam-api/iam-app",
        configuration = FeignConfiguration.class)
public interface IamAppApi {

    @NotNull @Operation(summary = "分页查询")
    @GetMapping("page")
    Result<List<IamApp>> page(@NotNull @SpringQueryMap QueryCondition<IamApp> condition);

    @NotNull @Operation(summary = "新增")
    @PostMapping("add")
    Result<String> add(@NotNull @RequestBody @Validated(Insert.class) IamApp iamApp);

    @NotNull @Operation(summary = "更新")
    @PostMapping("update")
    Result<String> update(@NotNull @RequestBody @Validated(Update.class) IamApp iamApp);

    @NotNull @Operation(summary = "详情")
    @GetMapping("detail")
    Result<IamApp> detail(@NotNull @RequestParam(value = "appId") String appId);

    @NotNull @Operation(summary = "删除")
    @PostMapping("delete")
    Result<String> delete(@NotNull @RequestBody List<String> appIds);

    @NotNull @Operation(summary = "禁用")
    @PostMapping("disable")
    Result<String> disable(@NotNull @RequestBody List<String> appIds);

    @NotNull @Operation(summary = "启用")
    @PostMapping("enable")
    Result<String> enable(@NotNull @RequestBody List<String> appIds);
}
