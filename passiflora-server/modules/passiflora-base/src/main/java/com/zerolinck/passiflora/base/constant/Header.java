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
package com.zerolinck.passiflora.base.constant;

import lombok.Getter;

/** @author 林常坤 on 2024/08/16 */
@Getter
public enum Header {
    AUTHORIZATION("Authorization"),
    MIME_VERSION("MIME-Version"),
    UPGRADE("Upgrade"),
    CACHE_CONTROL("Cache-Control"),
    CONTENT_TYPE("Content-Type"),
    HOST("Host"),
    REFERER("Referer"),
    ORIGIN("Origin"),
    USER_AGENT("User-Agent"),
    ACCEPT("Accept"),
    ACCEPT_LANGUAGE("Accept-Language"),
    ACCEPT_ENCODING("Accept-Encoding"),
    ACCEPT_CHARSET("Accept-Charset"),
    COOKIE("Cookie"),
    CONTENT_LENGTH("Content-Length"),
    CONTENT_DISPOSITION("Content-Disposition"),
    X_FORWARDED_FOR("X-Forwarded-For"),
    TRACE_ID("Trace-Id"),
    /** 请求来源，内部区分请求来自于哪个服务 */
    REQ_FROM("Req-From"),
    ;

    private final String value;

    Header(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.getValue();
    }
}
