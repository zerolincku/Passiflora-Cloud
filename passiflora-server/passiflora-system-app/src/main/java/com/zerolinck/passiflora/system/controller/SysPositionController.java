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
import com.zerolinck.passiflora.feign.system.SysPositionApi;
import com.zerolinck.passiflora.model.system.dto.PositionPermissionSaveDto;
import com.zerolinck.passiflora.model.system.entity.SysPosition;
import com.zerolinck.passiflora.model.system.vo.SysPositionVo;
import com.zerolinck.passiflora.system.service.SysPositionPermissionService;
import com.zerolinck.passiflora.system.service.SysPositionService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author linck
 * @since 2024-05-14
 */
@Slf4j
@RestController
@RequestMapping("sysPosition")
public class SysPositionController implements SysPositionApi {

    @Resource
    private SysPositionService sysPositionService;

    @Resource
    private SysPositionPermissionService sysPositionPermissionService;

    @Override
    public Result<ListWithPage<SysPosition>> page(
        QueryCondition<SysPosition> condition
    ) {
        return Result.page(sysPositionService.page(condition));
    }

    @Override
    public Result<String> add(SysPosition sysPosition) {
        sysPosition.setPositionId(IdWorker.getIdStr());
        if (StrUtil.isBlank(sysPosition.getParentPositionId())) {
            sysPosition.setParentPositionId("0");
        }
        sysPositionService.add(sysPosition);
        return Result.ok(sysPosition.getPositionId());
    }

    @Override
    public Result<String> update(SysPosition sysPosition) {
        if (StrUtil.isBlank(sysPosition.getParentPositionId())) {
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
        AssertUtil.notBlank(positionId);
        return Result.ok(sysPositionService.detail(positionId));
    }

    @Override
    public Result<String> delete(List<String> positionIds) {
        AssertUtil.notEmpty(positionIds);
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
        AssertUtil.notEmpty(sysPositionVos);
        sysPositionService.updateOrder(sysPositionVos);
        return Result.ok();
    }

    @Override
    public Result<List<String>> permissionIdsByPositionIds(List<String> positionIds) {
        AssertUtil.notEmpty(positionIds);
        return Result.ok(sysPositionPermissionService.permissionIdsByPositionIds(positionIds));
    }

    @Override
    public Result<String> savePositionPermission(PositionPermissionSaveDto positionPermissionSaveDto) {
        sysPositionPermissionService.savePositionPermission(positionPermissionSaveDto);
        return Result.ok();
    }
}
