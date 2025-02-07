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

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.function.Supplier;

import com.zerolinck.passiflora.common.api.ResultCode;
import com.zerolinck.passiflora.common.exception.BizException;
import com.zerolinck.passiflora.common.util.lock.suppert.SFunction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.transaction.support.TransactionTemplate;

import lombok.Setter;

/**
 * 分布式锁工具类，保证在并发情况下的数据正确
 *
 * @author linck on 2024-03-20
 */
public class LockUtils {

    @Setter
    private static RedissonClient redissonClient;

    @Setter
    private static TransactionTemplate transactionTemplate;

    /** 加锁等待时间 */
    private static final Integer COMMON_LOCK_WAIT_SECONDS = 2;
    /** 最长持有锁时间 */
    private static final Integer COMMON_LOCK_LEASE_SECONDS = 60;

    public static void lock(@NotNull String lockKey, @NotNull Runnable runnable) {
        lock(lockKey, null, false, runnable);
    }

    public static void lock(@NotNull String lockKey, boolean useTransaction, @NotNull Runnable runnable) {
        lock(lockKey, null, useTransaction, runnable);
    }

    public static void lock(@NotNull String lockKey, @Nullable LockWrapper<?> lockWrapper, @NotNull Runnable runnable) {
        lock(lockKey, lockWrapper, false, runnable);
    }

    public static void lock(
            @NotNull String lockKey,
            @Nullable LockWrapper<?> lockWrapper,
            boolean useTransaction,
            @NotNull Runnable runnable) {
        lock(lockKey, lockWrapper, useTransaction, () -> {
            runnable.run();
            return null;
        });
    }

    public static <T> T lock(
            @NotNull String lockKey, @Nullable LockWrapper<?> lockWrapper, @NotNull Supplier<T> supplier) {
        return lock(lockKey, lockWrapper, false, supplier);
    }

    public static <T> T lock(
            @NotNull String lockKey,
            @Nullable LockWrapper<?> lockWrapper,
            boolean useTransaction,
            @NotNull Supplier<T> supplier) {
        RLock[] locks;
        if (lockWrapper == null) {
            locks = new RLock[1];
        } else {
            AtomicInteger lockLength =
                    new AtomicInteger(lockWrapper.getColumns().size());
            lockWrapper.getColumnValueList().forEach((clazz, list) -> lockLength.addAndGet(list.size()));
            locks = new RLock[lockLength.get()];
        }

        try {
            // lockWrapper 为空时，直接锁定 lockKey
            if (lockWrapper == null) {
                locks[0] = redissonClient.getLock(lockKey);
                locks[0].tryLock(COMMON_LOCK_WAIT_SECONDS, COMMON_LOCK_LEASE_SECONDS, TimeUnit.SECONDS);
            } else {
                AtomicInteger i = new AtomicInteger();
                // 锁定单项值锁
                for (; i.get() < locks.length; i.getAndIncrement()) {
                    String fieldName = getFieldName(lockWrapper.getColumns().get(i.get()));
                    locks[i.get()] = redissonClient.getFairLock(lockKey + fieldName
                            + ":"
                            + lockWrapper.getColumnValues().get(i.get()));
                    locks[i.get()].tryLock(COMMON_LOCK_WAIT_SECONDS, COMMON_LOCK_LEASE_SECONDS, TimeUnit.SECONDS);
                }
                // 锁定多项值锁
                lockWrapper.getColumnValueList().forEach((function, columnValueList) -> {
                    for (Object columnValue : columnValueList) {
                        String fieldName = getFieldName(function);
                        locks[i.get()] = redissonClient.getFairLock(lockKey + fieldName + ":" + columnValue);
                        try {
                            locks[i.get()].tryLock(
                                    COMMON_LOCK_WAIT_SECONDS, COMMON_LOCK_LEASE_SECONDS, TimeUnit.SECONDS);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            throw new BizException(e, ResultCode.COMPETE_FAILED);
                        }
                        i.getAndIncrement();
                    }
                });
            }
            return useTransaction ? transactionTemplate.execute(status -> supplier.get()) : supplier.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BizException(e, ResultCode.COMPETE_FAILED);
        } finally {
            unlock(locks);
        }
    }

    public static void unlock(@Nullable Lock... locks) {
        for (Lock lock : locks) {
            if (lock != null) {
                lock.unlock();
            }
        }
    }

    @NotNull private static String getFieldName(@NotNull SFunction<?, ?> column) {
        return column.getFieldName();
    }
}
