package com.litchi.bbs.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 用于统计Controller层方法访问耗时
 *
 * @author cuiwj
 * @date 2020/3/26
 */
@Component
@Aspect
public class ControllerLogAspect {
    private static final Logger logger = LoggerFactory.getLogger(ControllerLogAspect.class);

    @Pointcut("execution(public * com.litchi.bbs.controller.*.*(..))")
    //切点匹配：执行（修饰符匹配 返回值类型 包名.类名.方法名(参数列表))
    public void pointcut() {
    }

    @Around("pointcut()")
    public String around(ProceedingJoinPoint point) throws Throwable {
        Date start = new Date();
        String templatePath = (String) point.proceed();//执行原方法
        long consumeTime = new Date().getTime() - start.getTime();
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert attributes != null;
        HttpServletRequest request = attributes.getRequest();
        String ip = request.getRemoteHost();
        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(start);
        String target = point.getSignature().getDeclaringTypeName() + "." + point.getSignature().getName();
        logger.info(String.format("用户[%s],在[%s],访问了[%s]方法,耗时[%d]ms.", ip, now, target, consumeTime));
        return templatePath;
    }
}
