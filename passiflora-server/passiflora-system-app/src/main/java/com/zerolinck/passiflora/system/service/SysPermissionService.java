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
package com.zerolinck.passiflora.system.service;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zerolinck.passiflora.common.exception.BizException;
import com.zerolinck.passiflora.common.util.CurrentUtil;
import com.zerolinck.passiflora.common.util.OnlyFieldCheck;
import com.zerolinck.passiflora.common.util.QueryCondition;
import com.zerolinck.passiflora.common.util.lock.LockUtil;
import com.zerolinck.passiflora.common.util.lock.LockWrapper;
import com.zerolinck.passiflora.model.common.enums.StatusEnum;
import com.zerolinck.passiflora.model.system.entity.SysPermission;
import com.zerolinck.passiflora.model.system.enums.PermissionTypeEnum;
import com.zerolinck.passiflora.model.system.mapperstruct.SysPermissionConvert;
import com.zerolinck.passiflora.model.system.vo.SysPermissionTableVo;
import com.zerolinck.passiflora.model.system.vo.SysPermissionVo;
import com.zerolinck.passiflora.system.mapper.SysPermissionMapper;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author linck
 * @since 2024-05-06
 */
@Slf4j
@Service
public class SysPermissionService
    extends ServiceImpl<SysPermissionMapper, SysPermission> {

    private static final String LOCK_KEY = "passiflora:lock:sysPermission:";

    @Nonnull
    public Page<SysPermission> page(
        @Nonnull QueryCondition<SysPermission> condition
    ) {
        return baseMapper.page(
            condition.page(),
            condition.searchWrapper(SysPermission.class),
            condition.sortWrapper(SysPermission.class)
        );
    }

    public void add(@Nonnull SysPermission sysPermission) {
        LockUtil.lockAndTransactionalLogic(
            LOCK_KEY,
            new LockWrapper<SysPermission>()
                .lock(
                    SysPermission::getPermissionTitle,
                    sysPermission.getPermissionTitle()
                ),
            () -> {
                OnlyFieldCheck.checkInsert(baseMapper, sysPermission);
                generateIadPathAndLevel(sysPermission);
                baseMapper.insert(sysPermission);
                return null;
            }
        );
    }

    public boolean update(@Nonnull SysPermission sysPermission) {
        return LockUtil.lockAndTransactionalLogic(
            LOCK_KEY,
            new LockWrapper<SysPermission>()
                .lock(
                    SysPermission::getPermissionTitle,
                    sysPermission.getPermissionTitle()
                ),
            () -> {
                OnlyFieldCheck.checkUpdate(baseMapper, sysPermission);
                generateIadPathAndLevel(sysPermission);
                int changeRowCount = baseMapper.updateById(sysPermission);
                // 子权限数据变更
                List<SysPermission> permissionList = listByParentId(
                    sysPermission.getPermissionId()
                );
                permissionList.forEach(permission -> {
                    generateIadPathAndLevel(permission);
                    baseMapper.updateById(permission);
                });
                return changeRowCount > 0;
            }
        );
    }

    @Nonnull
    public List<SysPermission> listByParentId(
        @Nonnull String permissionParentId
    ) {
        return baseMapper.listByParentId(permissionParentId);
    }

    @Transactional(rollbackFor = Exception.class)
    public int deleteByIds(@Nonnull Collection<String> permissionIds) {
        return baseMapper.deleteByIds(
            permissionIds,
            CurrentUtil.getCurrentUserId()
        );
    }

    @Nullable public SysPermission detail(@Nonnull String permissionId) {
        SysPermission sysPermission = baseMapper.selectById(permissionId);
        if (sysPermission == null) {
            throw new BizException("无对应菜单数据，请刷新后重试");
        }
        return sysPermission;
    }

    @Nonnull
    public List<SysPermissionVo> menuTree() {
        // TODO 查询当前用户有权限的菜单
        Map<String, List<SysPermissionVo>> menuMap = baseMapper
            .selectList(
                new LambdaQueryWrapper<SysPermission>()
                    .eq(SysPermission::getPermissionStatus, StatusEnum.ENABLE)
                    .in(
                        SysPermission::getPermissionType,
                        List.of(
                            PermissionTypeEnum.MENU_SET,
                            PermissionTypeEnum.MENU
                        )
                    )
                    .orderByAsc(SysPermission::getPermissionLevel)
                    .orderByAsc(SysPermission::getOrder)
            )
            .stream()
            .map(SysPermissionConvert.INSTANCE::entity2vo)
            .collect(
                Collectors.groupingBy(
                    SysPermissionVo::getPermissionParentId,
                    Collectors.toList()
                )
            );
        // 顶层菜单
        List<SysPermissionVo> topMenu = menuMap.get("0");
        if (CollectionUtil.isEmpty(topMenu)) {
            return new ArrayList<>();
        }
        dealMenuTree(topMenu, menuMap);
        return topMenu;
    }

    @Nonnull
    public List<SysPermissionTableVo> permissionTableTree() {
        // TODO 查询当前用户有权限的菜单
        Map<String, List<SysPermissionTableVo>> menuMap = baseMapper
            .selectList(
                new LambdaQueryWrapper<SysPermission>()
                    .in(
                        SysPermission::getPermissionType,
                        List.of(
                            PermissionTypeEnum.MENU_SET,
                            PermissionTypeEnum.MENU
                        )
                    )
                    .orderByAsc(SysPermission::getPermissionLevel)
                    .orderByAsc(SysPermission::getOrder)
            )
            .stream()
            .map(SysPermissionConvert.INSTANCE::entity2tableVo)
            .collect(
                Collectors.groupingBy(
                    com.zerolinck.passiflora.model.system.vo.SysPermissionTableVo::getPermissionParentId,
                    Collectors.toList()
                )
            );
        // 顶层菜单
        List<SysPermissionTableVo> topMenu = menuMap.get("0");
        if (CollectionUtil.isEmpty(topMenu)) {
            return new ArrayList<>();
        }
        permissionTableTree(topMenu, menuMap);
        return topMenu;
    }

    public void updateOrder(
        @Nonnull List<SysPermissionTableVo> sysPermissionTableVos
    ) {
        if (CollectionUtil.isEmpty(sysPermissionTableVos)) {
            return;
        }
        for (SysPermissionTableVo sysPermissionTableVo : sysPermissionTableVos) {
            baseMapper.updateOrder(sysPermissionTableVo);
            updateOrder(sysPermissionTableVo.getChildren());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void disable(@Nonnull List<String> permissionIds) {
        baseMapper.disable(permissionIds, CurrentUtil.getCurrentUserId());
    }

    @Transactional(rollbackFor = Exception.class)
    public void enable(@Nonnull List<String> permissionIds) {
        List<String> pathIds = new ArrayList<>();
        permissionIds.forEach(permissionId -> {
            SysPermission sysPermission = baseMapper.selectById(permissionId);
            String[] permissionIdList = sysPermission
                .getPermissionIdPath()
                .split("/");
            Collections.addAll(pathIds, permissionIdList);
        });
        baseMapper.enable(pathIds, CurrentUtil.getCurrentUserId());
    }

    @Nonnull
    public List<SysPermission> listSelfAndSub(@Nonnull String permissionId) {
        return baseMapper.listSelfAndSub(permissionId);
    }

    private void dealMenuTree(
        @Nonnull List<SysPermissionVo> menuVos,
        @Nonnull Map<String, List<SysPermissionVo>> menuMap
    ) {
        for (SysPermissionVo menu : menuVos) {
            if (menuMap.containsKey(menu.getPermissionId())) {
                menu.setChildren(menuMap.get(menu.getPermissionId()));
                dealMenuTree(menu.getChildren(), menuMap);
            }
        }
    }

    private void permissionTableTree(
        @Nonnull List<SysPermissionTableVo> menuVos,
        @Nonnull Map<String, List<SysPermissionTableVo>> menuMap
    ) {
        for (SysPermissionTableVo menu : menuVos) {
            if (menuMap.containsKey(menu.getPermissionId())) {
                menu.setChildren(menuMap.get(menu.getPermissionId()));
                permissionTableTree(menu.getChildren(), menuMap);
            }
        }
    }

    private void generateIadPathAndLevel(@Nonnull SysPermission sysPermission) {
        StringBuilder codeBuffer = new StringBuilder();
        String permissionParentId = sysPermission.getPermissionParentId();
        int level = 1;
        if (!"0".equals(permissionParentId)) {
            SysPermission parentSysPermission = detail(permissionParentId);
            assert parentSysPermission != null;
            codeBuffer
                .append(parentSysPermission.getPermissionIdPath())
                .append("/");
            level = parentSysPermission.getPermissionLevel() + 1;
        }
        codeBuffer.append(sysPermission.getPermissionId());
        sysPermission.setPermissionIdPath(codeBuffer.toString());
        sysPermission.setPermissionLevel(level);
    }
}
