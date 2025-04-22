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
package com.zerolinck.passiflora.common.exception;

import com.google.common.base.Strings;
import com.zerolinck.passiflora.common.api.ResultCode;

import lombok.Getter;

/**
 * 自定义运行时异常
 *
 * @author linck
 */
@Getter
public class BizException extends RuntimeException {

    /** 状态码 */
    private final Integer code;

    public BizException(BizException e, ResultCode resultCodeEnum) {
        super(e.getMessage());
        this.code = resultCodeEnum.getCode();
    }

    public BizException(BizException e) {
        super(e.getMessage());
        this.code = e.getCode();
    }

    public BizException(Throwable throwable, ResultCode resultCodeEnum) {
        super(throwable);
        this.code = resultCodeEnum.getCode();
    }

    public BizException(Throwable throwable) {
        super(throwable);
        this.code = ResultCode.FAILED.getCode();
    }

    public BizException(String message) {
        super(message);
        this.code = ResultCode.FAILED.getCode();
    }

    public BizException(String message, Object... params) {
        super(Strings.lenientFormat(message, params));
        this.code = ResultCode.FAILED.getCode();
    }

    /**
     * 自定义异常
     *
     * @param resultCodeEnum 返回枚举对象
     */
    public BizException(ResultCode resultCodeEnum) {
        super(resultCodeEnum.getMessage());
        this.code = resultCodeEnum.getCode();
    }

    /**
     * @param resultCodeEnum 返回枚举对象
     * @param message 错误信息
     */
    public BizException(ResultCode resultCodeEnum, String message) {
        super(message);
        this.code = resultCodeEnum.getCode();
    }

    public BizException(ResultCode resultCodeEnum, String message, Object... params) {
        super(Strings.lenientFormat(message, params));
        this.code = resultCodeEnum.getCode();
    }
}
