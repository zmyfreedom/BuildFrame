package com.zmy.filter;

import jakarta.servlet.*;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;

@Slf4j
public class YourFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("YourFilter init");
    }
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws ServletException, IOException {
        // 在这里编写过滤器的逻辑
        log.info("YourFilter doFilter before");
        filterChain.doFilter(servletRequest, servletResponse);
        log.info("YourFilter doFilter after");
    }

    @Override
    public void destroy() {
        log.info("YourFilter destroy");
    }
}
