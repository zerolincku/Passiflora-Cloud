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

import com.alibaba.cloud.nacos.NacosServiceInstance;
import com.alibaba.nacos.api.remote.response.Response;
import com.alibaba.nacos.api.remote.response.ServerCheckResponse;
import com.zerolinck.passiflora.gateway.filter.TokenCheckGatewayFilterFactory;
import java.util.List;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.TypeReference;
import org.springframework.cloud.client.ServiceInstance;

/** @author 林常坤 on 2024/10/31 */
public class RuntimeHintsRegistrar implements org.springframework.aot.hint.RuntimeHintsRegistrar {

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        List<TypeReference> references = List.of(
                TypeReference.of(NacosServiceInstance.class),
                TypeReference.of(ServiceInstance.class),
                TypeReference.of(TokenCheckGatewayFilterFactory.Config.class),
                TypeReference.of(ServerCheckResponse.class),
                TypeReference.of(Response.class));
        hints.reflection().registerTypes(references, builder -> builder.withMembers(MemberCategory.values()));
    }
}
