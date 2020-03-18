package com.litchi.bbs.interceptor;

import com.litchi.bbs.dao.LoginTokenDAO;
import com.litchi.bbs.dao.UserDAO;
import com.litchi.bbs.entity.HostHolder;
import com.litchi.bbs.entity.LoginToken;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private LoginTokenDAO loginTokenDAO;

    @Autowired
    private UserDAO userDAO;

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
            LoginToken loginToken = loginTokenDAO.selectByToken(token);
            if (loginToken != null && loginToken.getStatus() == 0 && loginToken.getExpired().after(new Date())) {
                hostHolder.set(userDAO.selectById(loginToken.getUserId()));
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
    }
}
