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
import com.zerolinck.passiflora.base.constant.RedisPrefix;
import com.zerolinck.passiflora.common.api.ResultCode;
import com.zerolinck.passiflora.common.config.PassifloraProperties;
import com.zerolinck.passiflora.common.exception.BizException;
import com.zerolinck.passiflora.common.util.*;
import com.zerolinck.passiflora.common.util.lock.LockUtil;
import com.zerolinck.passiflora.common.util.lock.LockWrapper;
import com.zerolinck.passiflora.iam.mapper.IamUserMapper;
import com.zerolinck.passiflora.model.iam.args.IamUserArgs;
import com.zerolinck.passiflora.model.iam.entity.IamPermission;
import com.zerolinck.passiflora.model.iam.entity.IamUser;
import com.zerolinck.passiflora.model.iam.enums.PermissionTypeEnum;
import com.zerolinck.passiflora.model.iam.mapperstruct.IamUserConvert;
import com.zerolinck.passiflora.model.iam.resp.IamUserInfo;
import com.zerolinck.passiflora.model.iam.resp.IamUserPositionResp;
import com.zerolinck.passiflora.model.iam.resp.IamUserResp;
import com.zerolinck.passiflora.model.iam.resp.IamUserRoleResp;
import com.zerolinck.passiflora.mybatis.util.ConditionUtils;
import com.zerolinck.passiflora.mybatis.util.OnlyFieldCheck;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

/** @author linck on 2024-02-07 */
@Service
@RequiredArgsConstructor
public class IamUserService {
    private final IamUserMapper mapper;
    private final IamOrgService iamOrgService;
    private final IamUserPositionService userPositionService;
    private final PassifloraProperties passifloraProperties;
    private final IamUserPositionService iamUserPositionService;
    private final IamPermissionService iamPermissionService;
    private final IamUserRoleService iamUserRoleService;

    private static final String LOCK_KEY = "passiflora:lock:iamUser:";

