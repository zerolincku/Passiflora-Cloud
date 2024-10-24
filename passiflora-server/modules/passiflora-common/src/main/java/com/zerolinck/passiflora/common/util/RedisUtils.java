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

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;

/**
 * 需要再 Configuration 中注册 redisTemplate 时，将 redisTemplate 注入此工具类
 *
 * @author linck on 2023-12-14
 */
@Slf4j
@UtilityClass
public class RedisUtils {

    private static RedisTemplate<String, Object> redisTemplate;

    public static void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        RedisUtils.redisTemplate = redisTemplate;
    }

    @Nullable public static Set<String> keys(@Nonnull String keys) {
        return redisTemplate.keys(keys);
    }

    @SuppressWarnings("UnusedReturnValue")
    public static Boolean expire(@Nonnull String key, long time) {
        return redisTemplate.expire(key, time, TimeUnit.SECONDS);
    }

    @SuppressWarnings("unchecked")
    public static void del(@Nonnull String... key) {
        if (key.length > 0) {
            if (key.length == 1) {
                redisTemplate.delete(key[0]);
            } else {
                redisTemplate.delete((Collection<String>) CollectionUtils.arrayToList(key));
            }
        }
    }

    @Nullable public static Object get(@Nonnull String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public static void set(@Nonnull String key, @Nonnull Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public static void set(@Nonnull String key, @Nonnull Object value, long time) {
        if (time > 0) {
            redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
        } else {
            set(key, value);
        }
    }
}
