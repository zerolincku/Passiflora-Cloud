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
import com.zerolinck.passiflora.model.system.entity.SysPermission;
import com.zerolinck.passiflora.model.system.vo.SysPermissionTableVo;
import com.zerolinck.passiflora.model.system.vo.SysPermissionVo;
import com.zerolinck.passiflora.model.valid.Insert;
import com.zerolinck.passiflora.model.valid.Update;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author linck
 * @since 2024-05-06
 */
@Tag(name = "菜单")
@FeignClient(
    value = "sysPermission",
    contextId = "sysPermission",
    path = "/passiflora/system-api/sysPermission",
    configuration = FeignConfiguration.class
)
public interface SysPermissionApi {
    @Operation(summary = "分页查询")
    @GetMapping("page")
    Result<ListWithPage<SysPermission>> page(
        QueryCondition<SysPermission> condition
    );

    @Operation(summary = "新增")
    @PostMapping("add")
    Result<String> add(
        @RequestBody @Validated(Insert.class) SysPermission sysPermission
    );

    @Operation(summary = "更新")
    @PostMapping("update")
    Result<String> update(
        @RequestBody @Validated(Update.class) SysPermission sysPermission
    );

    @Operation(summary = "详情")
    @GetMapping("detail")
    Result<SysPermission> detail(
        @RequestParam(value = "permissionId") String permissionId
    );

    @Operation(summary = "删除")
    @PostMapping("delete")
    Result<String> delete(@RequestBody List<String> permissionIds);

    @Operation(summary = "菜单树")
    @GetMapping("menuTree")
    Result<List<SysPermissionVo>> menuTree();

    @Operation(summary = "权限树-列表使用")
    @GetMapping("permissionTableTree")
    Result<List<SysPermissionTableVo>> permissionTableTree();

    @Operation(summary = "更新排序")
    @PostMapping("updateOrder")
    Result<String> updateOrder(
        @RequestBody @Validated(Update.class) List<
            SysPermissionTableVo
        > sysPermissionTableVos
    );

    @Operation(summary = "禁用")
    @PostMapping("disable")
    Result<String> disable(@RequestBody List<String> permissionIds);

    @Operation(summary = "启用")
    @PostMapping("enable")
    Result<String> enable(@RequestBody List<String> permissionIds);
}
