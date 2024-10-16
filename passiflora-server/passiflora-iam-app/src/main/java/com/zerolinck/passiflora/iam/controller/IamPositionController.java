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
import com.zerolinck.passiflora.common.api.ResultCodeEnum;
import com.zerolinck.passiflora.common.util.AssertUtil;
import com.zerolinck.passiflora.common.util.QueryCondition;
import com.zerolinck.passiflora.feign.iam.IamPositionApi;
import com.zerolinck.passiflora.iam.service.IamPositionPermissionService;
import com.zerolinck.passiflora.iam.service.IamPositionService;
import com.zerolinck.passiflora.model.iam.args.PositionPermissionSaveArgs;
import com.zerolinck.passiflora.model.iam.entity.IamPosition;
import com.zerolinck.passiflora.model.iam.vo.IamPositionVo;
import java.util.List;
import java.util.NoSuchElementException;
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
@RequestMapping("iam-position")
@RequiredArgsConstructor
public class IamPositionController implements IamPositionApi {

    private final IamPositionService iamPositionService;
    private final IamPositionPermissionService iamPositionPermissionService;

    @Override
    public Result<List<IamPosition>> page(QueryCondition<IamPosition> condition) {
        return Result.ok(iamPositionService.page(condition));
    }

    @Override
    public Result<String> add(IamPosition iamPosition) {
        iamPosition.setPositionId(IdWorker.getIdStr());
        if (StringUtils.isBlank(iamPosition.getParentPositionId())) {
            iamPosition.setParentPositionId("0");
        }
        iamPositionService.add(iamPosition);
        return Result.ok(iamPosition.getPositionId());
    }

    @Override
    public Result<String> update(IamPosition iamPosition) {
        if (StringUtils.isBlank(iamPosition.getParentPositionId())) {
            iamPosition.setParentPositionId("0");
        }
        boolean success = iamPositionService.update(iamPosition);
        if (success) {
            return Result.ok(iamPosition.getPositionId());
        } else {
            return Result.failed(ResultCodeEnum.COMPETE_FAILED);
        }
    }

    @Override
    public Result<IamPosition> detail(String positionId) {
        AssertUtil.notBlank(positionId, "职位 ID 不能为空");
        return Result.ok(iamPositionService.detail(positionId).orElseThrow(() -> new NoSuchElementException("职位不存在")));
    }

    @Override
    public Result<String> delete(List<String> positionIds) {
        AssertUtil.notEmpty(positionIds, "职位 ID 不能为空");
        iamPositionService.deleteByIds(positionIds);
        return Result.ok();
    }

    @Override
    public Result<List<IamPositionVo>> positionTree() {
        return Result.ok(iamPositionService.positionTree());
    }

    @Override
    public Result<String> disable(List<String> positionIds) {
        iamPositionService.disable(positionIds);
        return Result.ok();
    }

    @Override
    public Result<String> enable(List<String> positionIds) {
        iamPositionService.enable(positionIds);
        return Result.ok();
    }

    @Override
    public Result<String> updateOrder(List<IamPositionVo> iamPositionVos) {
        iamPositionService.updateOrder(iamPositionVos);
        return Result.ok();
    }

    @Override
    public Result<List<String>> permissionIdsByPositionIds(List<String> positionIds) {
        AssertUtil.notEmpty(positionIds, "职位 ID 不能为空");
        return Result.ok(iamPositionPermissionService.permissionIdsByPositionIds(positionIds));
    }

    @Override
    public Result<String> savePositionPermission(PositionPermissionSaveArgs args) {
        iamPositionPermissionService.savePositionPermission(args);
        return Result.ok();
    }
}
