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

import jakarta.servlet.http.HttpServletRequest;

import com.zerolinck.passiflora.base.constant.Header;
import com.zerolinck.passiflora.common.util.NetUtil;
import com.zerolinck.passiflora.common.util.SpringContextHolder;
import org.springframework.context.annotation.Bean;

import lombok.extern.slf4j.Slf4j;

import feign.Logger;
import feign.RequestInterceptor;
import feign.RequestTemplate;

/** @author linck on 2023-12-15 */
@Slf4j
public class FeignConfiguration implements RequestInterceptor {

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.HEADERS;
    }

    @Override
    public void apply(RequestTemplate requestTemplate) {
        HttpServletRequest request = NetUtil.getRequest();
        // FIXME 如果请求发起启点不是浏览器，比如定时任务
        if (request != null) {
            String authorization = request.getHeader(Header.AUTHORIZATION.toString());
            String traceId = request.getHeader(Header.TRACE_ID.toString());
            requestTemplate.header(Header.AUTHORIZATION.toString(), authorization);
            requestTemplate.header(Header.TRACE_ID.toString(), traceId);
        }
        requestTemplate.header(Header.REQ_FROM.toString(), SpringContextHolder.getProperty("spring.application.name"));
    }
}
