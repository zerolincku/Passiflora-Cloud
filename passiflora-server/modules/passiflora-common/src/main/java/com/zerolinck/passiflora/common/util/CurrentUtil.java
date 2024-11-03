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

import com.zerolinck.passiflora.common.api.ResultCode;
import com.zerolinck.passiflora.common.exception.BizException;
import com.zerolinck.passiflora.model.common.constant.Header;
import com.zerolinck.passiflora.model.common.constant.RedisPrefix;
import com.zerolinck.passiflora.model.iam.entity.IamUser;
import java.util.Map;
import java.util.Set;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** @author linck on 2023-12-15 */
@UtilityClass
public class CurrentUtil {

    /** 当前登录账户，线程级别缓存 */
    private static final ThreadLocal<IamUser> userMap = new ThreadLocal<>();

    /** 当前token */
    private static final ThreadLocal<String> tokenMap = new ThreadLocal<>();

    /** 获取当前登录用户 */
    @Nullable public static IamUser getCurrentUser() {
        if (userMap.get() != null) {
            return userMap.get();
        }
        String token = NetUtil.getRequest().getHeader(Header.AUTHORIZATION.toString());
        Set<String> keys = RedisUtils.keys(RedisPrefix.TOKEN_KEY + "*:" + token);
        if (keys == null || keys.isEmpty()) {
            return null;
        }
        Object o = RedisUtils.get(keys.stream().findFirst().get());
        if (o == null) {
            return null;
        }
        @SuppressWarnings("unchecked")
        IamUser iamUser = JsonUtil.convertValue((Map<String, Object>) o, IamUser.class);
        userMap.set(iamUser);
        return iamUser;
    }

    @Nullable public static String getCurrentUserId() {
        IamUser currentUser = getCurrentUser();
        if (currentUser == null) {
            return null;
        }
        return currentUser.getUserId();
    }

    @NotNull public static String requireCurrentUserId() {
        IamUser currentUser = getCurrentUser();
        if (currentUser == null) {
            throw new BizException(ResultCode.UNAUTHORIZED);
        }
        return currentUser.getUserId();
    }

    /** 获取当前 token */
    @Nullable public static String getToken() {
        if (tokenMap.get() != null) {
            return tokenMap.get();
        }
        String token = NetUtil.getRequest().getHeader(Header.AUTHORIZATION.toString());
        tokenMap.set(token);
        return token;
    }

    public static void clear() {
        userMap.remove();
        tokenMap.remove();
    }
}
