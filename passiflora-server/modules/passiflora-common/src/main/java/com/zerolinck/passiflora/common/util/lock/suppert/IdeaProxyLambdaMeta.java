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

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/** 在 IDEA 的 Evaluate 中执行的 Lambda 表达式元数据需要使用该类处理元数据 */
public class IdeaProxyLambdaMeta implements LambdaMeta {

    private final Class<?> clazz;
    private final String name;

    public IdeaProxyLambdaMeta(Proxy func) {
        InvocationHandler handler = Proxy.getInvocationHandler(func);
        try {
            Field field = handler.getClass().getDeclaredField("val$target");
            field.setAccessible(true);
            MethodHandle dmh = (MethodHandle) field.get(handler);
            Executable executable = MethodHandles.reflectAs(Executable.class, dmh);
            clazz = executable.getDeclaringClass();
            name = executable.getName();
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getImplMethodName() {
        return name;
    }

    @Override
    public Class<?> getInstantiatedClass() {
        return clazz;
    }

    @Override
    public String toString() {
        return clazz.getSimpleName() + "::" + name;
    }
}
