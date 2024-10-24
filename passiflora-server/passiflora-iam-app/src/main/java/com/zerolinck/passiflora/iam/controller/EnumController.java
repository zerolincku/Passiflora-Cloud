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
package com.zerolinck.passiflora.iam.controller;

import com.zerolinck.passiflora.common.api.Result;
import com.zerolinck.passiflora.common.util.StrUtil;
import com.zerolinck.passiflora.common.util.lock.ClassUtil;
import com.zerolinck.passiflora.feign.iam.EnumApi;
import jakarta.annotation.PostConstruct;
import java.util.*;
import lombok.SneakyThrows;
import org.springframework.util.ReflectionUtils;
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
        return Result.ok(Objects.requireNonNullElse(MAP.get(enumName), Collections.emptyList()));
    }

    @SneakyThrows
    private void initEnum() {
        Set<Class<?>> classes = ClassUtil.getLabelValueClasses();

        for (Class<?> clazz : classes) {
            Object[] enumConstants = clazz.getEnumConstants();
            // 遍历枚举实例
            for (Object enumInstance : enumConstants) {
                Object name = ReflectionUtils.invokeMethod(
                        Objects.requireNonNull(ReflectionUtils.findMethod(clazz, "getLabel")), enumInstance);
                Object value = ReflectionUtils.invokeMethod(
                        Objects.requireNonNull(ReflectionUtils.findMethod(clazz, "getValue")), enumInstance);
                String enumName = StrUtil.camelToMidline(clazz.getSimpleName());
                if (!MAP.containsKey(enumName)) {
                    MAP.put(enumName, new ArrayList<>());
                }
                assert name != null;
                assert value != null;
                MAP.get(enumName).add(Map.of("label", name.toString(), "value", value));
            }
        }
    }
}
