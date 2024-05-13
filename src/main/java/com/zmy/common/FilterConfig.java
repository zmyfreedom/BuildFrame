package com.zmy.common;

import com.zmy.filter.MyFilter;
import com.zmy.filter.YourFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// 过滤器不用再加@Component注解，因为是在此处将过滤器注册到Spring容器中
@Configuration
public class FilterConfig {
    @Bean
    public FilterRegistrationBean<MyFilter> myFilterRegistration() {
        // 创建FilterRegistrationBean对象
        FilterRegistrationBean<MyFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        //注册一个过滤器
        filterRegistrationBean.setFilter(new MyFilter());
        // 设置过滤器的URL模式
        filterRegistrationBean.addUrlPatterns("/*");
        // 设置过滤器的名称
        filterRegistrationBean.setName("myFilter");
        // 设置过滤器的优先级,值越小执行顺序越靠前
        filterRegistrationBean.setOrder(2);
        return filterRegistrationBean;
    }

    @Bean
    public FilterRegistrationBean<YourFilter> yourFilterRegistration() {
        // 创建FilterRegistrationBean对象
        FilterRegistrationBean<YourFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        //注册一个过滤器
        filterRegistrationBean.setFilter(new YourFilter());
        // 设置过滤器的URL模式
        filterRegistrationBean.addUrlPatterns("/*");
        // 设置过滤器的名称
        filterRegistrationBean.setName("yourFilter");
        // 设置过滤器的优先级
        filterRegistrationBean.setOrder(3);
        return filterRegistrationBean;
    }
}
