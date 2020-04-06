package com.litchi.bbs.config;

import com.litchi.bbs.service.UserService;
import com.litchi.bbs.util.LitchiUtil;
import com.litchi.bbs.util.constant.LitchiConst;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author cuiwj
 * @date 2020/4/6
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter implements LitchiConst {
    @Autowired
    UserService userService;

    @Override
    public void configure(WebSecurity web) throws Exception {
        // 不过滤静态资源的访问
        web.ignoring().antMatchers("/resources/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //权限配置
        http.authorizeRequests().antMatchers(
                "/user/setting",
                "/user/upload",
                "/discuss/add",
                "/comment/add/**",
                "/msg/letter/**",
                "/msg/letters",
                "/msg/notices",
                "/like",
                "/dislike",
                "/follow**",
                "/unFollow**"
        ).hasAnyAuthority(
                AUTHORITY_ADMIN,
                AUTHORITY_MODERATOR,
                AUTHORITY_USER
        ).antMatchers(
                "/discuss/top",
                "/discuss/wonderful"
        ).hasAnyAuthority(
                AUTHORITY_MODERATOR
        ).antMatchers(
                "/discuss/delete",
                "/data/**",
                "/actuator/**"
        ).hasAnyAuthority(
                AUTHORITY_ADMIN
        ).anyRequest().permitAll()
                .and().csrf().disable();//TODO 开启防csrf
        //配置异常情况的处理方式
        http.exceptionHandling()
                //没登录
                .authenticationEntryPoint(new AuthenticationEntryPoint() {
                    @Override
                    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
                        String xRequestedWith = request.getHeader("x-requested-with");
                        if ("XMLHttpRequest".equals(xRequestedWith)) {//异步请求
                            response.setContentType("application/plain;charset=utf-8");
                            PrintWriter writer = response.getWriter();
                            writer.write(LitchiUtil.getJSONString(403, "你还没有登录哦!"));
                        } else {//同步请求
                            response.sendRedirect(request.getContextPath() + "/login_page");
                        }
                    }
                })
                //没权限
                .accessDeniedHandler(new AccessDeniedHandler() {
                    @Override
                    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
                        String xRequestedWith = request.getHeader("x-requested-with");
                        if ("XMLHttpRequest".equals(xRequestedWith)) {
                            response.setContentType("application/plain;charset=utf-8");
                            PrintWriter writer = response.getWriter();
                            writer.write(LitchiUtil.getJSONString(403, "你没有访问此功能的权限!"));
                        } else {
                            response.sendRedirect(request.getContextPath() + "/denied");
                        }
                    }
                });
        // Security底层默认会拦截路径为/logout的请求,进行退出处理.
        // 修改它拦截的路径区别于/logout避免和我们的冲突,才能执行我们自己的退出代码.
        http.logout().logoutUrl("/security_logout");
    }
}
