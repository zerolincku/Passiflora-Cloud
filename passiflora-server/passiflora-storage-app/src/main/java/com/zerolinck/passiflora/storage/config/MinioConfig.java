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
package com.zerolinck.passiflora.storage.config;

import com.zerolinck.passiflora.common.config.PassifloraProperties;
import io.minio.MinioClient;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author linck
 * @since 2023-12-15
 */
@Configuration
public class MinioConfig {

    @Resource
    private PassifloraProperties passifloraProperties;

    /** 注入minio 客户端 */
    @Bean
    public MinioClient minioClient() {
        String endpoint = passifloraProperties.getStorage().getMinio().getEndpoint();
        String accessKey = passifloraProperties.getStorage().getMinio().getAccessKey();
        String secretKey = passifloraProperties.getStorage().getMinio().getSecretKey();
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }
}
