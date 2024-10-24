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
package com.zerolinck.passiflora.common.util.lock;

import com.google.common.reflect.ClassPath;
import com.zerolinck.passiflora.model.common.LabelValueInterface;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/** @author 林常坤 on 2024-08-14 */
public class ClassUtil {

    /**
     * 获取所有 LabelValue 接口实现的枚举类
     *
     * @since 2024-08-14
     */
    public static Set<Class<?>> getLabelValueClasses() {
        ClassPath classPath;
        try {
            classPath = ClassPath.from(Thread.currentThread().getContextClassLoader());
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }

        Collection<ClassPath.ClassInfo> allClasses = classPath.getTopLevelClassesRecursive("com.zerolinck");
        Set<Class<?>> result = new HashSet<>();
        allClasses.forEach(classInfo -> {
            Class<?> loadClass = classInfo.load();
            if (loadClass.isEnum() && LabelValueInterface.class.isAssignableFrom(loadClass)) {
                result.add(loadClass);
            }
        });
        return result;
    }
}
