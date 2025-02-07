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
package com.zerolinck.passiflora.common.handler;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.StringJoiner;

import com.zerolinck.passiflora.common.api.Result;
import com.zerolinck.passiflora.common.api.ResultCode;
import com.zerolinck.passiflora.common.exception.BizException;
import com.zerolinck.passiflora.common.util.NetUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import lombok.extern.slf4j.Slf4j;

/**
 * 全局异常捕获
 *
 * @author linck
 */
@Slf4j
@RestControllerAdvice
@ConditionalOnProperty(prefix = "passiflora.config", name = "exception", havingValue = "true")
public class GlobalExceptionHandler {

    @ExceptionHandler(value = IllegalStateException.class)
    public Result<String> processException(IllegalStateException e) {
        log.error("捕获状态异常", e);
        return Result.illegalArgs(e.getMessage());
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    public Result<String> processException(IllegalArgumentException e) {
        log.error("捕获参数异常", e);
        return Result.illegalArgs(e.getMessage());
    }

    @ExceptionHandler(value = NoSuchElementException.class)
    public Result<String> processException(NoSuchElementException e) {
        log.error("捕获元素不存在异常", e);
        return Result.illegalArgs(e.getMessage());
    }

    /** 捕获自定义异常 */
    @ExceptionHandler(value = BizException.class)
    public Result<String> processException(BizException e) {
        log.error("捕获自定义异常", e);
        return Result.failed(e.getCode(), e.getMessage());
    }

    /** 捕获异常返回详细异常信息 */
    @ExceptionHandler(Exception.class)
    public Result<String> exceptionHandler(Exception e) {
        long epochSecond =
                LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        String code = Long.toHexString(epochSecond).toUpperCase(Locale.ROOT);
        log.error("系统错误，错误代码: 0X{}", code, e);
        return Result.failed("系统错误，错误代码：0X" + code);
    }

    /** 捕获请求参数错误 */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result<String> exceptionHandler(HttpMessageNotReadableException e) {
        log.error("请求参数错误", e);
        return Result.failed(ResultCode.ILLEGAL_ARGUMENT);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public Result<String> exceptionHandler(NoResourceFoundException e) {
        NetUtils.getResponse().setStatus(404);
        log.error("404 Not Found: {}", e.getMessage());
        return Result.failed(ResultCode.NOT_FOUND);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Result<String> exceptionHandler(HttpRequestMethodNotSupportedException e) {
        log.error("RequestMethod 不支持", e);
        return Result.failed(e.getMessage());
    }

    /** 捕获异常返回详细异常信息 */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public Result<String> exceptionHandler(MaxUploadSizeExceededException e) {
        log.error("上传文件大小超出系统限制", e);
        return Result.failed("上传文件大小超出系统限制");
    }

    /** Post参数验证异常 */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<String> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        // 将所有的错误提示使用"，"拼接起来并返回
        StringJoiner sj = new StringJoiner("，");
        e.getBindingResult().getFieldErrors().forEach(x -> sj.add(x.getDefaultMessage()));
        log.error("参数错误：{}", sj);
        return Result.illegalArgs(sj.toString());
    }

    /** get参数验证异常,会抛出一个BindException */
    @ExceptionHandler(BindException.class)
    public Result<String> constraintViolationExceptionHandler(BindException e) {
        StringJoiner sj = new StringJoiner("，");
        e.getBindingResult().getFieldErrors().forEach(x -> sj.add(x.getDefaultMessage()));
        log.error("参数错误：{}", sj);
        return Result.illegalArgs(sj.toString());
    }
}
