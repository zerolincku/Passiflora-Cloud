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

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.zerolinck.passiflora.common.api.ListWithPage;
import com.zerolinck.passiflora.common.api.Result;
import com.zerolinck.passiflora.common.api.ResultCodeEnum;
import com.zerolinck.passiflora.common.exception.BizException;
import com.zerolinck.passiflora.common.util.AssertUtil;
import com.zerolinck.passiflora.common.util.QueryCondition;
import com.zerolinck.passiflora.feign.system.SysPositionApi;
import com.zerolinck.passiflora.model.system.args.PositionPermissionSaveArgs;
import com.zerolinck.passiflora.model.system.entity.SysPosition;
import com.zerolinck.passiflora.model.system.vo.SysPositionVo;
import com.zerolinck.passiflora.system.service.SysPositionPermissionService;
import com.zerolinck.passiflora.system.service.SysPositionService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author linck
 * @since 2024-05-14
 */
@Slf4j
@RestController
@RequestMapping("sysPosition")
@RequiredArgsConstructor
public class SysPositionController implements SysPositionApi {

    private final SysPositionService sysPositionService;
    private final SysPositionPermissionService sysPositionPermissionService;

    @Override
    public Result<ListWithPage<SysPosition>> page(QueryCondition<SysPosition> condition) {
        return Result.page(sysPositionService.page(condition));
    }

    @Override
    public Result<String> add(SysPosition sysPosition) {
        sysPosition.setPositionId(IdWorker.getIdStr());
        if (StringUtils.isBlank(sysPosition.getParentPositionId())) {
            sysPosition.setParentPositionId("0");
        }
        sysPositionService.add(sysPosition);
        return Result.ok(sysPosition.getPositionId());
    }

    @Override
    public Result<String> update(SysPosition sysPosition) {
        if (StringUtils.isBlank(sysPosition.getParentPositionId())) {
            sysPosition.setParentPositionId("0");
        }
        boolean success = sysPositionService.update(sysPosition);
        if (success) {
            return Result.ok(sysPosition.getPositionId());
        } else {
            return Result.failed(ResultCodeEnum.COMPETE_FAILED);
        }
    }

    @Override
    public Result<SysPosition> detail(String positionId) {
        AssertUtil.notBlank(positionId, "职位 ID 不能为空");
        return Result.ok(sysPositionService
                .detail(positionId)
                .orElseThrow(() -> new BizException(ResultCodeEnum.NO_MATCH_DATA)));
    }

    @Override
    public Result<String> delete(List<String> positionIds) {
        AssertUtil.notEmpty(positionIds, "职位 ID 不能为空");
        sysPositionService.deleteByIds(positionIds);
        return Result.ok();
    }

    @Override
    public Result<List<SysPositionVo>> positionTree() {
        return Result.ok(sysPositionService.positionTree());
    }

    @Override
    public Result<String> disable(List<String> positionIds) {
        sysPositionService.disable(positionIds);
        return Result.ok();
    }

    @Override
    public Result<String> enable(List<String> positionIds) {
        sysPositionService.enable(positionIds);
        return Result.ok();
    }

    @Override
    public Result<String> updateOrder(List<SysPositionVo> sysPositionVos) {
        sysPositionService.updateOrder(sysPositionVos);
        return Result.ok();
    }

    @Override
    public Result<List<String>> permissionIdsByPositionIds(List<String> positionIds) {
        AssertUtil.notEmpty(positionIds, "职位 ID 不能为空");
        return Result.ok(sysPositionPermissionService.permissionIdsByPositionIds(positionIds));
    }

    @Override
    public Result<String> savePositionPermission(PositionPermissionSaveArgs args) {
        sysPositionPermissionService.savePositionPermission(args);
        return Result.ok();
    }
}
