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
package com.zerolinck.passiflora.iam.service;

import java.util.*;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.zerolinck.passiflora.common.exception.BizException;
import com.zerolinck.passiflora.common.util.OnlyFieldCheck;
import com.zerolinck.passiflora.common.util.QueryCondition;
import com.zerolinck.passiflora.common.util.lock.LockUtil;
import com.zerolinck.passiflora.common.util.lock.LockWrapper;
import com.zerolinck.passiflora.iam.mapper.IamPositionMapper;
import com.zerolinck.passiflora.model.iam.entity.IamPosition;
import com.zerolinck.passiflora.model.iam.mapperstruct.IamPositionConvert;
import com.zerolinck.passiflora.model.iam.resp.IamPositionResp;
import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** @author linck on 2024-05-14 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IamPositionService extends ServiceImpl<IamPositionMapper, IamPosition> {

    private static final String LOCK_KEY = "passiflora:lock:iamPosition:";

    private final IamPositionPermissionService iamPositionPermissionService;

    /**
     * 分页查询
     *
     * @param condition 搜索条件
     * @since 2024-08-12
     */
    @NotNull public Page<IamPosition> page(@Nullable QueryCondition<IamPosition> condition) {
        condition = Objects.requireNonNullElse(condition, new QueryCondition<>());
        return mapper.paginate(
                condition.getPageNumber(), condition.getPageSize(), condition.searchWrapper(IamPosition.class));
    }

    /**
     * 根据职位ID集合查询数据
     *
     * @param positionIds 职位集合
     * @since 2024-08-12
     */
    @NotNull @SuppressWarnings("unused")
    public List<IamPosition> listByIds(@Nullable List<String> positionIds) {
        positionIds = Objects.requireNonNullElse(positionIds, Collections.emptyList());
        return mapper.selectListByQuery(new QueryWrapper().in(IamPosition::getPositionId, positionIds));
    }

    /**
     * 新增职位
     *
     * @param iamPosition 新增职位
     * @since 2024-08-12
     */
    public void add(@NotNull IamPosition iamPosition) {
        LockUtil.lock(
                LOCK_KEY,
                new LockWrapper<IamPosition>().lock(IamPosition::getPositionName, iamPosition.getPositionName()),
                true,
                () -> {
                    OnlyFieldCheck.checkInsert(mapper, iamPosition);
                    generateIdPathAndLevel(iamPosition);
                    mapper.insert(iamPosition);
                });
    }

    /**
     * 更新职位
     *
     * @param iamPosition 更新职位
     * @since 2024-08-12
     */
    public boolean update(@NotNull IamPosition iamPosition) {
        return LockUtil.lock(
                LOCK_KEY,
                new LockWrapper<IamPosition>().lock(IamPosition::getPositionName, iamPosition.getPositionName()),
                true,
                () -> {
                    OnlyFieldCheck.checkUpdate(mapper, iamPosition);
                    generateIdPathAndLevel(iamPosition);
                    int changeRowCount = mapper.update(iamPosition);
                    // 子机构数据变更
                    List<IamPositionResp> positionResps = mapper.listByParentId(iamPosition.getPositionId());
                    positionResps.forEach(resp -> {
                        IamPosition position = IamPositionConvert.INSTANCE.respToEntity(resp);
                        generateIdPathAndLevel(position);
                        mapper.update(position);
                    });
                    return changeRowCount > 0;
                });
    }

    /**
     * 根据职位ID集合删除数据
     *
     * @param positionIds 职位ID集合
     * @since 2024-08-12
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteByIds(@Nullable Collection<String> positionIds) {
        if (CollectionUtils.isEmpty(positionIds)) {
            return 0;
        }
        int changeRowNum = mapper.deleteBatchByIds(positionIds, 500);
        iamPositionPermissionService.deleteByPositionIds(positionIds);
        return changeRowNum;
    }

    /**
     * 查询详情
     *
     * @param positionId 职位ID
     * @since 2024-08-12
     */
    @NotNull public Optional<IamPosition> detail(@NotNull String positionId) {
        return Optional.ofNullable(mapper.selectOneById(positionId));
    }

    /**
     * 获取职位树
     *
     * @since 2024-08-12
     */
    @NotNull public List<IamPositionResp> positionTree() {
        List<IamPositionResp> iamPositionResps = mapper.listByParentId("0");
        iamPositionResps.forEach(this::recursionTree);
        return iamPositionResps;
    }

    /**
     * 禁用职位，会关联禁用下级职位
     *
     * @param positionIds 职位ID集合
     * @since 2024-08-12
     */
    @Transactional(rollbackFor = Exception.class)
    public void disable(@Nullable List<String> positionIds) {
        if (CollectionUtils.isEmpty(positionIds)) {
            return;
        }
        mapper.disable(positionIds);
    }

    /**
     * 启用职位，会关联启动上级职位
     *
     * @param positionIds 职位ID集合
     * @since 2024-08-12
     */
    @Transactional(rollbackFor = Exception.class)
    public void enable(@Nullable List<String> positionIds) {
        if (CollectionUtils.isEmpty(positionIds)) {
            return;
        }
        List<String> pathIds = new ArrayList<>();
        positionIds.forEach(positionId -> {
            IamPosition iamPosition = mapper.selectOneById(positionId);
            String[] positionIdList = iamPosition.getPositionIdPath().split("/");
            Collections.addAll(pathIds, positionIdList);
        });
        mapper.enable(pathIds);
    }

    /**
     * 更新职位排序
     *
     * @param iamPositionResps 职位数数据
     * @since 2024-08-12
     */
    public void updateOrder(@Nullable Collection<IamPositionResp> iamPositionResps) {
        if (CollectionUtils.isEmpty(iamPositionResps)) {
            return;
        }
        for (IamPositionResp iamPositionTableVo : iamPositionResps) {
            mapper.updateOrder(iamPositionTableVo);
            updateOrder(iamPositionTableVo.getChildren());
        }
    }

    /**
     * 递归处理职位树
     *
     * @param iamPositionResp 职位
     * @since 2024-08-12
     */
    private void recursionTree(@NotNull IamPositionResp iamPositionResp) {
        iamPositionResp.setChildren(mapper.listByParentId(iamPositionResp.getPositionId()));
        iamPositionResp.getChildren().forEach(this::recursionTree);
    }

    /**
     * 生成职位 idPath 和 level 字段
     *
     * @param iamPosition 职位
     * @since 2024-08-12
     */
    private void generateIdPathAndLevel(@NotNull IamPosition iamPosition) {
        StringBuilder codeBuffer = new StringBuilder();
        String positionParentId = iamPosition.getParentPositionId();
        int level = 0;
        if (!"0".equals(positionParentId)) {
            IamPosition parentOrg = detail(positionParentId)
                    .orElseThrow(() -> new BizException("职位数据错误，%s无上级职位", iamPosition.getPositionName()));
            codeBuffer.append(parentOrg.getPositionIdPath()).append("/");
            level = parentOrg.getPositionLevel() + 1;
        }
        codeBuffer.append(iamPosition.getPositionId());
        iamPosition.setPositionLevel(level);
        iamPosition.setPositionIdPath(codeBuffer.toString());
    }
}
