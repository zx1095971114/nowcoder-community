package com.nowcoder.community.controller.advice;

import com.nowcoder.community.util.CommonUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;


/**
 * @author : Zhou Xiang
 * @date : 2023/12/23  9:49
 * @description :用于统一处理异常的通知类
 **/
//扫描所有带有Controller注解的组件
@ControllerAdvice(annotations = Controller.class)
public class ExceptionAdvice {
    private static final Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);

    //如果发生其value中指定的异常，就会调用这个方法处理
    @ExceptionHandler({Exception.class})
    public void handleException(HttpServletRequest request, HttpServletResponse response, Exception e){
        //发生服务器内部异常，就记日志
        logger.error("服务器发生异常: " + e.getMessage());
        StackTraceElement[] stackTrace = e.getStackTrace();
        for(StackTraceElement element: stackTrace){
            logger.error(element.toString());
        }

        //然后根据是不是异步请求，返回不同的响应
        String xRequestedWith = request.getHeader("x-requested-with");
        try {
            if(xRequestedWith != null && xRequestedWith.equals("XMLHttpRequest")){
                //异步请求，返回json,这里用plain而不是json是因为在项目的其他地方异步请求发字符串也是直接发json字符串，到浏览器再重新展开
                response.setContentType("application/plain;charset=UTF-8");
                PrintWriter writer = response.getWriter();
                String jsonString = CommonUtils.getJSONString(500, "服务器异常!");
                writer.write(jsonString);
            }else {
                response.sendRedirect(request.getContextPath() + "/error");
            }
        } catch (IOException ex) {
            logger.error("异常处理类本身异常: " + ex.getMessage());
        }
    }
}
