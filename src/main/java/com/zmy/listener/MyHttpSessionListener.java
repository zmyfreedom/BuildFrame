package com.zmy.listener;

import jakarta.servlet.http.HttpSessionListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MyHttpSessionListener implements HttpSessionListener {
    private int count = 0;

    @Override
    public synchronized void sessionCreated(jakarta.servlet.http.HttpSessionEvent se) {
        log.info("Session created: {}", se.getSession().getId());
        count++;
        se.getSession().getServletContext().setAttribute("sessionCount", count);
    }

    @Override
    public synchronized void sessionDestroyed(jakarta.servlet.http.HttpSessionEvent se) {
        log.info("Session destroyed: {}", se.getSession().getId());
        count--;
        se.getSession().getServletContext().setAttribute("sessionCount", count);
    }
}
