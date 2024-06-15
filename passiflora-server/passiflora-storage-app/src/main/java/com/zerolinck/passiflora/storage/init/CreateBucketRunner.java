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
package com.zerolinck.passiflora.storage.init;

import com.zerolinck.passiflora.common.config.PassifloraProperties;
import com.zerolinck.passiflora.common.util.lock.LockUtil;
import com.zerolinck.passiflora.common.util.lock.LockWrapper;
import com.zerolinck.passiflora.model.storage.entity.StorageFile;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 检查 bucket 是否存在，不存在，则创建
 *
 * @author linck
 * @since 2024-05-27
 */
@Slf4j
@Component
public class CreateBucketRunner implements ApplicationRunner {

    @Resource
    private MinioClient minioClient;

    @Resource
    private PassifloraProperties passifloraProperties;

    @Override
    public void run(ApplicationArguments args) {
        String bucketName = passifloraProperties.getStorage().getBucketName();
        LockUtil.lockAndTransactionalLogic(
            "passiflora:storage:bucket:",
            new LockWrapper<StorageFile>(),
            () -> {
                try {
                    boolean bucketExists = minioClient.bucketExists(
                        BucketExistsArgs.builder().bucket(bucketName).build()
                    );
                    if (log.isDebugEnabled()) {
                        log.debug(
                            "bucket: {}, 是否存在: {}",
                            bucketName,
                            bucketExists
                        );
                    }
                    if (!bucketExists) {
                        minioClient.makeBucket(
                            MakeBucketArgs.builder().bucket(bucketName).build()
                        );
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                return null;
            }
        );
    }
}
