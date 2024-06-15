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
package com.zerolinck.passiflora.common.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.net.Inet4Address;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicReference;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @author linck
 * @since 2023-12-11
 */
public class NetUtil {

    /** 查询网卡 ip 地址 */
    public static String findOutIp() throws SocketException {
        Enumeration<NetworkInterface> interfaces =
            NetworkInterface.getNetworkInterfaces();
        AtomicReference<String> ip = new AtomicReference<>(null);
        for (NetworkInterface face : Collections.list(interfaces)) {
            // 过滤回环接口等
            if (face.isLoopback() || !face.isUp()) continue;

            face
                .getInterfaceAddresses()
                .stream()
                .map(InterfaceAddress::getAddress)
                .filter(a -> a instanceof Inet4Address && !a.isLoopbackAddress()
                )
                .findFirst()
                .ifPresent(a -> ip.set(a.getHostAddress()));
        }
        return ip.get();
    }

    public static HttpServletRequest getRequest() {
        ServletRequestAttributes servletRequestAttributes =
            (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert servletRequestAttributes != null;
        return servletRequestAttributes.getRequest();
    }

    public static HttpServletResponse getResponse() {
        ServletRequestAttributes servletRequestAttributes =
            (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert servletRequestAttributes != null;
        return servletRequestAttributes.getResponse();
    }
}
