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

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.zerolinck.passiflora.common.api.Result;
import com.zerolinck.passiflora.common.api.ResultCode;
import com.zerolinck.passiflora.common.util.Asserts;
import com.zerolinck.passiflora.common.util.QueryCondition;
import com.zerolinck.passiflora.feign.iam.IamOrgApi;
import com.zerolinck.passiflora.iam.service.IamOrgService;
import com.zerolinck.passiflora.model.iam.entity.IamOrg;
import com.zerolinck.passiflora.model.iam.vo.IamOrgVo;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** @author linck on 2024-04-09 */
@Slf4j
@RestController
@RequestMapping("iam-org")
@RequiredArgsConstructor
public class IamOrgController implements IamOrgApi {

    private final IamOrgService iamOrgService;

    @Override
    public Result<List<IamOrg>> page(@NotNull QueryCondition<IamOrg> condition) {
        return Result.ok(iamOrgService.page(condition));
    }

    @Override
    public Result<String> add(IamOrg iamOrg) {
        iamOrg.setOrgId(IdWorker.getIdStr());
        if (StringUtils.isBlank(iamOrg.getParentOrgId())) {
            iamOrg.setParentOrgId("0");
        }
        iamOrgService.add(iamOrg);
        return Result.ok(iamOrg.getOrgId());
    }

    @Override
    public Result<String> update(IamOrg iamOrg) {
        if (StringUtils.isBlank(iamOrg.getParentOrgId())) {
            iamOrg.setParentOrgId("0");
        }
        boolean success = iamOrgService.update(iamOrg);
        if (success) {
            return Result.ok(iamOrg.getOrgId());
        } else {
            return Result.failed(ResultCode.COMPETE_FAILED);
        }
    }

    @Override
    public Result<IamOrg> detail(@Nullable String orgId) {
        Asserts.notBlank(orgId, "机构 ID 不能为空");
        return Result.ok(iamOrgService.detail(orgId).orElseThrow(() -> new NoSuchElementException("机构不存在")));
    }

    /** 此方法会级联删除下级机构 */
    @Override
    public Result<Void> delete(List<String> orgIds) {
        Asserts.notEmpty(orgIds, "机构 ID 不能为空");
        iamOrgService.deleteByIds(orgIds);
        return Result.ok();
    }

    @Override
    public Result<List<IamOrgVo>> listByParentId(String orgParentId) {
        if (StringUtils.isBlank(orgParentId)) {
            orgParentId = "0";
        }
        return Result.ok(iamOrgService.listByParentId(orgParentId));
    }

    @Override
    public Result<List<IamOrgVo>> orgTree() {
        return Result.ok(iamOrgService.orgTree());
    }
}
