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
package com.zerolinck.passiflora.feign.system;

import com.zerolinck.passiflora.common.api.ListWithPage;
import com.zerolinck.passiflora.common.api.Result;
import com.zerolinck.passiflora.common.util.QueryCondition;
import com.zerolinck.passiflora.feign.config.FeignConfiguration;
import com.zerolinck.passiflora.model.system.dto.PositionPermissionSaveDto;
import com.zerolinck.passiflora.model.system.entity.SysPosition;
import com.zerolinck.passiflora.model.system.vo.SysPositionVo;
import com.zerolinck.passiflora.model.valid.Insert;
import com.zerolinck.passiflora.model.valid.Update;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author linck
 * @since 2024-05-14
 */
@Tag(name = "职位")
@FeignClient(
    value = "sysPosition",
    contextId = "sysPosition",
    path = "/passiflora/system-api/sysPosition",
    configuration = FeignConfiguration.class
)
public interface SysPositionApi {
    @Operation(summary = "分页查询")
    @GetMapping("page")
    Result<ListWithPage<SysPosition>> page(
        QueryCondition<SysPosition> condition
    );

    @Operation(summary = "新增")
    @PostMapping("add")
    Result<String> add(
        @RequestBody @Validated(Insert.class) SysPosition sysPosition
    );

    @Operation(summary = "更新")
    @PostMapping("update")
    Result<String> update(
        @RequestBody @Validated(Update.class) SysPosition sysPosition
    );

    @Operation(summary = "详情")
    @GetMapping("detail")
    Result<SysPosition> detail(
        @RequestParam(value = "positionId") String positionId
    );

    @Operation(summary = "删除")
    @PostMapping("delete")
    Result<String> delete(@RequestBody List<String> positionIds);

    @Operation(summary = "职位树")
    @GetMapping("positionTree")
    Result<List<SysPositionVo>> positionTree();

    @Operation(summary = "禁用")
    @PostMapping("disable")
    Result<String> disable(@RequestBody List<String> positionIds);

    @Operation(summary = "启用")
    @PostMapping("enable")
    Result<String> enable(@RequestBody List<String> positionIds);

    @Operation(summary = "更新排序")
    @PostMapping("updateOrder")
    Result<String> updateOrder(
        @RequestBody @Validated(Update.class) List<SysPositionVo> sysPositionVos
    );

    @Operation(summary = "根据职位ids获取权限ids")
    @PostMapping("permissionIdsByPositionIds")
    Result<List<String>> permissionIdsByPositionIds(
        @RequestBody List<String> positionIds
    );

    @Operation(summary = "保存职位权限")
    @PostMapping("savePositionPermission")
    Result<String> savePositionPermission(
        @RequestBody @Validated PositionPermissionSaveDto positionPermissionSaveDto
    );
}
