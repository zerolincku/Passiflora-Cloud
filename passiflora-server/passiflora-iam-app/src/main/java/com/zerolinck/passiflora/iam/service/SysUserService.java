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
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zerolinck.passiflora.common.api.ResultCodeEnum;
import com.zerolinck.passiflora.common.config.PassifloraProperties;
import com.zerolinck.passiflora.common.exception.BizException;
import com.zerolinck.passiflora.common.util.*;
import com.zerolinck.passiflora.common.util.lock.LockUtil;
import com.zerolinck.passiflora.common.util.lock.LockWrapper;
import com.zerolinck.passiflora.iam.mapper.SysUserMapper;
import com.zerolinck.passiflora.model.common.constant.RedisPrefix;
import com.zerolinck.passiflora.model.iam.args.SysUserSaveArgs;
import com.zerolinck.passiflora.model.iam.entity.SysPermission;
import com.zerolinck.passiflora.model.iam.entity.SysUser;
import com.zerolinck.passiflora.model.iam.enums.PermissionTypeEnum;
import com.zerolinck.passiflora.model.iam.mapperstruct.SysUserConvert;
import com.zerolinck.passiflora.model.iam.vo.SysUserInfo;
import com.zerolinck.passiflora.model.iam.vo.SysUserPositionVo;
import com.zerolinck.passiflora.model.iam.vo.SysUserRoleVo;
import com.zerolinck.passiflora.model.iam.vo.SysUserVo;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author linck
 * @since 2024-02-07
 */
@Service
@RequiredArgsConstructor
public class SysUserService extends ServiceImpl<SysUserMapper, SysUser> {

    private final SysOrgService sysOrgService;
    private final SysUserPositionService userPositionService;
    private final PassifloraProperties passifloraProperties;
    private final SysUserPositionService sysUserPositionService;
    private final SysPermissionService sysPermissionService;
    private final SysUserRoleService sysUserRoleService;

    private static final String LOCK_KEY = "passiflora:lock:sysUser:";

    @Nonnull
    public Page<SysUserVo> page(@Nonnull String orgId, @Nullable QueryCondition<SysUser> condition) {
        condition = Objects.requireNonNullElse(condition, new QueryCondition<>());
        Page<SysUser> page = baseMapper.page(
                orgId, condition.page(), condition.searchWrapper(SysUser.class), condition.sortWrapper(SysUser.class));

        Set<String> userIds = page.getRecords().stream().map(SysUser::getUserId).collect(Collectors.toSet());
        Set<String> orgIds = page.getRecords().stream()
                .map(SysUser::getOrgId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        // 查询 org 信息
        Map<String, String> orgId2NameMap = sysOrgService.orgId2NameMap(orgIds);

        // 查询职位信息
        Map<String, List<SysUserPositionVo>> userPositionMap = sysUserPositionService.selectByUserIds(userIds).stream()
                .collect(Collectors.groupingBy(SysUserPositionVo::getUserId));

        // 查询角色信息
        Map<String, List<SysUserRoleVo>> userRoleMap = sysUserRoleService.selectByUserIds(userIds).stream()
                .collect(Collectors.groupingBy(SysUserRoleVo::getUserId));

        List<SysUserVo> recordsVo = page.getRecords().stream()
                .map(sysUser -> {
                    SysUserVo sysUserVo = SysUserConvert.INSTANCE.entity2vo(sysUser);
                    // 填充机构信息
                    sysUserVo.setOrgName(orgId2NameMap.get(sysUser.getOrgId()));

                    // 填充职位信息
                    List<SysUserPositionVo> positionVoList =
                            userPositionMap.getOrDefault(sysUser.getUserId(), new ArrayList<>());
                    sysUserVo.setPositionIds(positionVoList.stream()
                            .map(SysUserPositionVo::getPositionId)
                            .collect(Collectors.toList()));
                    sysUserVo.setPositionNames(positionVoList.stream()
                            .map(SysUserPositionVo::getPositionName)
                            .collect(Collectors.toList()));

                    // 填充角色信息
                    List<SysUserRoleVo> userRoleVos = userRoleMap.getOrDefault(sysUser.getUserId(), new ArrayList<>());
                    sysUserVo.setRoleIds(
                            userRoleVos.stream().map(SysUserRoleVo::getRoleId).collect(Collectors.toList()));
                    sysUserVo.setRoleNames(
                            userRoleVos.stream().map(SysUserRoleVo::getRoleName).collect(Collectors.toList()));

                    return sysUserVo;
                })
                .toList();

        return new Page<SysUserVo>(page.getCurrent(), page.getSize(), page.getTotal()).setRecords(recordsVo);
    }

    public void add(@Nonnull SysUserSaveArgs args) {
        args.setUserPassword(PwdUtil.hashPassword(args.getUserPassword()));

        LockUtil.lock(LOCK_KEY, new LockWrapper<SysUser>().lock(SysUser::getUserName, args.getUserName()), true, () -> {
            OnlyFieldCheck.checkInsert(baseMapper, args);
            baseMapper.insert(args);
            userPositionService.updateRelation(args);
            sysUserRoleService.updateRelation(args);
        });
    }

    public boolean update(@Nonnull SysUserSaveArgs args) {
        return LockUtil.lock(
                LOCK_KEY, new LockWrapper<SysUser>().lock(SysUser::getUserName, args.getUserName()), true, () -> {
                    OnlyFieldCheck.checkUpdate(baseMapper, args);
                    int changeRowCount = baseMapper.updateById(args);
                    userPositionService.updateRelation(args);
                    sysUserRoleService.updateRelation(args);
                    return changeRowCount > 0;
                });
    }

    @Transactional(rollbackFor = Exception.class)
    public int deleteByIds(@Nullable Collection<String> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return 0;
        }
        int count = baseMapper.deleteByIds(userIds, CurrentUtil.getCurrentUserId());
        userPositionService.deleteByUserIds(userIds);
        return count;
    }

