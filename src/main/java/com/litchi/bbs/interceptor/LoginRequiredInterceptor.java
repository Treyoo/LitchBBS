package com.litchi.bbs.interceptor;

import com.litchi.bbs.entity.HostHolder;
import com.litchi.bbs.util.LitchiUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * author:CuiWJ
 * date:2018/12/7
 */
@Component
@Deprecated
public class LoginRequiredInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(LoginRequiredInterceptor.class);

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        if (hostHolder.get() == null) {
            if ("XMLHttpRequest".equalsIgnoreCase(httpServletRequest.getHeader("X-Requested-With"))) {
                //异步请求
                httpServletResponse.setCharacterEncoding("UTF-8");
                httpServletResponse.setContentType("application/json;charset=utf-8");
                try (PrintWriter out = httpServletResponse.getWriter()) {
                    out.append(LitchiUtil.getJSONString(999, "请先登录"));
                    out.flush();
                } catch (IOException e) {
                    logger.error("向response写入数据失败" + e.getMessage());
                }
            } else {
                //同步请求
                if ("GET".equals(httpServletRequest.getMethod())) {
                    httpServletResponse.sendRedirect("/login_page?next=" + httpServletRequest.getRequestURI());
                } else {
                    httpServletResponse.sendRedirect("/login_page");
                }
            }
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
