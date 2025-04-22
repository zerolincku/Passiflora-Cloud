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
package com.zerolinck.passiflora.common.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * Spring 工具类
 *
 * @author lengleng on 2019/2/1
 */
@Slf4j
@Service
@Lazy(false)
public class SpringContextHolder implements ApplicationContextAware, DisposableBean {

    /** -- GETTER -- 取得存储在静态变量中的ApplicationContext. */
    @Getter
    private static ApplicationContext applicationContext = null;

    /** 实现ApplicationContextAware接口, 注入Context到静态变量中. */
    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) {
        SpringContextHolder.applicationContext = applicationContext;
    }

    /** 从静态变量applicationContext中取得Bean, 自动转型为所赋值对象的类型. */
    @Nullable @SuppressWarnings({"unchecked", "unused"})
    public static <T> T getBean(@NotNull String name) {
        if (applicationContext == null) {
            return null;
        }
        return (T) applicationContext.getBean(name);
    }

    /** 从静态变量applicationContext中取得Bean, 自动转型为所赋值对象的类型. */
    @Nullable public static <T> T getBean(@NotNull Class<T> requiredType) {
        if (applicationContext == null) {
            return null;
        }
        return applicationContext.getBean(requiredType);
    }

    @Nullable public static String getProperty(String key) {
        return null == applicationContext
                ? null
                : applicationContext.getEnvironment().getProperty(key);
    }

    /** 清除SpringContextHolder中的ApplicationContext为Null. */
    public static void clearHolder() {
        log.debug("清除SpringContextHolder中的ApplicationContext:{}", applicationContext);
        applicationContext = null;
    }

    /** 发布事件 */
    @SuppressWarnings("unused")
    public static void publishEvent(@NotNull ApplicationEvent event) {
        if (applicationContext == null) {
            return;
        }
        applicationContext.publishEvent(event);
    }

    /** 实现DisposableBean接口, 在Context关闭时清理静态变量. */
    @Override
    @SneakyThrows
    public void destroy() {
        SpringContextHolder.clearHolder();
    }
}
