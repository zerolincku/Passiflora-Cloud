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
package com.zerolinck.passiflora.gateway.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerolinck.passiflora.common.api.Result;
import com.zerolinck.passiflora.common.api.ResultCode;
import com.zerolinck.passiflora.common.exception.BizException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.reactive.resource.NoResourceFoundException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.handler.ResponseStatusExceptionHandler;
import reactor.core.publisher.Mono;

/** 网关异常通用处理器，只作用在webflux 环境下, 优先级低于 {@link ResponseStatusExceptionHandler} 执行 */
@Slf4j
@Order(-1)
@RequiredArgsConstructor
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper;

    /**
     * 处理服务器端发生的异常
     *
     * @param exchange 服务器端交换对象
     * @param throwable 抛出的异常
     * @return 处理结果
     */
    @Override
    @SuppressWarnings("all")
    public Mono<Void> handle(ServerWebExchange exchange, Throwable throwable) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        if (response.isCommitted()) {
            return Mono.error(throwable);
        }

        // header set
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        return handleException(request, response, throwable)
                .flatMap(result -> response.writeWith(Mono.fromSupplier(() -> {
                    DataBufferFactory bufferFactory = response.bufferFactory();
                    try {
                        return bufferFactory.wrap(objectMapper.writeValueAsBytes(result));
                    } catch (JsonProcessingException e) {
                        log.error("Error writing response", e);
                        return bufferFactory.wrap(new byte[0]);
                    }
                })));
    }

    /**
     * 处理异常
     *
     * @param request 服务器请求
     * @param response 服务器响应
     * @param throwable 抛出的异常
     * @return 处理结果
     */
    private Mono<Result<String>> handleException(
            ServerHttpRequest request, ServerHttpResponse response, Throwable throwable) {
        if (throwable instanceof ResponseStatusException e) {
            response.setStatusCode(e.getStatusCode());
            logException(request, e);
            if (HttpStatusCode.valueOf(404).equals(e.getStatusCode())) {
                return Mono.just(Result.failed(ResultCode.NOT_FOUND));
            }
            return Mono.just(Result.failed(e.getReason()));
        } else {
            if (throwable instanceof BizException e) {
                logException(request, e);
                return Mono.just(Result.failed(e.getCode(), e.getMessage()));
            } else {
                logException(request, throwable);
                return Mono.just(Result.failed(ResultCode.FAILED));
            }
        }
    }

    /**
     * 记录异常信息
     *
     * @param request 服务器请求
     * @param throwable 抛出的异常
     */
    private void logException(ServerHttpRequest request, Throwable throwable) {
        switch (throwable) {
            case BizException bizException ->
                    log.warn("Business exception: path={}, exception={}", request.getPath(), throwable.getMessage());
            case NoResourceFoundException noResourceFoundException ->
                    log.warn("NoResourceFoundException: path={}, exception={}", request.getPath(), throwable.getMessage());
            case IllegalArgumentException illegalArgumentException ->
                    log.warn("IllegalArgumentException: path={}, exception={}", request.getPath(), throwable.getMessage());
            case null, default -> log.error("Unexpected exception: path={}", request.getPath(), throwable);
        }
    }
}
