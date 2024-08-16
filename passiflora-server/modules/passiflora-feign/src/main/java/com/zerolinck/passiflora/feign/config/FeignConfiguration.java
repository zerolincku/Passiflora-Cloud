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
package com.zerolinck.passiflora.feign.config;

import com.zerolinck.passiflora.common.util.NetUtil;
import com.zerolinck.passiflora.common.util.SpringContextHolder;
import com.zerolinck.passiflora.model.common.constant.Constants;
import feign.Logger;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;

/**
 * @author linck
 * @since 2023-12-15
 */
@Slf4j
public class FeignConfiguration implements RequestInterceptor {

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.HEADERS;
    }

    @Override
    public void apply(RequestTemplate requestTemplate) {
        HttpServletRequest request = NetUtil.getRequest();
        String authorization = request.getHeader("authorization");
        String traceId = request.getHeader("traceId");
        requestTemplate.header(Constants.Authorization, authorization);
        requestTemplate.header(Constants.traceId, traceId);
        requestTemplate.header("req-from", SpringContextHolder.getProperty("spring.application.name"));
    }
}
