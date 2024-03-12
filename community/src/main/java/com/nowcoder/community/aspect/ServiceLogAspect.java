package com.nowcoder.community.aspect;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author : Zhou Xiang
 * @date : 2024/2/21  16:39
 * @description : 在访问Service方法时统一记录日志
 **/
@Component
@Aspect
public class ServiceLogAspect {
    private static final Logger logger = LoggerFactory.getLogger(ServiceLogAspect.class);

    @Pointcut("execution(* com.nowcoder.community.service.joint.*.*(..))")
    public void pointcut(){}

    /*记录日志的格式为
    用户 [127.0.0.1] 在[yyyy-MM-dd HH:mm:ss] 访问了 [com.nowcoder.community.service.joint.x.x]
     */
    @Before("pointcut()")
    public void logBefore(JoinPoint joinPoint){
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        String ip = "";
        if(attributes == null){
            ip = "服务器定时任务";
        }else{
            HttpServletRequest request = attributes.getRequest();
            ip = request.getRemoteHost();
        }

        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String method = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();

        logger.info(String.format("用户[%s]在[%s]访问了[%s]", ip, time, method));
    }
}
