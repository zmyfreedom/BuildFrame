package com.zmy.interceptor;

import com.zmy.common.UnInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.lang.reflect.Method;

/*
preHandle(……) 方法：该方法的执行时机是，当某个 url 已经匹配到对应的 Controller 中的某个方法，且在这个方法执行之前。
所以 preHandle(……) 方法可以决定是否将请求放行，这是通过返回值来决定的，返回 true 则放行，返回 false 则不会向后执行。
postHandle(……) 方法：该方法的执行时机是，当某个 url 已经匹配到对应的 Controller 中的某个方法，且在执行完了该方法，
但是在 DispatcherServlet 视图渲染之前。所以在这个方法中有个 ModelAndView 参数，可以在此做一些修改动作。
afterCompletion(……) 方法：顾名思义，该方法是在整个请求处理完成后（包括视图渲染）执行，
这时做一些资源的清理工作，这个方法只有在 preHandle(……) 被成功执行后并且返回 true 才会被执行。
 */
//默认拦截静态资源
@Slf4j
public class MyInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception{
        // 请求处理之前进行调用（Controller方法调用之前）
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        String methodName = method.getName();
        log.info("拦截器生效，====拦截到了方法：{}，在该方法执行之前执行====", methodName);

        UnInterceptor unInterceptor = method.getAnnotation(UnInterceptor.class);
        // 有自定义注解@UnInterceptor，不拦截，返回true
        if(null != unInterceptor){
            return true;
        }
        // 判断是否登录 /BuildFrame/listener/interceptor?token=1
        String token = request.getParameter("token");
        if (token == null || "".equals(token)) {
            response.getWriter().println("用户未登录，请先登陆");
            log.info("用户未登录，请先登陆");
            return false;
        }
        // 返回true才会继续向下执行，返回false取消当前请求
        return true;
    }
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // 请求处理之后进行调用，但是在视图被渲染之前
        log.info("执行完方法之后进执行(Controller方法调用之后)，但是此时还没进行视图渲染");
        // 修改 ModelAndView
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex){
        // 整个请求处理完毕之后进行调用，也就是在 DispatcherServlet 渲染了对应的视图之后执行（主要是用于进行资源清理工作）
        log.info("请求处理完毕之后进执行(Controller方法调用之后)，但是在视图被渲染之后");
    }
}
