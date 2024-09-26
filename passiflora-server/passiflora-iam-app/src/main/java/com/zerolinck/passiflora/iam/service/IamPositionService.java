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

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zerolinck.passiflora.common.exception.BizException;
import com.zerolinck.passiflora.common.util.CurrentUtil;
import com.zerolinck.passiflora.common.util.OnlyFieldCheck;
import com.zerolinck.passiflora.common.util.QueryCondition;
import com.zerolinck.passiflora.common.util.lock.LockUtil;
import com.zerolinck.passiflora.common.util.lock.LockWrapper;
import com.zerolinck.passiflora.iam.mapper.IamPositionMapper;
import com.zerolinck.passiflora.model.iam.entity.IamPosition;
import com.zerolinck.passiflora.model.iam.vo.IamPositionVo;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author linck
 * @since 2024-05-14
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IamPositionService extends ServiceImpl<IamPositionMapper, IamPosition> {

    private static final String LOCK_KEY = "passiflora:lock:sysPosition:";

    private final IamPositionPermissionService iamPositionPermissionService;

    /**
     * 分页查询
     *
     * @param condition 搜索条件
     * @since 2024-08-12
     */
    @Nonnull
    public Page<IamPosition> page(@Nullable QueryCondition<IamPosition> condition) {
        condition = Objects.requireNonNullElse(condition, new QueryCondition<>());
        return baseMapper.page(
                condition.page(), condition.searchWrapper(IamPosition.class), condition.sortWrapper(IamPosition.class));
    }

    /**
     * 根据职位ID集合查询数据
     *
     * @param positionIds 职位集合
     * @since 2024-08-12
     */
    @Nonnull
    @SuppressWarnings("unused")
    public List<IamPosition> listByIds(@Nullable List<String> positionIds) {
        positionIds = Objects.requireNonNullElse(positionIds, Collections.emptyList());
        return baseMapper.selectList(new LambdaQueryWrapper<IamPosition>().in(IamPosition::getPositionId, positionIds));
    }

    /**
     * 新增职位
     *
     * @param iamPosition 新增职位
     * @since 2024-08-12
     */
    public void add(@Nonnull IamPosition iamPosition) {
        LockUtil.lock(
                LOCK_KEY,
                new LockWrapper<IamPosition>().lock(IamPosition::getPositionName, iamPosition.getPositionName()),
                true,
                () -> {
                    OnlyFieldCheck.checkInsert(baseMapper, iamPosition);
                    generateIdPathAndLevel(iamPosition);
                    baseMapper.insert(iamPosition);
                });
    }

    /**
     * 更新职位
     *
     * @param iamPosition 更新职位
     * @since 2024-08-12
     */
    public boolean update(@Nonnull IamPosition iamPosition) {
        return LockUtil.lock(
                LOCK_KEY,
                new LockWrapper<IamPosition>().lock(IamPosition::getPositionName, iamPosition.getPositionName()),
                true,
                () -> {
                    OnlyFieldCheck.checkUpdate(baseMapper, iamPosition);
                    generateIdPathAndLevel(iamPosition);
                    int changeRowCount = baseMapper.updateById(iamPosition);
                    // 子机构数据变更
                    List<IamPositionVo> positionVoList = baseMapper.listByParentId(iamPosition.getPositionId());
                    positionVoList.forEach(positionVo -> {
                        generateIdPathAndLevel(positionVo);
                        baseMapper.updateById(positionVo);
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
        int changeRowNum = baseMapper.deleteByIds(positionIds, CurrentUtil.getCurrentUserId());
        iamPositionPermissionService.deleteByPositionIds(positionIds);
        return changeRowNum;
    }

    /**
     * 查询详情
     *
     * @param positionId 职位ID
     * @since 2024-08-12
     */
    @Nonnull
    public Optional<IamPosition> detail(@Nonnull String positionId) {
        return Optional.ofNullable(baseMapper.selectById(positionId));
    }

    /**
     * 获取职位树
     *
     * @since 2024-08-12
     */
    @Nonnull
    public List<IamPositionVo> positionTree() {
        List<IamPositionVo> sysPositionVos = baseMapper.listByParentId("0");
        sysPositionVos.forEach(this::recursionTree);
        return sysPositionVos;
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
        baseMapper.disable(positionIds, CurrentUtil.getCurrentUserId());
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
            IamPosition iamPosition = baseMapper.selectById(positionId);
            String[] positionIdList = iamPosition.getPositionIdPath().split("/");
            Collections.addAll(pathIds, positionIdList);
        });
        baseMapper.enable(pathIds, CurrentUtil.getCurrentUserId());
    }

    /**
     * 更新职位排序
     *
     * @param sysPositionVos 职位数数据
     * @since 2024-08-12
     */
    public void updateOrder(@Nullable List<IamPositionVo> sysPositionVos) {
        if (CollectionUtils.isEmpty(sysPositionVos)) {
            return;
        }
        for (IamPositionVo sysPositionTableVo : sysPositionVos) {
            baseMapper.updateOrder(sysPositionTableVo);
            updateOrder(sysPositionTableVo.getChildren());
        }
    }

    /**
     * 递归处理职位树
     *
     * @param sysPositionVo 职位
     * @since 2024-08-12
     */
    private void recursionTree(@Nonnull IamPositionVo sysPositionVo) {
        sysPositionVo.setChildren(baseMapper.listByParentId(sysPositionVo.getPositionId()));
        sysPositionVo.getChildren().forEach(this::recursionTree);
    }

    /**
     * 生成职位 idPath 和 level 字段
     *
     * @param iamPosition 职位
     * @since 2024-08-12
     */
    private void generateIdPathAndLevel(@Nonnull IamPosition iamPosition) {
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
