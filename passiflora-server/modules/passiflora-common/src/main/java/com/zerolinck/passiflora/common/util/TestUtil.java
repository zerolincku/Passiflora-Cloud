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

import java.util.concurrent.locks.ReentrantLock;

import org.jetbrains.annotations.TestOnly;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import com.redis.testcontainers.RedisContainer;

import lombok.experimental.UtilityClass;

/**
 * TestContainers 容器复用，避免频繁创建测试容器
 *
 * @author 林常坤 on 2024-10-07
 */
@TestOnly
@UtilityClass
public class TestUtil {

    private static PostgreSQLContainer<?> postgres;
    private static MinIOContainer minio;
    private static RedisContainer redis;
    private static final ReentrantLock lock = new ReentrantLock();

    /**
     * nacos 不能自动创建 namespace，需要手动登录创建，用户环境隔离，避免测试环境与其他环境产生干扰
     *
     * @author 林常坤 on 2024/10/18
     */
    public static void nacosTestNameSpace(DynamicPropertyRegistry registry) {
        registry.add("spring.cloud.nacos.discovery.namespace", () -> "test");
    }

    public static void postgresContainerStart(DynamicPropertyRegistry registry) {
        if (postgres == null) {
            try {
                lock.lock();
                if (postgres == null) {
                    postgres = new PostgreSQLContainer<>("postgres:17.0-alpine").withReuse(true);
                }
            } finally {
                lock.unlock();
            }
        }
        postgres.start();
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    public static void redisContainerStart(DynamicPropertyRegistry registry) {
        if (redis == null) {
            try {
                lock.lock();
                if (redis == null) {
                    redis = new RedisContainer("redis:7.4.1-alpine").withReuse(true);
                }
            } finally {
                lock.unlock();
            }
        }
        redis.start();
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", redis::getRedisPort);
    }

    @SuppressWarnings("unused")
    public static void minioContainerStart(DynamicPropertyRegistry registry) {
        if (minio == null) {
            try {
                lock.lock();
                if (minio == null) {
                    minio = new MinIOContainer("minio/minio:RELEASE.2024-10-13T13-34-11Z").withReuse(true);
                }
            } finally {
                lock.unlock();
            }
        }
        minio.start();
        registry.add("passiflora.storage.oss.endpoint", minio::getS3URL);
        registry.add("passiflora.storage.oss.access-key", minio::getUserName);
        registry.add("passiflora.storage.oss.secret-key", minio::getPassword);
    }
}
