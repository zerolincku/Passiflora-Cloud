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
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zerolinck.passiflora.common.config.PassifloraProperties;
import com.zerolinck.passiflora.common.exception.BizException;
import com.zerolinck.passiflora.common.util.CurrentUtil;
import com.zerolinck.passiflora.common.util.OnlyFieldCheck;
import com.zerolinck.passiflora.common.util.QueryCondition;
import com.zerolinck.passiflora.common.util.RedisUtils;
import com.zerolinck.passiflora.common.util.lock.LockUtil;
import com.zerolinck.passiflora.common.util.lock.LockWrapper;
import com.zerolinck.passiflora.model.common.constant.RedisPrefix;
import com.zerolinck.passiflora.model.system.entity.SysUser;
import com.zerolinck.passiflora.model.system.mapperstruct.SysUserConvert;
import com.zerolinck.passiflora.model.system.vo.SysUserVo;
import com.zerolinck.passiflora.system.mapper.SysUserMapper;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
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
    private final PassifloraProperties passifloraProperties;

    private static final String LOCK_KEY = "passiflora:lock:sysUser:";

    public Page<SysUserVo> page(
        String orgId,
        QueryCondition<SysUser> condition
    ) {
        if (condition == null) {
            condition = new QueryCondition<>();
        }
        Page<SysUser> page = baseMapper.page(
            orgId,
            condition.page(),
            condition.searchWrapper(SysUser.class),
            condition.sortWrapper(SysUser.class)
        );

        Map<String, String> orgId2NameMap = sysOrgService.orgId2NameMap(
            page.getRecords().stream().map(SysUser::getOrgId).toList()
        );

        List<SysUserVo> recordsVo = page
            .getRecords()
            .stream()
            .map(sysUser -> {
                SysUserVo sysUserVo = SysUserConvert.INSTANCE.entity2vo(
                    sysUser
                );
                sysUserVo.setOrgName(orgId2NameMap.get(sysUser.getOrgId()));
                return sysUserVo;
            })
            .toList();

        return new Page<SysUserVo>(
            page.getCurrent(),
            page.getSize(),
            page.getTotal()
        )
            .setRecords(recordsVo);
    }

    public void add(SysUser sysUser) {
        sysUser.setSalt(BCrypt.gensalt());
        sysUser.setUserPassword(
            BCrypt.hashpw(sysUser.getUserPassword(), sysUser.getSalt())
        );

        LockUtil.lockAndTransactionalLogic(
            LOCK_KEY,
            new LockWrapper<SysUser>()
                .lock(SysUser::getUserName, sysUser.getUserName()),
            () -> {
                OnlyFieldCheck.checkInsert(baseMapper, sysUser);
                baseMapper.insert(sysUser);
                return null;
            }
        );
    }

    public boolean update(SysUser sysUser) {
        return (boolean) LockUtil.lockAndTransactionalLogic(
            LOCK_KEY,
            new LockWrapper<SysUser>()
                .lock(SysUser::getUserName, sysUser.getUserName()),
            () -> {
                OnlyFieldCheck.checkUpdate(baseMapper, sysUser);
                int changeRowCount = baseMapper.updateById(sysUser);
                return changeRowCount > 0;
            }
        );
    }

    @Transactional(rollbackFor = Exception.class)
    public int deleteByIds(Collection<String> userIds) {
        return baseMapper.deleteByIds(userIds, CurrentUtil.getCurrentUserId());
    }

    public SysUser detail(String userId) {
        SysUser sysUser = baseMapper.selectById(userId);
        if (sysUser == null) {
            throw new BizException("无对应用户数据，请刷新后重试");
        }
        return sysUser;
    }

    public String login(SysUser sysUser) {
        SysUser dbSysUser = baseMapper.selectOne(
            new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUserName, sysUser.getUserName())
        );
        if (dbSysUser == null) {
            throw new BizException("账号或密码错误");
        }
        if (
            !BCrypt.checkpw(
                sysUser.getUserPassword(),
                dbSysUser.getUserPassword()
            )
        ) {
            throw new BizException("账号或密码错误");
        }
        String token = IdWorker.getIdStr();
        RedisUtils.set(
            RedisPrefix.TOKEN_KEY + dbSysUser.getUserId() + ":" + token,
            dbSysUser,
            passifloraProperties.getSystem().getToken().getExpire()
        );
        return token;
    }

    public void logout() {
        Set<String> keys = RedisUtils.keys(
            RedisPrefix.TOKEN_KEY + "*:" + CurrentUtil.getToken()
        );
        if (CollectionUtil.isEmpty(keys)) {
            return;
        }
        keys.forEach(RedisUtils::del);
    }

    public Boolean checkToken() {
        Set<String> keys = RedisUtils.keys(
            RedisPrefix.TOKEN_KEY + "*:" + CurrentUtil.getToken()
        );
        if (CollectionUtil.isEmpty(keys)) {
            return Boolean.FALSE;
        }
        keys.forEach(key ->
            RedisUtils.expire(
                key,
                passifloraProperties.getSystem().getToken().getExpire()
            )
        );
        return Boolean.TRUE;
    }
}
