package com.zmy.controller;

import com.zmy.common.ResponseResult;
import com.zmy.common.UnInterceptor;
import com.zmy.entity.User;
import com.zmy.service.ListenerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLEncoder;

/**
 * @author zmy
 * @create 2024-04-23 9:39
 */
@RestController
@RequestMapping("listener")
@Tag(name="资源模块")
@Slf4j
public class ListenerController {
    @Resource
    private ListenerService listenerService;

    @Operation(summary="监听ServletContext")
    @GetMapping("test")
    public ResponseResult<User> getUserList(HttpServletRequest request){
        ServletContext servletContext= request.getServletContext();
        User user=(User)servletContext.getAttribute("user");
        return ResponseResult.ok(user);
    }

    @Operation(summary = "监听session")
    @GetMapping("session")
    public ResponseResult<Integer> getSessionCount(HttpServletRequest request, HttpServletResponse response){
        Cookie cookie;
        try{
            log.info("获取sessionId成功:{}",request.getSession().getId());
            cookie=new Cookie("JSESSIONID", URLEncoder.encode(request.getSession().getId(),"utf-8"));
            cookie.setPath("/");
            cookie.setMaxAge(10);
            response.addCookie(cookie);
        }catch (Exception e){
            e.printStackTrace();
            log.error("获取sessionId失败");
        }
        Integer count=(Integer)request.getSession().getServletContext().getAttribute("sessionCount");
        return ResponseResult.ok(count);
    }

    @Operation(summary = "监听ServletRequest")
    @GetMapping("/request")
    public ResponseResult<String> getRequestInfo(HttpServletRequest request) {
        System.out.println("requestListener中的初始化的name数据：" + request.getAttribute("name"));
        return ResponseResult.ok("success");
    }

    @Operation(summary = "监听MyEvent")
    @GetMapping("/myEvent")
    public ResponseResult<User> getMyEvent(HttpServletRequest request) {
        return ResponseResult.ok(listenerService.getUserEvent());
    }

    @Operation(summary = "拦截器")
    @GetMapping("/interceptor")
    @UnInterceptor//通过反射获取该注解，放行，不拦截
    public ResponseResult<String> getInterceptor(HttpServletRequest request) {
        log.info("拦截器方法");
        return ResponseResult.ok("interceptor");
    }
}
