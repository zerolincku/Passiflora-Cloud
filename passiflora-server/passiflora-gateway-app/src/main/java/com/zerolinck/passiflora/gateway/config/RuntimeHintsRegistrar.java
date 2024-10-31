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
package com.zerolinck.passiflora.gateway.config;

import com.alibaba.nacos.api.remote.response.ServerCheckResponse;
import java.util.List;
import org.springframework.aot.hint.ExecutableMode;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.TypeReference;

/** @author 林常坤 on 2024/10/31 */
public class RuntimeHintsRegistrar implements org.springframework.aot.hint.RuntimeHintsRegistrar {

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        hints.reflection()
                .registerType(
                        ServerCheckResponse.class,
                        hint -> hint.withMethod(
                                "setSupportAbilityNegotiation",
                                List.of(TypeReference.of(Boolean.class)),
                                ExecutableMode.INVOKE));
    }
}
