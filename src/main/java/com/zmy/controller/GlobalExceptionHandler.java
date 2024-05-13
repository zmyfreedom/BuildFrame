package com.zmy.controller;

import cn.hutool.http.HttpStatus;
import com.zmy.Exception.CustomException;
import com.zmy.common.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice//只能捕获controller层抛出的异常
@ResponseBody
@Slf4j
//Spring Boot 默认的事务规则是遇到运行异常（RuntimeException）和程序错误（Error）才会回滚
//事务中需要指定异常类型，@Transactional(rollbackFor = Exception.class)
public class GlobalExceptionHandler {
    //controller层抛出才会被这里捕获
    // 处理参数缺失异常
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseResult<Object> handleException(MissingServletRequestParameterException ex) {
        log.error("参数缺失异常：{}", ex.getMessage());
        return ResponseResult.error(HttpStatus.HTTP_BAD_REQUEST,null, "参数缺失：" + ex.getMessage());
    }

    // 处理空指针异常
    @ExceptionHandler(NullPointerException.class)
    public ResponseResult<Object> handleException(NullPointerException ex) {
        log.error("空指针异常：{}", ex.getMessage());
        return ResponseResult.error(HttpStatus.HTTP_INTERNAL_ERROR,null, "空指针异常：" + ex.getMessage());
    }

    //处理自定义异常
    @ExceptionHandler(CustomException.class)
    public ResponseResult<Object> handleException(CustomException ex) {
        log.error("自定义异常CustomException：{}", ex.getMessage());
        return ResponseResult.error(ex.getCode(),null, ex.getMessage());
    }

    @ExceptionHandler(ArithmeticException.class)
    public ResponseResult<Object> handleException(ArithmeticException ex) {
        log.error("ArithmeticException：{}", ex.getMessage());
        return ResponseResult.error(HttpStatus.HTTP_INTERNAL_ERROR,null, "自定义异常ArithmeticException：" + ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseResult<Object> handleException(Exception ex) {
        log.error("未知异常：{}", ex.getMessage());
        return ResponseResult.error(HttpStatus.HTTP_INTERNAL_ERROR,null, "未知异常：" + ex.getMessage());
    }
}
