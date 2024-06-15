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
package com.zerolinck.passiflora.common.util.lock.suppert.reflect;

import org.springframework.core.GenericTypeResolver;

/**
 * Spring 反射辅助类
 *
 * @author noear
 * @author hubin
 * @since 2021-09-03
 */
public class SpringReflectionHelper {

    public static Class<?>[] resolveTypeArguments(
        Class<?> clazz,
        Class<?> genericIfc
    ) {
        return GenericTypeResolver.resolveTypeArguments(clazz, genericIfc);
    }
}
