package com.zmy.Exception;

import com.zmy.common.CustomExceptionEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomException extends RuntimeException{

    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 异常码
     */
    private int code;
    /**
     * 异常提示信息
     */
    private String message;
    public CustomException(CustomExceptionEnum customExceptionEnum){
        this.code=customExceptionEnum.getCode();
        this.message=customExceptionEnum.getMessage();
    }

}
