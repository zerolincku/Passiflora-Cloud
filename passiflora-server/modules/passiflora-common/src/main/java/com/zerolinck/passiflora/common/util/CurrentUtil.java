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
package com.zerolinck.passiflora.common.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerolinck.passiflora.common.api.ResultCodeEnum;
import com.zerolinck.passiflora.common.exception.BizException;
import com.zerolinck.passiflora.model.common.constant.Constants;
import com.zerolinck.passiflora.model.common.constant.RedisPrefix;
import com.zerolinck.passiflora.model.iam.entity.SysUser;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.util.Set;

/**
 * @author linck
 * @since 2023-12-15
 */
public class CurrentUtil {

    /** 当前登录账户，线程级别缓存 */
    private static final ThreadLocal<SysUser> userMap = new ThreadLocal<>();

    /** 当前token */
    private static final ThreadLocal<String> tokenMap = new ThreadLocal<>();

    /** 获取当前登录用户 */
    @Nullable public static SysUser getCurrentUser() {
        if (userMap.get() != null) {
            return userMap.get();
        }
        String token = NetUtil.getRequest().getHeader(Constants.Authorization);
        Set<String> keys = RedisUtils.keys(RedisPrefix.TOKEN_KEY + "*:" + token);
        if (keys == null || keys.isEmpty()) {
            return null;
        }
        Object o = RedisUtils.get(keys.stream().findFirst().get());
        if (o == null) {
            return null;
        }
        ObjectMapper objectMapper = SpringContextHolder.getBean(ObjectMapper.class);
        SysUser sysUser = objectMapper.convertValue(o, SysUser.class);
        userMap.set(sysUser);
        return sysUser;
    }

    @Nullable public static String getCurrentUserId() {
        SysUser currentUser = getCurrentUser();
        if (currentUser == null) {
            return null;
        }
        return currentUser.getUserId();
    }

    @Nonnull
    public static String requireCurrentUserId() {
        SysUser currentUser = getCurrentUser();
        if (currentUser == null) {
            throw new BizException(ResultCodeEnum.UNAUTHORIZED);
        }
        return currentUser.getUserId();
    }

    /** 获取当前 token */
    @Nullable public static String getToken() {
        if (tokenMap.get() != null) {
            return tokenMap.get();
        }
        String token = NetUtil.getRequest().getHeader(Constants.Authorization);
        tokenMap.set(token);
        return token;
    }

    public static void clear() {
        userMap.remove();
        tokenMap.remove();
    }
}
