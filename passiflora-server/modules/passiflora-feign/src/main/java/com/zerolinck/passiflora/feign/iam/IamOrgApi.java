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
import com.zerolinck.passiflora.model.iam.entity.IamOrg;
import com.zerolinck.passiflora.model.iam.vo.IamOrgVo;
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
 * @since 2024-04-09
 */
@Tag(name = "机构")
@FeignClient(
        value = "sysOrg",
        contextId = "sysOrg",
        path = "/passiflora/iam-api/sysOrg",
        configuration = FeignConfiguration.class)
public interface IamOrgApi {
    @Operation(summary = "分页查询")
    @GetMapping("page")
    Result<ListWithPage<IamOrg>> page(QueryCondition<IamOrg> condition);

    @Operation(summary = "新增")
    @PostMapping("add")
    Result<String> add(@RequestBody @Validated(Insert.class) IamOrg iamOrg);

    @Operation(summary = "更新")
    @PostMapping("update")
    Result<String> update(@RequestBody @Validated(Update.class) IamOrg iamOrg);

    @Operation(summary = "详情")
    @GetMapping("detail")
    Result<IamOrg> detail(@RequestParam(value = "orgId") String orgId);

    /** 此方法会级联删除下级机构 */
    @Operation(summary = "删除")
    @PostMapping("delete")
    Result<String> delete(@RequestBody List<String> orgIds);

    @Operation(summary = "根据父级ID查询子机构列表")
    @GetMapping("listByParentId")
    Result<List<IamOrgVo>> listByParentId(@RequestParam(required = false, value = "orgParentId") String orgParentId);

    @Operation(summary = "机构树")
    @GetMapping("orgTree")
    Result<List<IamOrgVo>> orgTree();
}
