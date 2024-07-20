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

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.zerolinck.passiflora.common.api.ListWithPage;
import com.zerolinck.passiflora.common.api.Result;
import com.zerolinck.passiflora.common.api.ResultCodeEnum;
import com.zerolinck.passiflora.common.util.AssertUtil;
import com.zerolinck.passiflora.common.util.QueryCondition;
import com.zerolinck.passiflora.feign.system.SysOrgApi;
import com.zerolinck.passiflora.model.system.entity.SysOrg;
import com.zerolinck.passiflora.model.system.vo.SysOrgVo;
import com.zerolinck.passiflora.system.service.SysOrgService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author linck
 * @since 2024-04-09
 */
@Slf4j
@RestController
@RequestMapping("sysOrg")
@RequiredArgsConstructor
public class SysOrgController implements SysOrgApi {
    private final SysOrgService sysOrgService;

    @Override
    public Result<ListWithPage<SysOrg>> page(QueryCondition<SysOrg> condition) {
        return Result.page(sysOrgService.page(condition));
    }

    @Override
    public Result<String> add(SysOrg sysOrg) {
        sysOrg.setOrgId(IdWorker.getIdStr());
        if (StrUtil.isBlank(sysOrg.getParentOrgId())) {
            sysOrg.setParentOrgId("0");
        }
        sysOrgService.add(sysOrg);
        return Result.ok(sysOrg.getOrgId());
    }

    @Override
    public Result<String> update(SysOrg sysOrg) {
        if (StrUtil.isBlank(sysOrg.getParentOrgId())) {
            sysOrg.setParentOrgId("0");
        }
        boolean success = sysOrgService.update(sysOrg);
        if (success) {
            return Result.ok(sysOrg.getOrgId());
        } else {
            return Result.failed(ResultCodeEnum.COMPETE_FAILED);
        }
    }

    @Override
    public Result<SysOrg> detail(String orgId) {
        AssertUtil.notBlank(orgId);
        return Result.ok(sysOrgService.detail(orgId));
    }

    /**
     * 此方法会级联删除下级机构
     */
    @Override
    public Result<String> delete(List<String> orgIds) {
        AssertUtil.notEmpty(orgIds);
        sysOrgService.deleteByIds(orgIds);
        return Result.ok();
    }

    @Override
    public Result<List<SysOrgVo>> listByParentId(String orgParentId) {
        if (StrUtil.isBlank(orgParentId)) {
            orgParentId = "0";
        }
        return Result.ok(sysOrgService.listByParentId(orgParentId));
    }

    @Override
    public Result<List<SysOrgVo>> orgTree() {
        return Result.ok(sysOrgService.orgTree());
    }
}
