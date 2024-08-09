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
package com.zerolinck.passiflora.common.util.lock;

import com.zerolinck.passiflora.common.api.ResultCodeEnum;
import com.zerolinck.passiflora.common.exception.BizException;
import com.zerolinck.passiflora.common.util.lock.suppert.LambdaMeta;
import com.zerolinck.passiflora.common.util.lock.suppert.LambdaUtils;
import com.zerolinck.passiflora.common.util.lock.suppert.SFunction;
import com.zerolinck.passiflora.common.util.lock.suppert.reflect.PropertyNamer;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.function.Supplier;
import lombok.Setter;
import lombok.SneakyThrows;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * 分布式锁工具类，保证在并发情况下的数据正确
 *
 * @author linck
 * @since 2024-03-20
 */
public class LockUtil {

    @Setter
    private static RedissonClient redissonClient;

    @Setter
    private static TransactionTemplate transactionTemplate;

    private static final Map<String, LambdaMeta> LAMBDA_META_CACHE =
        new ConcurrentHashMap<>();

    private static final Map<Class<?>, Map<String, String>> FIELD_NAME_CACHE =
        new ConcurrentHashMap<>();

    private static final Map<Class<?>, Map<String, Method>> METHOD_CACHE =
        new ConcurrentHashMap<>();

    public static <T> T lock(
        String lockKey,
        LockWrapper<?> lockWrapper,
        Supplier<T> supplier
    ) {
        return lock(lockKey, lockWrapper, false, supplier);
    }

    public static <T> T lock(
        String lockKey,
        LockWrapper<?> lockWrapper,
        boolean useTransaction,
        Supplier<T> supplier
    ) {
        AtomicInteger lockLength = new AtomicInteger(
            lockWrapper.getColumns().size()
        );
        lockWrapper
            .getEntityListLock()
            .forEach((clazz, list) -> lockLength.addAndGet(list.size()));
        RLock[] locks = new RLock[lockLength.get()];

        try {
            AtomicInteger i = new AtomicInteger();
            for (
                ;
                i.get() < lockWrapper.getColumns().size();
                i.getAndIncrement()
            ) {
                String fieldName = getFieldName(
                    lockWrapper.getColumns().get(i.get())
                );
                locks[i.get()] =
                redissonClient.getFairLock(
                    lockKey +
                    fieldName +
                    ":" +
                    lockWrapper.getColumnValues().get(i.get())
                );
                locks[i.get()].tryLock(10, 60, TimeUnit.SECONDS);
            }
            lockWrapper
                .getEntityListLock()
                .forEach((function, entityList) -> {
                    Method method = getMethod(function);
                    for (Object entity : entityList) {
                        String fieldName = getFieldName(function);
                        Object value;
                        try {
                            value = method.invoke(entity);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        locks[i.get()] =
                        redissonClient.getFairLock(
                            lockKey + fieldName + ":" + value
                        );
                        try {
                            locks[i.get()].tryLock(10, 60, TimeUnit.SECONDS);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            throw new BizException(
                                e,
                                ResultCodeEnum.COMPETE_FAILED
                            );
                        }
                        i.getAndIncrement();
                    }
                });
            return useTransaction
                ? transactionTemplate.execute(status -> supplier.get())
                : supplier.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BizException(e, ResultCodeEnum.COMPETE_FAILED);
        } finally {
            unlock(locks);
        }
    }

    public static void unlock(Lock... locks) {
        for (Lock lock : locks) {
            if (lock != null) {
                lock.unlock();
            }
        }
    }

    private static String getFieldName(SFunction<?, ?> column) {
        if (!LAMBDA_META_CACHE.containsKey(column.toString())) {
            LAMBDA_META_CACHE.put(
                column.toString(),
                LambdaUtils.extract(column)
            );
        }
        LambdaMeta meta = LAMBDA_META_CACHE.get(column.toString());
        if (!FIELD_NAME_CACHE.containsKey(meta.getInstantiatedClass())) {
            FIELD_NAME_CACHE.put(meta.getInstantiatedClass(), new HashMap<>());
        }
        if (
            !FIELD_NAME_CACHE
                .get(meta.getInstantiatedClass())
                .containsKey(meta.getImplMethodName())
        ) {
            String fieldName = PropertyNamer.methodToProperty(
                meta.getImplMethodName()
            );
            FIELD_NAME_CACHE
                .get(meta.getInstantiatedClass())
                .put(meta.getImplMethodName(), fieldName);
        }
        return FIELD_NAME_CACHE
            .get(meta.getInstantiatedClass())
            .get(meta.getImplMethodName());
    }

    @SneakyThrows
    private static Method getMethod(SFunction<?, ?> column) {
        if (!LAMBDA_META_CACHE.containsKey(column.toString())) {
            LAMBDA_META_CACHE.put(
                column.toString(),
                LambdaUtils.extract(column)
            );
        }
        LambdaMeta meta = LAMBDA_META_CACHE.get(column.toString());
        Class<?> instantiatedClass = meta.getInstantiatedClass();
        String implMethodName = meta.getImplMethodName();
        if (!METHOD_CACHE.containsKey(instantiatedClass)) {
            METHOD_CACHE.put(instantiatedClass, new HashMap<>());
        }
        if (!METHOD_CACHE.get(instantiatedClass).containsKey(implMethodName)) {
            METHOD_CACHE
                .get(instantiatedClass)
                .put(
                    implMethodName,
                    instantiatedClass.getMethod(implMethodName)
                );
        }
        return METHOD_CACHE.get(instantiatedClass).get(implMethodName);
    }
}
