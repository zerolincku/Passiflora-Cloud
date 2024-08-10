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
package com.zerolinck.passiflora.common.util.lock.suppert;

import java.lang.invoke.SerializedLambda;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ReflectLambdaMeta implements LambdaMeta {

    private final SerializedLambda lambda;

    private final ClassLoader classLoader;

    public ReflectLambdaMeta(SerializedLambda lambda, ClassLoader classLoader) {
        this.lambda = lambda;
        this.classLoader = classLoader;
    }

    @Override
    public String getImplMethodName() {
        return lambda.getImplMethodName();
    }

    @Override
    public Class<?> getInstantiatedClass() {
        String instantiatedMethodType = lambda.getInstantiatedMethodType();
        String instantiatedType = instantiatedMethodType
                .substring(2, instantiatedMethodType.indexOf(StringPool.SEMICOLON))
                .replace(StringPool.SLASH, StringPool.DOT);
        return ClassUtils.toClassConfident(instantiatedType, this.classLoader);
    }
}
