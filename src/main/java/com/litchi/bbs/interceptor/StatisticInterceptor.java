package com.litchi.bbs.interceptor;

import com.litchi.bbs.entity.HostHolder;
import com.litchi.bbs.service.StatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * 统计访问数据
 *
 * @author cuiwj
 * @date 2020/4/10
 */
@Component
public class StatisticInterceptor implements HandlerInterceptor {
    @Autowired
    private StatisticService statisticService;
    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Date date = new Date();
        String ip = request.getRemoteHost();
        statisticService.addUV(ip, date);//统计UV
        if (hostHolder.get() != null) {
            statisticService.addDAU(hostHolder.get().getId(), date);//统计DAU
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
