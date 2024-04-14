package com.zmy.common;

import lombok.Data;

/**
 * @author zmy
 * @create 2024-04-14 17:47
 */
@Data

public class ResponseResult<T> {
    private T data;
    private int code;
    private String message;

    ResponseResult(int code, T data,String message){
        this.data = data;
        this.code = code;
        this.message = message;
    }

    public static <T> ResponseResult<T> ok(T data){
        return new ResponseResult<>(200,data,"success");
    }

    public static <T> ResponseResult<T> error(int code, T data,String message){
        return new ResponseResult<>(code,data,message);
    }
}
