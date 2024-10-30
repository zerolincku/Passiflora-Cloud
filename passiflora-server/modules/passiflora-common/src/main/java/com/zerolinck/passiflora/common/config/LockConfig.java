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
package com.zerolinck.passiflora.common.config;

import com.zerolinck.passiflora.common.util.lock.LockUtil;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.support.TransactionTemplate;

/** @author linck on 2024-05-13 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "passiflora.config", name = "lock", havingValue = "true")
public class LockConfig {

    @Resource
    private ApplicationContext applicationContext;

    @PostConstruct
    public void init() {
        LockUtil.setRedissonClient(applicationContext.getBean(RedissonClient.class));
        LockUtil.setTransactionTemplate(applicationContext.getBean(TransactionTemplate.class));
    }
}
