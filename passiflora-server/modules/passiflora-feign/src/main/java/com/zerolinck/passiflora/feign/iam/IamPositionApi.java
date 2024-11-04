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
import com.zerolinck.passiflora.model.iam.args.PositionPermissionArgs;
import com.zerolinck.passiflora.model.iam.entity.IamPosition;
import com.zerolinck.passiflora.model.iam.resp.IamPositionResp;
import com.zerolinck.passiflora.model.valid.Insert;
import com.zerolinck.passiflora.model.valid.Update;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/** @author linck on 2024-05-14 */
@Tag(name = "职位")
@FeignClient(
        value = "passiflora-iam-app",
        contextId = "iamPosition",
        path = "/passiflora/iam-api/iam-position",
        configuration = FeignConfiguration.class)
public interface IamPositionApi {
    @Operation(summary = "分页查询")
    @GetMapping("page")
    Result<List<IamPosition>> page(@NotNull @SpringQueryMap QueryCondition<IamPosition> condition);

    @Operation(summary = "新增")
    @PostMapping("add")
    Result<String> add(@RequestBody @Validated(Insert.class) IamPosition iamPosition);

    @Operation(summary = "更新")
    @PostMapping("update")
    Result<String> update(@RequestBody @Validated(Update.class) IamPosition iamPosition);

    @Operation(summary = "详情")
    @GetMapping("detail")
    Result<IamPosition> detail(@Nullable @RequestParam(value = "positionId", required = false) String positionId);

    @Operation(summary = "删除")
    @PostMapping("delete")
    Result<Void> delete(@RequestBody List<String> positionIds);

    @Operation(summary = "职位树")
    @GetMapping("position-tree")
    Result<List<IamPositionResp>> positionTree();

    @Operation(summary = "禁用")
    @PostMapping("disable")
    Result<Void> disable(@RequestBody List<String> positionIds);

    @Operation(summary = "启用")
    @PostMapping("enable")
    Result<Void> enable(@RequestBody List<String> positionIds);

    @Operation(summary = "更新排序")
    @PostMapping("update-order")
    Result<Void> updateOrder(@RequestBody @Validated(Update.class) List<IamPositionResp> iamPositionResps);

    @Operation(summary = "根据职位ids获取权限ids")
    @PostMapping("permission-ids-by-position-ids")
    Result<List<String>> permissionIdsByPositionIds(@RequestBody List<String> positionIds);

    @Operation(summary = "保存职位权限")
    @PostMapping("save-position-permission")
    Result<Void> savePositionPermission(@RequestBody @Validated PositionPermissionArgs positionPermissionArgs);
}
