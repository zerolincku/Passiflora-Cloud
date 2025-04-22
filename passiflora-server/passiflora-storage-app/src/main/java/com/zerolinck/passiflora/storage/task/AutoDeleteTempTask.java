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
package com.zerolinck.passiflora.storage.task;

import java.util.Set;

import com.zerolinck.passiflora.common.util.lock.LockUtils;
import com.zerolinck.passiflora.storage.service.StorageFileService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 自动删除超过 24h 的临时文件
 *
 * @author 林常坤 on 2024/10/20
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AutoDeleteTempTask {
    private final StorageFileService storageFileService;

    private static final String LOCK_KEY = "passiflora:lock:autoDeleteTempTask";

    @Scheduled(cron = "0 0 0 * * *")
    public void deleteTempTask() {
        LockUtils.lock(LOCK_KEY, () -> {
            Set<String> expiredTempFileIds = storageFileService.expiredTempFileIds();
            storageFileService.deleteByIds(expiredTempFileIds);
            log.info("自定删除临时文件，删除数量: {}", expiredTempFileIds.size());
        });
    }
}
