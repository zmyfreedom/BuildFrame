package com.zmy.common;

/*
import com.zmy.interceptor.MyInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

//继承 WebMvcConfigurationSupport 类的方式可以用在前后端分离的项目中，后台不需要访问静态资源（就不需要放开静态资源了）
@Configuration
public class MyInterceptorConfig extends WebMvcConfigurationSupport {
    // 添加自定义拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new MyInterceptor())
                .addPathPatterns("/listener/interceptor/**");

        // /*  ：匹配一级，即 /list , /add 等
        // /** ：匹配多级，即 /add , /add/user, /add/user/… 等
        //不要写应用名：BuildFrame，拦截地址不会热部署
        super.addInterceptors(registry);
    }

    // 用来指定静态资源不被拦截，否则继承WebMvcConfigurationSupport这种方式会导致静态资源无法直接访问
    // 静态资源的访问为: 当前项目根路径/ + 静态资源名: BuildFrame/index.html
    // 如果加了其他路径：BuildFrame/static/index.html，则会当作controller请求，
    // 此时可以将ResourceHandler设为/static/**，即可通过 BuildFrame/static/index.html 访问静态资源
    // 排除doc.html，放行swagger文档
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
        registry.addResourceHandler("/doc.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        super.addResourceHandlers(registry);
    }
}
//*/

//*
import com.zmy.interceptor.MyInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//实现 WebMvcConfigure 接口的话，不会拦截 Spring Boot 默认的静态资源
//实现 WebMvcConfigure 接口的方式可以用在非前后端分离的项目中，因为需要读取一些图片、css、js文件等等
@Configuration
public class MyInterceptorConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new MyInterceptor())
                .addPathPatterns("/listener/interceptor/**");
    }
}
//*/