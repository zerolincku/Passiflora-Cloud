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

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author linck
 * @since 2024-05-27
 */
@Data
@ConfigurationProperties(prefix = "passiflora")
public class PassifloraProperties {

    /** 项目版本 */
    private String projectVersion;

    /** 编译时间 */
    private String buildTime;

    private System system;

    private Config config;

    private Storage storage;

    @Data
    @NoArgsConstructor
    public static class System {

        private Token token;

        @Data
        @NoArgsConstructor
        public static class Token {

            /** token 过期时间，单位：s */
            private Integer expire;
        }
    }

    @Data
    @NoArgsConstructor
    public static class Config {

        /** 是否开启 MybatisConfig 默认配置 {@link com.zerolinck.passiflora.common.config.mybaits.MybatisConfig} */
        private Boolean mybatis;
        /** 是否开启全局异常捕获 {@link com.zerolinck.passiflora.common.handler.GlobalExceptionHandler} */
        private Boolean exception;
        /** 是否开启全局默认过滤器 {@link com.zerolinck.passiflora.common.config.GlobalFilter} */
        private Boolean globalFilter;
        /** 是否开启 LockUtil 初始化 {@link com.zerolinck.passiflora.common.config.LockConfig} */
        private Boolean lock;
        /** 是否开启 Cache 初始化 {@link com.zerolinck.passiflora.common.config.CacheConfig} */
        private Boolean cache;
    }

    @Data
    @NoArgsConstructor
    public static class Storage {

        /** 对象存储桶名称 */
        private String bucketName;

        private Minio minio;

        @Data
        @NoArgsConstructor
        public static class Minio {

            /** minio 地址 */
            private String endpoint;
            /** minio accessKey */
            private String accessKey;
            /** minio secretKey */
            private String secretKey;
        }
    }
}
