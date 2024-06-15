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

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

/**
 * 通用返回对象
 *
 * @author linck
 */
@Data
public class Result<T> {

    private long code;
    private String message;
    private T data;

    protected Result() {}

    protected Result(long code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /** 成功返回结果 */
    public static Result<String> ok() {
        return new Result<>(
            ResultCodeEnum.SUCCESS.getCode(),
            ResultCodeEnum.SUCCESS.getMessage(),
            ""
        );
    }

    /**
     * 成功返回结果
     *
     * @param data 获取的数据
     */
    public static <T> Result<T> ok(T data) {
        return new Result<>(
            ResultCodeEnum.SUCCESS.getCode(),
            ResultCodeEnum.SUCCESS.getMessage(),
            data
        );
    }

    public static <T> Result<ListWithPage<T>> page(Page<T> data) {
        return new Result<>(
            ResultCodeEnum.SUCCESS.getCode(),
            ResultCodeEnum.SUCCESS.getMessage(),
            new ListWithPage<>(data.getRecords(), data.getTotal())
        );
    }

    /**
     * 成功返回结果
     *
     * @param data 获取的数据
     * @param message 提示信息
     */
    public static <T> Result<T> ok(T data, String message) {
        return new Result<>(ResultCodeEnum.SUCCESS.getCode(), message, data);
    }

    /**
     * 失败返回结果
     *
     * @param errorCode 错误码
     */
    public static <T> Result<T> failed(IErrorCode errorCode) {
        return new Result<>(errorCode.getCode(), errorCode.getMessage(), null);
    }

    public static <T> Result<T> failed(Integer code, String message) {
        return new Result<>(code, message, null);
    }

    /**
     * 失败返回结果
     *
     * @param message 提示信息
     */
    public static <T> Result<T> failed(String message) {
        return new Result<>(ResultCodeEnum.FAILED.getCode(), message, null);
    }

    /**
     * 参数验证失败返回结果
     *
     * @param message 提示信息
     */
    public static <T> Result<T> validateFailed(String message) {
        return new Result<>(
            ResultCodeEnum.VALIDATE_FAILED.getCode(),
            message,
            null
        );
    }

    /** 未登录返回结果 */
    public static <T> Result<T> unauthorized() {
        return failed(ResultCodeEnum.UNAUTHORIZED);
    }

    /** 未授权返回结果 */
    public static <T> Result<T> forbidden() {
        return failed(ResultCodeEnum.FORBIDDEN);
    }
}
