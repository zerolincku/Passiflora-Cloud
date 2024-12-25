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
import java.util.stream.Collectors;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.zerolinck.passiflora.base.constant.Constants;
import com.zerolinck.passiflora.base.enums.StatusEnum;
import com.zerolinck.passiflora.common.exception.BizException;
import com.zerolinck.passiflora.common.util.CurrentUtil;
import com.zerolinck.passiflora.common.util.OnlyFieldCheck;
import com.zerolinck.passiflora.common.util.QueryCondition;
import com.zerolinck.passiflora.common.util.lock.LockUtil;
import com.zerolinck.passiflora.common.util.lock.LockWrapper;
import com.zerolinck.passiflora.iam.mapper.IamPermissionMapper;
import com.zerolinck.passiflora.model.iam.entity.IamPermission;
import com.zerolinck.passiflora.model.iam.enums.PermissionTypeEnum;
import com.zerolinck.passiflora.model.iam.mapperstruct.IamPermissionConvert;
import com.zerolinck.passiflora.model.iam.resp.IamPermissionResp;
import com.zerolinck.passiflora.model.iam.resp.IamPermissionTableResp;
import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

/** @author linck on 2024-05-06 */
@Slf4j
@Service
public class IamPermissionService extends ServiceImpl<IamPermissionMapper, IamPermission> {

    private static final String LOCK_KEY = "passiflora:lock:iamPermission:";

    @NotNull public Page<IamPermission> page(@Nullable QueryCondition<IamPermission> condition) {
        condition = Objects.requireNonNullElse(condition, new QueryCondition<>());
        return mapper.paginate(
                condition.getPageNumber(), condition.getPageSize(), condition.searchWrapper(IamPermission.class));
    }

    public void add(@NotNull IamPermission iamPermission) {
        LockUtil.lock(
                LOCK_KEY,
                new LockWrapper<IamPermission>()
                        .lock(IamPermission::getPermissionTitle, iamPermission.getPermissionTitle()),
                true,
                () -> {
                    OnlyFieldCheck.checkInsert(mapper, iamPermission);
                    generateIadPathAndLevel(iamPermission);
                    mapper.insert(iamPermission);
                });
    }

    public boolean update(@NotNull IamPermission iamPermission) {
        return LockUtil.lock(
                LOCK_KEY,
                new LockWrapper<IamPermission>()
                        .lock(IamPermission::getPermissionTitle, iamPermission.getPermissionTitle()),
                true,
                () -> {
                    OnlyFieldCheck.checkUpdate(mapper, iamPermission);
                    generateIadPathAndLevel(iamPermission);
                    int changeRowCount = mapper.update(iamPermission);
                    // 子权限数据变更
                    List<IamPermission> permissionList = listByParentId(iamPermission.getPermissionId());
                    permissionList.forEach(permission -> {
                        generateIadPathAndLevel(permission);
                        mapper.update(permission);
                    });
                    return changeRowCount > 0;
                });
    }

    @NotNull public List<IamPermission> listByParentId(@NotNull String permissionParentId) {
        return mapper.listByParentId(permissionParentId);
    }

    @Transactional(rollbackFor = Exception.class)
    public int deleteByIds(@Nullable Collection<String> permissionIds) {
        if (CollectionUtils.isEmpty(permissionIds)) {
            return 0;
        }
        return mapper.deleteBatchByIds(permissionIds, 500);
    }

    @NotNull public Optional<IamPermission> detail(@NotNull String permissionId) {
        return Optional.ofNullable(mapper.selectOneById(permissionId));
    }

    @NotNull public List<IamPermissionResp> menuTree() {
        Map<String, List<IamPermissionResp>> menuMap = this.listByUserIds(CurrentUtil.requireCurrentUserId()).stream()
                .filter(permission -> PermissionTypeEnum.MENU.equals(permission.getPermissionType())
                        || PermissionTypeEnum.MENU_SET.equals(permission.getPermissionType()))
                .filter(permission -> StatusEnum.ENABLE.equals(permission.getPermissionStatus()))
                .map(IamPermissionConvert.INSTANCE::entityToResp)
                .collect(Collectors.groupingBy(IamPermissionResp::getPermissionParentId, Collectors.toList()));
        // 顶层菜单
        List<IamPermissionResp> topMenu = menuMap.get("0");
        if (CollectionUtils.isEmpty(topMenu)) {
            return new ArrayList<>();
        }
        dealMenuTree(topMenu, menuMap);
        return topMenu;
    }

    @NotNull public List<IamPermissionTableResp> permissionTableTree() {
        Map<String, List<IamPermissionTableResp>> menuMap =
                this.listByUserIds(CurrentUtil.requireCurrentUserId()).stream()
                        .map(IamPermissionConvert.INSTANCE::entityToTableResp)
                        .collect(Collectors.groupingBy(
                                IamPermissionTableResp::getPermissionParentId, Collectors.toList()));
        // 顶层菜单
        List<IamPermissionTableResp> topMenu = menuMap.get("0");
        if (CollectionUtils.isEmpty(topMenu)) {
            return new ArrayList<>();
        }
        permissionTableTree(topMenu, menuMap);
        return topMenu;
    }

