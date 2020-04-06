package com.litchi.bbs.interceptor;

import com.litchi.bbs.entity.HostHolder;
import com.litchi.bbs.entity.LoginToken;
import com.litchi.bbs.entity.User;
import com.litchi.bbs.service.UserService;
import com.litchi.bbs.util.JedisAdapter;
import com.litchi.bbs.util.LitchiUtil;
import com.litchi.bbs.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * 获取用户身份
 * author:CuiWJ
 * date:2018/12/7
 */
@Component
public class PassportInterceptor implements HandlerInterceptor {
    /*@Autowired
    private LoginTokenDAO loginTokenDAO;*/
    @Autowired
    private JedisAdapter jedisAdapter;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        String token = null;
        Cookie[] cookies = httpServletRequest.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("token".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }
        if (token != null) {
            /*LoginToken loginToken = loginTokenDAO.selectByToken(token);*/
            String redisKey = RedisKeyUtil.getLoginTokenKey(token);
            LoginToken loginToken = LitchiUtil.parseObject(jedisAdapter.get(redisKey), LoginToken.class);
            if (loginToken != null && loginToken.getStatus() == 0 && loginToken.getExpired().after(new Date())) {
                User user = userService.selectUserById(loginToken.getUserId());
                hostHolder.set(user);
                // 由于我们绕过了Spring Security使用了我们之前的认证逻辑，所以认证通过后我们要手动
                // 构建认证结果并存入SecurityContext中，以便于Security进行判断认证和授权 by Cuiwj
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        user, user.getPassword(), userService.getAuthorities(user));
                SecurityContextHolder.setContext(new SecurityContextImpl(authentication));
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        if (modelAndView != null && hostHolder.get() != null) {
            modelAndView.addObject("loginUser", hostHolder.get());
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        hostHolder.clear();
        SecurityContextHolder.clearContext();
    }
}
