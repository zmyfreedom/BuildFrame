package com.zmy.listener;

import jakarta.servlet.ServletRequestEvent;
import jakarta.servlet.ServletRequestListener;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MyServletRequestListener implements ServletRequestListener {
    @Override
    public void requestInitialized(ServletRequestEvent sre) {
        // 请求开始时调用
        HttpServletRequest request=(HttpServletRequest)sre.getServletRequest();
        log.info("请求开始");
        log.info("session id为：{}", request.getRequestedSessionId());
        log.info("request url为：{}", request.getRequestURL());

        request.setAttribute("name", "测试");
    }
    @Override
    public void requestDestroyed(ServletRequestEvent sre) {
        // 请求结束时调用
        HttpServletRequest request=(HttpServletRequest)sre.getServletRequest();
        log.info("请求结束,request域中保存的name值为:{}",request.getAttribute("name"));
    }

}
