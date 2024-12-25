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
package com.zerolinck.passiflora.gateway.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cloud.client.loadbalancer.reactive.LoadBalancedExchangeFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/** 网关配置类 该类负责配置网关的全局异常处理器和WebClient构建器 */
@Configuration(proxyBeanMethods = false)
public class GatewayConfiguration {

    /**
     * 创建并注册全局异常处理器
     *
     * @param objectMapper Jackson对象映射器，用于处理JSON数据
     * @return 全局异常处理器实例
     */
    @Bean
    public GlobalExceptionHandler globalExceptionHandler(ObjectMapper objectMapper) {
        return new GlobalExceptionHandler(objectMapper);
    }

    /**
     * 创建并注册WebClient构建器
     *
     * @param balancedExchangeFilterFunction 负载均衡交换过滤器函数
     * @return WebClient构建器实例
     */
    @Bean
    public WebClient.Builder webClientBuilder(LoadBalancedExchangeFilterFunction balancedExchangeFilterFunction) {
        return WebClient.builder().filter(balancedExchangeFilterFunction);
    }
}