    /**
     * 根据提供的组织ID和查询条件分页查询IAM用户。
     *
     * @param orgId 组织ID
     * @param condition 查询条件
     * @return IAM用户响应的分页结果
     */
    @NotNull public Page<IamUserResp> page(@NotNull String orgId, @Nullable QueryCondition<IamUser> condition) {
        condition = Objects.requireNonNullElse(condition, new QueryCondition<>());
        Page<IamUser> page = mapper.paginate(
                condition.getPageNum(),
                condition.getPageSize(),
                ConditionUtils.searchWrapper(condition, IamUser.class));

        Set<String> userIds = page.getRecords().stream().map(IamUser::getUserId).collect(Collectors.toSet());
        Set<String> orgIds = page.getRecords().stream()
                .map(IamUser::getOrgId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        // 查询 org 信息
        Map<String, String> orgId2NameMap = iamOrgService.orgId2NameMap(orgIds);

        // 查询职位信息
        Map<String, List<IamUserPositionResp>> userPositionMap =
                iamUserPositionService.selectByUserIds(userIds).stream()
                        .collect(Collectors.groupingBy(IamUserPositionResp::getUserId));

        // 查询角色信息
        Map<String, List<IamUserRoleResp>> userRoleMap = iamUserRoleService.selectByUserIds(userIds).stream()
                .collect(Collectors.groupingBy(IamUserRoleResp::getUserId));

        List<IamUserResp> recordsVo = page.getRecords().stream()
                .map(iamUser -> {
                    IamUserResp iamUserResp = IamUserConvert.INSTANCE.entityToResp(iamUser);
                    // 填充机构信息
                    iamUserResp.setOrgName(orgId2NameMap.get(iamUser.getOrgId()));

                    // 填充职位信息
                    List<IamUserPositionResp> positionVoList =
                            userPositionMap.getOrDefault(iamUser.getUserId(), new ArrayList<>());
                    iamUserResp.setPositionIds(positionVoList.stream()
                            .map(IamUserPositionResp::getPositionId)
                            .collect(Collectors.toList()));
                    iamUserResp.setPositionNames(positionVoList.stream()
                            .map(IamUserPositionResp::getPositionName)
                            .collect(Collectors.toList()));

                    // 填充角色信息
                    List<IamUserRoleResp> userRoleVos =
                            userRoleMap.getOrDefault(iamUser.getUserId(), new ArrayList<>());
                    iamUserResp.setRoleIds(
                            userRoleVos.stream().map(IamUserRoleResp::getRoleId).collect(Collectors.toList()));
                    iamUserResp.setRoleNames(userRoleVos.stream()
                            .map(IamUserRoleResp::getRoleName)
                            .collect(Collectors.toList()));

                    return iamUserResp;
                })
                .toList();
        Page<IamUserResp> iamUserRespPage = new Page<>(page.getPageNumber(), page.getPageSize(), page.getTotalRow());
        iamUserRespPage.setRecords(recordsVo);
        return iamUserRespPage;
    }

    /**
     * 添加新的IAM用户。
     *
     * @param args IAM用户参数
     */
    public void add(@NotNull IamUserArgs args) {
        args.setUserPassword(PwdUtil.hashPassword(args.getUserPassword()));

        LockUtil.lock(LOCK_KEY, new LockWrapper<IamUser>().lock(IamUser::getUserName, args.getUserName()), true, () -> {
            IamUser iamUser = IamUserConvert.INSTANCE.argsToEntity(args);
            OnlyFieldCheck.checkInsert(mapper, iamUser);
            mapper.insert(iamUser);
            args.setUserId(iamUser.getUserId());
            userPositionService.updateRelation(args);
            iamUserRoleService.updateRelation(args);
        });
    }

    /**
     * 更新现有的IAM用户。
     *
     * @param args IAM用户参数
     * @return 如果更新成功返回true，否则返回false
     */
    public boolean update(@NotNull IamUserArgs args) {
        return LockUtil.lock(
                LOCK_KEY, new LockWrapper<IamUser>().lock(IamUser::getUserName, args.getUserName()), true, () -> {
                    IamUser iamUser = IamUserConvert.INSTANCE.argsToEntity(args);
                    OnlyFieldCheck.checkUpdate(mapper, iamUser);
                    int changeRowCount = mapper.update(iamUser);
                    userPositionService.updateRelation(args);
                    iamUserRoleService.updateRelation(args);
                    return changeRowCount > 0;
                });
    }

    /**
     * 根据用户ID集合删除IAM用户。
     *
     * @param userIds 用户ID集合
     * @return 删除的用户数量
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteByIds(@Nullable Collection<String> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return 0;
        }
        int count = mapper.deleteBatchByIds(userIds, 500);
        userPositionService.deleteByUserIds(userIds);
        return count;
    }

    /**
     * 根据用户ID获取IAM用户的详细信息。
     *
     * @param userId 用户ID
     * @return 包含IAM用户响应的Optional对象
     */
    @NotNull public Optional<IamUserResp> detail(@NotNull String userId) {
        return Optional.ofNullable(IamUserConvert.INSTANCE.entityToResp(mapper.selectOneById(userId)));
    }

    /**
     * 获取当前用户的信息。
     *
     * @return 当前用户的信息
     */
    @NotNull public IamUserInfo currentUserInfo() {
        String userId = CurrentUtil.getCurrentUserId();
        IamUser iamUser = mapper.selectOneById(userId);
        if (StringUtils.isBlank(userId) || Objects.isNull(iamUser)) {
            throw new BizException(ResultCode.UNAUTHORIZED);
        }

        IamUserInfo iamUserInfo = IamUserConvert.INSTANCE.entityToInfo(iamUser);
        List<IamPermission> permissionList = iamPermissionService.listByUserIds(userId);

        permissionList.forEach(iamPermission -> {
            if (PermissionTypeEnum.MENU.equals(iamPermission.getPermissionType())
                    || PermissionTypeEnum.MENU_SET.equals(iamPermission.getPermissionType())) {
                iamUserInfo.getMenu().add(iamPermission.getPermissionName());
            } else {
                iamUserInfo.getPermission().add(iamPermission.getPermissionName());
            }
        });
        return iamUserInfo;
    }

    /**
     * 登录IAM用户。
     *
     * @param iamUser IAM用户
     * @return 生成的令牌
     */
    @NotNull public String login(@NotNull IamUser iamUser) {
        IamUser dbIamUser = mapper.selectByUsername(iamUser.getUserName());
        if (dbIamUser == null) {
            throw new BizException("账号或密码错误");
        }
        if (!PwdUtil.verifyPassword(iamUser.getUserPassword(), dbIamUser.getUserPassword())) {
            throw new BizException("账号或密码错误");
        }
        String token = StrUtil.randomString(10);
        RedisUtils.set(
                RedisPrefix.TOKEN_KEY + dbIamUser.getUserId() + ":" + token,
                dbIamUser,
                passifloraProperties.getIam().getToken().getExpire());
        return token;
    }

    /** 登出当前用户。 */
    public void logout() {
        Set<String> keys = RedisUtils.keys(RedisPrefix.TOKEN_KEY + "*:" + CurrentUtil.getToken());
        if (CollectionUtils.isEmpty(keys)) {
            return;
        }
        keys.forEach(RedisUtils::del);
    }

    /**
     * 检查当前用户的令牌是否有效。
     *
     * @return 如果令牌有效返回true，否则返回false
     */
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
