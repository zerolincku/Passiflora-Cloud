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

import com.zerolinck.passiflora.common.api.Result;
import com.zerolinck.passiflora.common.api.ResultCodeEnum;
import com.zerolinck.passiflora.common.exception.BizException;
import com.zerolinck.passiflora.model.common.constant.Constants;
import jakarta.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.RequestPath;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * @author linck
 * @since 2024-04-30
 */
@Slf4j
@Component
public class TokenCheckGatewayFilterFactory
        extends AbstractGatewayFilterFactory<TokenCheckGatewayFilterFactory.Config> {

    private static final Set<Pattern> UN_CHECK_PATH = new HashSet<>();

    static {
        UN_CHECK_PATH.add(Pattern.compile("/passiflora/iam-api/iam-user/login"));
        UN_CHECK_PATH.add(Pattern.compile("/passiflora/iam-api/iam-user/logout"));
        UN_CHECK_PATH.add(Pattern.compile("/doc\\.html"));
        UN_CHECK_PATH.add(Pattern.compile(".*/v3/api-docs"));
    }

    public TokenCheckGatewayFilterFactory() {
        super(TokenCheckGatewayFilterFactory.Config.class);
    }

    @Resource
    private WebClient.Builder webBuilder;

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            RequestPath path = exchange.getRequest().getPath();
            for (Pattern pattern : UN_CHECK_PATH) {
                if (pattern.matcher(path.value()).matches()) {
                    return chain.filter(exchange);
                }
            }

            HttpHeaders headers = exchange.getRequest().getHeaders();
            List<String> tokens = headers.get(Constants.Authorization);
            String token = "";
            if (CollectionUtils.isNotEmpty(tokens)) {
                token = tokens.getFirst();
            }
            return webBuilder
                    .baseUrl("http://passiflora-iam-app")
                    .build()
                    .get()
                    .uri("/passiflora/iam-api/iam-user/check-token")
                    .header(Constants.Authorization, token)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Result<Boolean>>() {})
                    .flatMap(result -> {
                        if (ResultCodeEnum.SUCCESS.getCode() != result.getCode() || !result.getData()) {
                            return Mono.error(new BizException(ResultCodeEnum.UNAUTHORIZED));
                        } else {
                            return chain.filter(exchange);
                        }
                    })
                    .onErrorResume(ex -> {
                        if (ex instanceof BizException) {
                            return Mono.error(ex);
                        }
                        return Mono.error(new BizException(ex));
                    });
        });
    }

    /** 配置参数类 */
    @Getter
    @Slf4j
    public static class Config {}
}
