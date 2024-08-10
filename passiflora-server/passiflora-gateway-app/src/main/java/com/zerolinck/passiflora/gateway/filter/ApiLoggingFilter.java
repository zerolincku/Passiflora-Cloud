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

    // nginx 需要配置
    private static final String X_FORWARDED_FOR = "X-Forwarded-For";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        exchange.getAttributes().put(START_TIME, System.currentTimeMillis());
        return chain.filter(exchange).then(logRequest(exchange));
    }

    private Mono<Void> logRequest(ServerWebExchange exchange) {
        return logRequestSummary(exchange).flatMap(clientIpAddress -> logRequestDetails(exchange, clientIpAddress));
    }

    private Mono<String> logRequestSummary(ServerWebExchange exchange) {
        return Mono.fromSupplier(() -> {
            ServerHttpRequest request = exchange.getRequest();
            if (log.isDebugEnabled()) {
                log.debug("Received request: {}:{} {}", request.getMethod(), request.getURI(), request.getHeaders());
            }
            return resolveClientIpAddress(request);
        });
    }

    private String resolveClientIpAddress(ServerHttpRequest request) {
        String clientIpAddress = request.getHeaders().getFirst(X_FORWARDED_FOR);
        if (clientIpAddress == null) {
            if (log.isDebugEnabled()) {
                log.debug("X-Forwarded-For 未配置");
            }
            clientIpAddress = Objects.requireNonNull(request.getRemoteAddress()).getHostString();
        } else {
            clientIpAddress = clientIpAddress.split(",")[0];
        }
        return clientIpAddress;
    }

    private Mono<Void> logRequestDetails(ServerWebExchange exchange, String clientIpAddress) {
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
        return Ordered.LOWEST_PRECEDENCE;
    }
}
