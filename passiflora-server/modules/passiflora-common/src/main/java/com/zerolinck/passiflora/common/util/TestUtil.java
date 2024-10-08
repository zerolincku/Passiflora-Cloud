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
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * TestContainers 容器复用，避免频繁创建测试容器
 *
 * @author 林常坤
 * @since 2024-10-07
 */
public class TestUtil {

    private static PostgreSQLContainer<?> postgres;
    private static final ReentrantLock lock = new ReentrantLock();

    public static PostgreSQLContainer<?> getPostgres() {
        if (postgres == null) {
            try {
                lock.lock();
                if (postgres == null) {
                    postgres = new PostgreSQLContainer<>("postgres:13.16-bookworm").withReuse(true);
                }
            } finally {
                lock.unlock();
            }
        }
        return postgres;
    }
}
