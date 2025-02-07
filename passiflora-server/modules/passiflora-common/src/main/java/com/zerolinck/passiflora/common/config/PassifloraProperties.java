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

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * PassifloraProperties类 用于映射passiflora前缀的配置属性
 *
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

    @NestedConfigurationProperty
    private IamConfig iam;

    @NestedConfigurationProperty
    private Config config;

    @NestedConfigurationProperty
    private Storage storage;

    /** IamConfig类 ��于映射IAM相关的配置属性 */
    @Data
    @NoArgsConstructor
    public static class IamConfig {

        private Token token;

        /** Token类 用于映射token相关的配置属性 */
        @Data
        @NoArgsConstructor
        public static class Token {

            /** token 过期时间，单位：s */
            private Integer expire;
        }
    }

    /** Config类 用于映射通用配置属性 */
    @Data
    @NoArgsConstructor
    public static class Config {
        /** 是否开启全局异常捕获 {@link com.zerolinck.passiflora.common.handler.GlobalExceptionHandler} */
        private Boolean exception;
        /** 是否开启全局默认过滤器 {@link com.zerolinck.passiflora.common.config.GlobalFilter} */
        private Boolean globalFilter;
        /** 是否开启 LockUtils 初始化 {@link com.zerolinck.passiflora.common.config.LockConfig} */
        private Boolean lock;
        /** 是否开启 Cache 初始化 {@link com.zerolinck.passiflora.common.config.CacheConfig} */
        private Boolean cache;
    }

    /** Storage类 用于映射存储相关的配置属性 */
    @Data
    @NoArgsConstructor
    public static class Storage {

        /** 对象存储桶名称 */
        private String bucketName;

        private Oss oss;

        /** Oss类 用于映射OSS相关的配置属性 */
        @Data
        @NoArgsConstructor
        public static class Oss {

            /** oss 地址 */
            private String endpoint;
            /** oss accessKey */
            private String accessKey;
            /** oss secretKey */
            private String secretKey;
        }
    }
}
