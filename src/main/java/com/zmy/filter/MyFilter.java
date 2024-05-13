package com.zmy.filter;

import jakarta.servlet.*;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;

@Slf4j
public class MyFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 初始化逻辑
        log.info("MyFilter初始化");
    }
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        // 过滤逻辑
        log.info("MyFilter放行前逻辑");
        chain.doFilter(request, response);
        log.info("MyFilter放行后逻辑");
    }


    @Override
    public void destroy() {
        // 销毁逻辑
        log.info("MyFilter销毁");
    }
}
