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
package com.zerolinck.passiflora.common.config;

import java.io.IOException;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;

import com.zerolinck.passiflora.common.util.CurrentUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;

import lombok.extern.slf4j.Slf4j;

/**
 * 全局过滤器 用于在每个请求前清除当前线程的上下文信息
 *
 * @since 2024-04-24
 */
@Slf4j
@WebFilter("/*")
@Order(Integer.MIN_VALUE)
@ConditionalOnProperty(prefix = "passiflora.config", name = "globalFilter", havingValue = "true")
public class GlobalFilter implements Filter {

    /**
     * 过滤方法 在每个请求前清除当前线程的上下文信息
     *
     * @param servletRequest Servlet请求对象
     * @param servletResponse Servlet响应对象
     * @param filterChain 过滤器链
     * @throws IOException 如果发生IO错误
     * @throws ServletException 如果发生Servlet错误
     * @since 2024-04-24
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        CurrentUtils.clear();
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
