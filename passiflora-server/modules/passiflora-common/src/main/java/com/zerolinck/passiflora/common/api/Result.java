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
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.Data;

/**
 * 通用返回对象
 *
 * @author linck
 */
@Data
public class Result<T> {

    private int code;
    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;
    /** 总条数 */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long total;

    protected Result() {}

    protected Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    protected Result(int code, String message, T data, Long total) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.total = total;
    }

    /** 成功返回结果 */
    public static Result<Void> ok() {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), null);
    }

    /**
     * 成功返回结果
     *
     * @param data 获取的数据
     */
    public static <T> Result<T> ok(T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data);
    }

    public static <T> Result<List<T>> ok(Page<T> data) {
        return new Result<>(
                ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data.getRecords(), data.getTotal());
    }

    /**
     * 成功返回结果
     *
     * @param data 获取的数据
     * @param message 提示信息
     */
    public static <T> Result<T> ok(T data, String message) {
        return new Result<>(ResultCode.SUCCESS.getCode(), message, data);
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

    /** 参数异常 */
    public static <T> Result<T> illegalArgs(String message) {
        return new Result<>(ResultCode.ILLEGAL_ARGUMENT.getCode(), message, null);
    }

    /**
     * 失败返回结果
     *
     * @param message 提示信息
     */
    public static <T> Result<T> failed(String message) {
        return new Result<>(ResultCode.FAILED.getCode(), message, null);
    }

    /** 未登录返回结果 */
    @SuppressWarnings("unused")
    public static <T> Result<T> unauthorized() {
        return failed(ResultCode.UNAUTHORIZED);
    }

    /** 未授权返回结果 */
    @SuppressWarnings("unused")
    public static <T> Result<T> forbidden() {
        return failed(ResultCode.FORBIDDEN);
    }
}
