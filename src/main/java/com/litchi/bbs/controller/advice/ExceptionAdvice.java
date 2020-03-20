package com.litchi.bbs.controller.advice;

import com.litchi.bbs.util.LitchiUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Controller统一异常处理
 *
 * @author cuiwj
 * @date 2020/3/20
 */
@ControllerAdvice(annotations = Controller.class)//只扫描带@Controller注解的Bean
public class ExceptionAdvice {
    private static final Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);

    @ExceptionHandler({Exception.class})//处理所有类型Exception
    //方法名任意，参数有很多可选，Spring自动注入，较常用的参数为以下3个
    public void handleException(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        //1.记录异常日志
        logger.error("服务器发生异常：" + e.getMessage());
        for (StackTraceElement element : e.getStackTrace()) {
            logger.error(element.toString());//记录异常栈信息
        }
        //2.给浏览器发送错误响应
        if ("XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"))) {
            //异步请求返回错误JSON
            response.setContentType("application/plain;charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.write(LitchiUtil.getJSONString(1, "服务器发生异常！"));
        } else {
            //同步请求返回重定向到error页面
            response.sendRedirect(request.getContextPath()+"/error");
        }
    }
}
