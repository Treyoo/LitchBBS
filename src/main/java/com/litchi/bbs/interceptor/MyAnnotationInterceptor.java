package com.litchi.bbs.interceptor;

import com.litchi.bbs.annotation.MyAnnotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * 本拦截器用于处理自定义注解@MyAnnotation
 *
 * @author cuiwj
 * @date 2020/3/19
 */
@Component
public class MyAnnotationInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(MyAnnotationInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {//如果拦截到的是方法
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();//通过反射获取method对象
            MyAnnotation myAnnotation = method.getAnnotation(MyAnnotation.class);//获取方法的注解
            if (myAnnotation != null) {
                //do something
                /*logger.debug(method.getName() + "方法被调用，你看到此日志是因为该方法使用了自定义注解："
                        + myAnnotation.toString());*/
            }
        }
        return true;
    }
}
