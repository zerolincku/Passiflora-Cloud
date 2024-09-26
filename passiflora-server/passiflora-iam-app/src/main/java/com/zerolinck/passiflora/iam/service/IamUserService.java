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
import com.zerolinck.passiflora.iam.mapper.IamUserMapper;
import com.zerolinck.passiflora.model.common.constant.RedisPrefix;
import com.zerolinck.passiflora.model.iam.args.IamUserSaveArgs;
import com.zerolinck.passiflora.model.iam.entity.IamPermission;
import com.zerolinck.passiflora.model.iam.entity.IamUser;
import com.zerolinck.passiflora.model.iam.enums.PermissionTypeEnum;
import com.zerolinck.passiflora.model.iam.mapperstruct.IamUserConvert;
import com.zerolinck.passiflora.model.iam.vo.IamUserInfo;
import com.zerolinck.passiflora.model.iam.vo.IamUserPositionVo;
import com.zerolinck.passiflora.model.iam.vo.IamUserRoleVo;
import com.zerolinck.passiflora.model.iam.vo.IamUserVo;
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
public class IamUserService extends ServiceImpl<IamUserMapper, IamUser> {

    private final IamOrgService iamOrgService;
    private final IamUserPositionService userPositionService;
    private final PassifloraProperties passifloraProperties;
    private final IamUserPositionService iamUserPositionService;
    private final IamPermissionService iamPermissionService;
    private final IamUserRoleService iamUserRoleService;

    private static final String LOCK_KEY = "passiflora:lock:sysUser:";

    @Nonnull
    public Page<IamUserVo> page(@Nonnull String orgId, @Nullable QueryCondition<IamUser> condition) {
        condition = Objects.requireNonNullElse(condition, new QueryCondition<>());
        Page<IamUser> page = baseMapper.page(
                orgId, condition.page(), condition.searchWrapper(IamUser.class), condition.sortWrapper(IamUser.class));

        Set<String> userIds = page.getRecords().stream().map(IamUser::getUserId).collect(Collectors.toSet());
        Set<String> orgIds = page.getRecords().stream()
                .map(IamUser::getOrgId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        // 查询 org 信息
        Map<String, String> orgId2NameMap = iamOrgService.orgId2NameMap(orgIds);

        // 查询职位信息
        Map<String, List<IamUserPositionVo>> userPositionMap = iamUserPositionService.selectByUserIds(userIds).stream()
                .collect(Collectors.groupingBy(IamUserPositionVo::getUserId));

        // 查询角色信息
        Map<String, List<IamUserRoleVo>> userRoleMap = iamUserRoleService.selectByUserIds(userIds).stream()
                .collect(Collectors.groupingBy(IamUserRoleVo::getUserId));

        List<IamUserVo> recordsVo = page.getRecords().stream()
                .map(sysUser -> {
                    IamUserVo sysUserVo = IamUserConvert.INSTANCE.entity2vo(sysUser);
                    // 填充机构信息
                    sysUserVo.setOrgName(orgId2NameMap.get(sysUser.getOrgId()));

                    // 填充职位信息
                    List<IamUserPositionVo> positionVoList =
                            userPositionMap.getOrDefault(sysUser.getUserId(), new ArrayList<>());
                    sysUserVo.setPositionIds(positionVoList.stream()
                            .map(IamUserPositionVo::getPositionId)
                            .collect(Collectors.toList()));
                    sysUserVo.setPositionNames(positionVoList.stream()
                            .map(IamUserPositionVo::getPositionName)
                            .collect(Collectors.toList()));

                    // 填充角色信息
                    List<IamUserRoleVo> userRoleVos = userRoleMap.getOrDefault(sysUser.getUserId(), new ArrayList<>());
                    sysUserVo.setRoleIds(
                            userRoleVos.stream().map(IamUserRoleVo::getRoleId).collect(Collectors.toList()));
                    sysUserVo.setRoleNames(
                            userRoleVos.stream().map(IamUserRoleVo::getRoleName).collect(Collectors.toList()));

                    return sysUserVo;
                })
                .toList();

        return new Page<IamUserVo>(page.getCurrent(), page.getSize(), page.getTotal()).setRecords(recordsVo);
    }

    public void add(@Nonnull IamUserSaveArgs args) {
        args.setUserPassword(PwdUtil.hashPassword(args.getUserPassword()));

        LockUtil.lock(LOCK_KEY, new LockWrapper<IamUser>().lock(IamUser::getUserName, args.getUserName()), true, () -> {
            OnlyFieldCheck.checkInsert(baseMapper, args);
            baseMapper.insert(args);
            userPositionService.updateRelation(args);
            iamUserRoleService.updateRelation(args);
        });
    }

    public boolean update(@Nonnull IamUserSaveArgs args) {
        return LockUtil.lock(
                LOCK_KEY, new LockWrapper<IamUser>().lock(IamUser::getUserName, args.getUserName()), true, () -> {
                    OnlyFieldCheck.checkUpdate(baseMapper, args);
                    int changeRowCount = baseMapper.updateById(args);
                    userPositionService.updateRelation(args);
                    iamUserRoleService.updateRelation(args);
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
    public Optional<IamUser> detail(@Nonnull String userId) {
        return Optional.ofNullable(baseMapper.selectById(userId));
    }

    @Nonnull
    public IamUserInfo currentUserInfo() {
        String userId = CurrentUtil.getCurrentUserId();
        IamUser iamUser = this.getById(userId);
        if (StringUtils.isBlank(userId) || Objects.isNull(iamUser)) {
            throw new BizException(ResultCodeEnum.UNAUTHORIZED);
        }

        IamUserInfo sysUserInfo = IamUserConvert.INSTANCE.entity2info(iamUser);
        List<IamPermission> permissionList = iamPermissionService.listByUserIds(userId);

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
    public String login(@Nonnull IamUser iamUser) {
        IamUser dbIamUser =
                baseMapper.selectOne(new LambdaQueryWrapper<IamUser>().eq(IamUser::getUserName, iamUser.getUserName()));
        if (dbIamUser == null) {
            throw new BizException("账号或密码错误");
        }
        if (!PwdUtil.verifyPassword(iamUser.getUserPassword(), dbIamUser.getUserPassword())) {
            throw new BizException("账号或密码错误");
        }
        String token = IdWorker.getIdStr();
        RedisUtils.set(
                RedisPrefix.TOKEN_KEY + dbIamUser.getUserId() + ":" + token,
                dbIamUser,
                passifloraProperties.getIam().getToken().getExpire());
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
        keys.forEach(key ->
                RedisUtils.expire(key, passifloraProperties.getIam().getToken().getExpire()));
        return Boolean.TRUE;
    }
}
