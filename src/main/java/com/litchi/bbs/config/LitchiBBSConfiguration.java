package com.litchi.bbs.config;


import com.litchi.bbs.interceptor.LoginRequiredInterceptor;
import com.litchi.bbs.interceptor.MyAnnotationInterceptor;
import com.litchi.bbs.interceptor.PassportInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 用于注册Interceptor
 * author:CuiWJ
 * date:2018/12/7
 */
@Component
public class LitchiBBSConfiguration implements WebMvcConfigurer {
    @Autowired
    PassportInterceptor passportInterceptor;
    @Autowired
    LoginRequiredInterceptor loginRequiredInterceptor;
    @Autowired
    MyAnnotationInterceptor myAnnotationInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(passportInterceptor)//不拦截静态资源访问
                .excludePathPatterns("/**/*.css","/**/*.png","/**/*.jpg","/**/*.jpeg");;
        registry.addInterceptor(loginRequiredInterceptor)
                .addPathPatterns("/user/*")
                .addPathPatterns("/*like")
                .addPathPatterns("/comment/add/*")
                .addPathPatterns("/*follow*")
                .addPathPatterns("/msg/*")
                .excludePathPatterns("/**/*.css","/**/*.png","/**/*.jpg","/**/*.jpeg");;

        registry.addInterceptor(myAnnotationInterceptor)
                .excludePathPatterns("/**/*.css","/**/*.png","/**/*.jpg","/**/*.jpeg");
    }
}
