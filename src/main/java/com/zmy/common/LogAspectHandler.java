package com.zmy.common;
/*
javax.servlet 和 jakarta.servlet 是 Java Servlet API 的两个版本。
javax.servlet 版本是 Java Servlet API 3.1 的旧版本，而 jakarta.servlet 版本是 Java Servlet API 4.0 的更新版本。
tomcat10以后才支持 jakarta.servlet
 */
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 日志切面
 */
@Aspect
@Component
@Slf4j
public class LogAspectHandler {

    //定义一个切面，拦截controller包及其子包下的所有方法
    /*
    execution(* com.zmy.controller..*.*(..))
    第一个 * 号的位置：表示返回值类型，* 表示所有类型
    包名：表示需要拦截的包名，后面的两个句点..表示当前包和当前包的所有子包，com.zmy.controller 包、子包下所有类的方法
    第二个 * 号的位置：表示类名，* 表示所有类
    *(..) ：这个星号表示方法名，* 表示所有的方法，后面括弧里面表示方法的参数，两个句点表示任何参数
     */
    //*/
    @Pointcut("execution(* com.zmy.service.impl.*.*(..))")
    public void methodPointCut(){}

    //annotation() 方式是针对某个注解来定义切面，比如对具有@GetMapping注解的方法做切面
    @Pointcut("@annotation(org.springframework.web.bind.annotation.GetMapping)")
    public void annotationPointCut() {}

    @Before("methodPointCut()")
    public void before(JoinPoint joinPoint) throws UnknownHostException {
        log.info("before method: {}", joinPoint.getSignature());
        log.info("before args: {}", joinPoint.getArgs());
        log.info("before target: {}", joinPoint.getTarget());
        log.info("before package:{}", joinPoint.getSignature().getDeclaringTypeName());
        //获取请求的url
        //String[] requestURI = joinPoint.getTarget().getClass().getAnnotation(org.springframework.web.bind.annotation.RequestMapping.class).value();
        //根据RFC7239规范（Forwarded HTTP Extension），
        // 代理服务器应该设置X-Forwarded-For请求头参数，
        // 并将客户端IP地址放在该参数中传递给下一个代理服务器或Web应用服务器。
        //
        String ip=null;
        try{
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        log.info("request url: {}", request.getRequestURL().toString());
            ip= InetAddress.getLocalHost().getHostAddress();
            log.info("request ip: {}", ip);
        }catch (NullPointerException e){
            if(ip == null) {
                log.info("request ip: {}", "null");
            } else {
                throw e;
            }
        }
    }

    @After("methodPointCut()")
    public void after(JoinPoint joinPoint) {
        log.info("after method: {}", joinPoint.getSignature());
    }

    //AfterReturning可以捕获返回结果,属性 returning 的值res必须要和参数res保持一致，否则会检测不到
    @AfterReturning(pointcut = "methodPointCut()", returning = "res")
    public void afterReturning(JoinPoint joinPoint, Object res) {
        log.info("afterReturning method: {}", joinPoint.getSignature());
        log.info("afterReturning result: {}", res);
    }

    //AfterThrowing可以捕获异常，属性 throwing 的值e必须要和参数e保持一致，否则会检测不到
    @AfterThrowing(pointcut = "methodPointCut()", throwing = "e")
    public void afterThrowing(JoinPoint joinPoint, NullPointerException e) {
        log.info("afterThrowing method: {}", joinPoint.getSignature());
        log.info("afterThrowing exception: {}", e.getMessage());
        throw e;
    }
}
