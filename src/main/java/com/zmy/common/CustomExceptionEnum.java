package com.zmy.common;

/**
 * 1、需要在枚举实例后面加上分号，然后再写构造函数等
 * 2、枚举实例必须在前面
 * 3、定义枚举的构造器方法带参,只能为private
 */
public enum CustomExceptionEnum {
    /** 参数异常 ordinal():0,name(): PARAMETER_EXCEPTION*/
    PARAMETER_EXCEPTION(102, "参数异常!"),
    /** 等待超时 */
    SERVICE_TIME_OUT(103, "服务调用超时！"),
    /** 参数过大 */
    PARAMETER_BIG_EXCEPTION(102, "输入的图片数量不能超过50张!"),
    /** 500 : 一劳永逸的提示也可以在这定义 */
    UNEXPECTED_EXCEPTION(500, "系统发生异常，请联系管理员！");
    // 还可以定义更多的业务异常

    /**
     * 消息码
     */
    private final int code;
    /**
     * 消息内容
     */
    private final String message;

    // 构造方法
    CustomExceptionEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }
    // set get方法

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