    @Nonnull
    public Optional<SysUser> detail(@Nonnull String userId) {
        return Optional.ofNullable(baseMapper.selectById(userId));
    }

    @Nonnull
    public SysUserInfo currentUserInfo() {
        String userId = CurrentUtil.getCurrentUserId();
        SysUser sysUser = this.getById(userId);
        if (StringUtils.isBlank(userId) || Objects.isNull(sysUser)) {
            throw new BizException(ResultCodeEnum.UNAUTHORIZED);
        }

        SysUserInfo sysUserInfo = SysUserConvert.INSTANCE.entity2info(sysUser);
        List<SysPermission> permissionList = sysPermissionService.listByUserIds(userId);

        permissionList.forEach(sysPermission -> {
            if (PermissionTypeEnum.MENU.equals(sysPermission.getPermissionType())
                    || PermissionTypeEnum.MENU_SET.equals(sysPermission.getPermissionType())) {
                sysUserInfo.getMenu().add(sysPermission.getPermissionName());
            } else {
                sysUserInfo.getPermission().add(sysPermission.getPermissionName());
            }
        });
        return sysUserInfo;
    }

    @Nonnull
    public String login(@Nonnull SysUser sysUser) {
        SysUser dbSysUser =
                baseMapper.selectOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUserName, sysUser.getUserName()));
        if (dbSysUser == null) {
            throw new BizException("账号或密码错误");
        }
        if (!PwdUtil.verifyPassword(sysUser.getUserPassword(), dbSysUser.getUserPassword())) {
            throw new BizException("账号或密码错误");
        }
        String token = IdWorker.getIdStr();
        RedisUtils.set(
                RedisPrefix.TOKEN_KEY + dbSysUser.getUserId() + ":" + token,
                dbSysUser,
                passifloraProperties.getSystem().getToken().getExpire());
        return token;
    }

    public void logout() {
        Set<String> keys = RedisUtils.keys(RedisPrefix.TOKEN_KEY + "*:" + CurrentUtil.getToken());
        if (CollectionUtils.isEmpty(keys)) {
            return;
        }
        keys.forEach(RedisUtils::del);
    }

    public Boolean checkToken() {
        Set<String> keys = RedisUtils.keys(RedisPrefix.TOKEN_KEY + "*:" + CurrentUtil.getToken());
        if (CollectionUtils.isEmpty(keys)) {
            return Boolean.FALSE;
        }
        keys.forEach(key -> RedisUtils.expire(
                key, passifloraProperties.getSystem().getToken().getExpire()));
        return Boolean.TRUE;
    }
}
