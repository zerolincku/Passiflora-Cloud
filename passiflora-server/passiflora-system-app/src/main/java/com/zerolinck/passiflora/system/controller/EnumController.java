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
package com.zerolinck.passiflora.system.controller;

import com.zerolinck.passiflora.common.api.Result;
import com.zerolinck.passiflora.common.util.lock.ClassUtil;
import com.zerolinck.passiflora.feign.system.EnumApi;
import jakarta.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.*;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** @author linck */
@RestController
@RequestMapping("enum")
public class EnumController implements EnumApi {

    private final Map<String, List<Map<String, Object>>> MAP = new HashMap<>();

    @SneakyThrows
    @PostConstruct
    public void init() {
        initEnum();
    }

    @Override
    public Result<List<Map<String, Object>>> get(String enumName) {
        return Result.ok(MAP.get(enumName));
    }

    @SneakyThrows
    private void initEnum() {
        Set<Class<?>> classes = ClassUtil.getLabelValueClasses();

        for (Class<?> clazz : classes) {
            Object[] enumConstants = clazz.getEnumConstants();
            // 遍历枚举实例
            for (Object enumInstance : enumConstants) {
                Method getNameMethod = clazz.getMethod("getLabel");
                Object name = getNameMethod.invoke(enumInstance);
                Method getValueMethod = clazz.getMethod("getValue");
                Object value = getValueMethod.invoke(enumInstance);
                if (!MAP.containsKey(clazz.getSimpleName())) {
                    MAP.put(clazz.getSimpleName(), new ArrayList<>());
                }
                MAP.get(clazz.getSimpleName()).add(Map.of("label", name.toString(), "value", value));
            }
        }
    }
}
