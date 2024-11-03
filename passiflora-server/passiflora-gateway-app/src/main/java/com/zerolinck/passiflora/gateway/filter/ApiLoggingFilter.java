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
package com.zerolinck.passiflora.gateway.filter;

import com.zerolinck.passiflora.model.common.constant.Header;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 全局拦截器，作用所有的微服务
 *
 * <p>1. 对请求的API调用过滤，记录接口的请求时间，方便日志审计、告警、分析等运维操作
 *
 * <p>
 */
@Slf4j
@Component
public class ApiLoggingFilter implements GlobalFilter, Ordered {

    private static final String START_TIME = "startTime";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 在交换属性中设置请求开始时间
        exchange.getAttributes().put(START_TIME, System.currentTimeMillis());
        // 执行过滤链，并在完成后记录请求日志
        return chain.filter(exchange).then(logRequest(exchange));
    }

    private Mono<Void> logRequest(ServerWebExchange exchange) {
        // 记录请求摘要和详细信息
        return logRequestSummary(exchange).flatMap(clientIpAddress -> logRequestDetails(exchange, clientIpAddress));
    }

    private Mono<String> logRequestSummary(ServerWebExchange exchange) {
        // 记录请求的基本信息，如方法、URI和头
        return Mono.fromSupplier(() -> {
            ServerHttpRequest request = exchange.getRequest();
            if (log.isDebugEnabled()) {
                log.debug("Received request: {}:{} {}", request.getMethod(), request.getURI(), request.getHeaders());
            }
            // 解析客户端IP地址
            return resolveClientIpAddress(request);
        });
    }

    private String resolveClientIpAddress(ServerHttpRequest request) {
        // 从请求头中获取客户端IP地址，如果未配置则使用远程地址
        String clientIpAddress = request.getHeaders().getFirst(Header.X_FORWARDED_FOR.toString());
        if (clientIpAddress == null) {
            if (log.isDebugEnabled()) {
                log.debug("X-Forwarded-For 未配置");
            }
            clientIpAddress = Objects.requireNonNull(request.getRemoteAddress()).getHostString();
        } else {
            // 如果有多个IP地址，只取第一个
            clientIpAddress = clientIpAddress.split(",")[0];
        }
        return clientIpAddress;
    }

    private Mono<Void> logRequestDetails(ServerWebExchange exchange, String clientIpAddress) {
        // 记录请求的详细信息，如客户端IP、URI、状态码和执行时间
        return Mono.fromRunnable(() -> {
            Long startTime = exchange.getAttribute(START_TIME);
            if (startTime != null) {
                Long executionTime = System.currentTimeMillis() - startTime;
                HttpStatusCode statusCode = exchange.getResponse().getStatusCode();
                log.info(
                        "Request from {} to {} returned with status {} in {} ms",
                        clientIpAddress,
                        exchange.getRequest().getURI(),
                        statusCode,
                        executionTime);
            }
        });
    }

    @Override
    public int getOrder() {
        // 返回最低优先级，确保在其他过滤器之后执行
        return Ordered.LOWEST_PRECEDENCE;
    }
}
