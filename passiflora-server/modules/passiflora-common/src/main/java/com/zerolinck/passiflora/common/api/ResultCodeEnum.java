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
package com.zerolinck.passiflora.common.api;

/**
 * 枚举了一些常用API操作码
 *
 * @author linck
 */
public enum ResultCodeEnum implements IErrorCode {
    SUCCESS(200, "操作成功"),
    FAILED(500, "操作失败"),
    NO_STATUS_VALUE(501, "没有对应的状态"),
    NO_RESULT_VALUE(502, "没有对应的结果"),
    VALIDATE_FAILED(503, "参数检验失败"),
    /** 加锁失败 */
    COMPETE_FAILED(504, "操作失败，请刷新重试"),
    UNAUTHORIZED(401, "暂未登录或token已经过期"),
    FORBIDDEN(403, "没有相关权限"),
    NOT_FOUND(404, "404 NOT FOUND");

    private final int code;
    private final String message;

    ResultCodeEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 通过状态码获取枚举对象
     *
     * @param code 状态码
     * @return 枚举对象
     */
    public static ResultCodeEnum getByCode(int code) {
        for (ResultCodeEnum resultEnum : ResultCodeEnum.values()) {
            if (code == resultEnum.getCode()) {
                return resultEnum;
            }
        }
        return null;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
