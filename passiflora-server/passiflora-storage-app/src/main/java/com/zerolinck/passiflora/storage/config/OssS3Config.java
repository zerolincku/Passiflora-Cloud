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
import com.zerolinck.passiflora.storage.util.OssS3Util;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;

/**
 * @author linck
 * @since 2023-12-15
 */
@Configuration
@RequiredArgsConstructor
public class OssS3Config {
    private final PassifloraProperties passifloraProperties;

    /** 注入 s3 客户端 */
    @Bean
    public S3Client amazonS3() {
        String endpoint = passifloraProperties.getStorage().getOss().getEndpoint();
        String accessKey = passifloraProperties.getStorage().getOss().getAccessKey();
        String secretKey = passifloraProperties.getStorage().getOss().getSecretKey();

        // 创建凭证
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

        // 配置S3，设置路径样式访问
        S3Configuration s3Configuration =
                S3Configuration.builder().pathStyleAccessEnabled(true).build();

        // 构建S3Client
        S3Client s3Client = S3Client.builder()
                .httpClientBuilder(ApacheHttpClient.builder())
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .serviceConfiguration(s3Configuration)
                .region(Region.CN_NORTH_1)
                .endpointOverride(URI.create(endpoint))
                .build();
        OssS3Util.setS3Client(s3Client);
        return s3Client;
    }
}
