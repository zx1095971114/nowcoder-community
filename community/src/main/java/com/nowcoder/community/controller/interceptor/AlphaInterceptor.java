package com.nowcoder.community.controller.interceptor;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author : Zhou Xiang
 * @date : 2023/12/6  20:46
 * @description :拦截器(用于在处理请求前后做一些操作)示例
 **/
@Deprecated
//@Component
public class AlphaInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(AlphaInterceptor.class);

    //handler是拦截目标
    //在Controller前处理请求
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception{
        logger.debug("preHandler: " + handler.toString());
        return true;
    }

    //在Controller之后，模板引擎之前处理请求
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception{
        logger.debug("postHandler: " + handler.toString());
    }

    //在模板引擎前处理请求
    //ex是模板引擎报的异常
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception{
        logger.debug("afterCompletion: " + handler.toString());
    }

}
