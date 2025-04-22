/* 
 * Copyright (C) 2025 Linck. <zerolinck@foxmail.com>
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
package com.zerolinck.passiflora.common.config;

import jakarta.annotation.PostConstruct;

import com.zerolinck.passiflora.common.util.lock.LockUtils;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.support.TransactionTemplate;

import lombok.RequiredArgsConstructor;

/**
 * 锁配置类 配置RedissonClient和TransactionTemplate到LockUtil中
 *
 * @since 2024-05-13
 */
@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "passiflora.config", name = "lock", havingValue = "true")
public class LockConfig {

    private final ApplicationContext applicationContext;

    /**
     * 初始化方法 在容器启动后配置RedissonClient和TransactionTemplate到LockUtil中
     *
     * @since 2024-05-13
     */
    @PostConstruct
    public void init() {
        LockUtils.setRedissonClient(applicationContext.getBean(RedissonClient.class));
        LockUtils.setTransactionTemplate(applicationContext.getBean(TransactionTemplate.class));
    }
}