    public void updateOrder(@Nullable Collection<IamPermissionTableResp> iamPermissionTableResps) {
        if (CollectionUtils.isEmpty(iamPermissionTableResps)) {
            return;
        }
        for (IamPermissionTableResp iamPermissionTableResp : iamPermissionTableResps) {
            mapper.updateOrder(iamPermissionTableResp);
            updateOrder(iamPermissionTableResp.getChildren());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void disable(@Nullable List<String> permissionIds) {
        if (CollectionUtils.isEmpty(permissionIds)) {
            return;
        }
        mapper.disable(permissionIds);
    }

    @Transactional(rollbackFor = Exception.class)
    public void enable(@Nullable List<String> permissionIds) {
        if (CollectionUtils.isEmpty(permissionIds)) {
            return;
        }
        List<String> pathIds = new ArrayList<>();
        permissionIds.forEach(permissionId -> {
            IamPermission iamPermission = mapper.selectOneById(permissionId);
            String[] permissionIdList = iamPermission.getPermissionIdPath().split("/");
            Collections.addAll(pathIds, permissionIdList);
        });
        mapper.enable(pathIds, CurrentUtil.getCurrentUserId());
    }

    @NotNull @SuppressWarnings("unused")
    public List<IamPermission> listSelfAndSub(@NotNull String permissionId) {
        return mapper.listSelfAndSub(permissionId);
    }

    /**
     * 根据职位ID查询权限
     *
     * @param positionId 职位ID
     * @since 2024-08-12
     */
    @NotNull @SuppressWarnings("unused")
    List<IamPermission> listByPositionIds(@NotNull String positionId) {
        return mapper.listByPositionId(positionId);
    }

    /**
     * 根据角色ID查询权限
     *
     * @param roleId 角色ID
     * @author 林常坤 on 2024/8/17
     */
    @NotNull @SuppressWarnings("unused")
    List<IamPermission> listByRoleId(@NotNull String roleId) {
        return mapper.listByRoleId(roleId);
    }

    /**
     * 根据用户ID查询权限，包含职位关联和角色关联
     *
     * @param userId 用户ID
     * @since 2024-08-12
     */
    @NotNull @SuppressWarnings("unused")
    List<IamPermission> listByUserIds(@NotNull String userId) {
        if (Constants.SUPER_ADMIN_ID.equals(userId)) {
            return mapper.selectListByQuery(new QueryWrapper()
                    .orderBy(IamPermission::getPermissionLevel, true)
                    .orderBy(IamPermission::getOrder, true)
                    .orderBy(IamPermission::getPermissionTitle, true));
        }
        return mapper.listByUserId(userId);
    }

    /**
     * 递归处理菜单树
     *
     * @param menuVos 菜单
     * @param menuMap 菜单Map
     * @author 林常坤 on 2024/12/20
     */
    private void dealMenuTree(
            @Nullable Collection<IamPermissionResp> menuVos, @NotNull Map<String, List<IamPermissionResp>> menuMap) {
        if (CollectionUtils.isEmpty(menuVos)) {
            return;
        }
        for (IamPermissionResp menu : menuVos) {
            if (menuMap.containsKey(menu.getPermissionId())) {
                menu.setChildren(menuMap.get(menu.getPermissionId()));
                dealMenuTree(menu.getChildren(), menuMap);
            }
        }
    }

    /**
     * 递归处理权限树
     *
     * @param menuVos 权限
     * @param menuMap 权限Map
     * @author 林常坤 on 2024/12/20
     */
    private void permissionTableTree(
            @Nullable Collection<IamPermissionTableResp> menuVos,
            @NotNull Map<String, List<IamPermissionTableResp>> menuMap) {
        if (CollectionUtils.isEmpty(menuVos)) {
            return;
        }
        for (IamPermissionTableResp menu : menuVos) {
            if (menuMap.containsKey(menu.getPermissionId())) {
                menu.setChildren(menuMap.get(menu.getPermissionId()));
                permissionTableTree(menu.getChildren(), menuMap);
            }
        }
    }

    /**
     * 生成权限路径和层级
     *
     * @param iamPermission 权限
     * @author 林常坤 on 2024/12/20
     */
    private void generateIadPathAndLevel(@NotNull IamPermission iamPermission) {
        StringBuilder codeBuffer = new StringBuilder();
        String permissionParentId = iamPermission.getPermissionParentId();
        int level = 1;
        if (!"0".equals(permissionParentId)) {
            IamPermission parentIamPermission = detail(permissionParentId)
                    .orElseThrow(() -> new BizException("权限数据错误，%s无上级权限", iamPermission.getPermissionName()));
            assert parentIamPermission != null;
            codeBuffer.append(parentIamPermission.getPermissionIdPath()).append("/");
            level = parentIamPermission.getPermissionLevel() + 1;
        }
        codeBuffer.append(iamPermission.getPermissionId());
        iamPermission.setPermissionIdPath(codeBuffer.toString());
        iamPermission.setPermissionLevel(level);
    }
}
