package com.nowcoder.community.controller.interceptor;

import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.util.CommonUtils;
import com.nowcoder.community.util.HostHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : Zhou Xiang
 * @date : 2023/12/10  21:13
 * @description :检查登录状态，防止非法访问的拦截器
 **/

@Deprecated
@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {
    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(handler instanceof HandlerMethod){
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            if(handlerMethod.getMethodAnnotation(LoginRequired.class) != null && hostHolder.getUser() == null){
                //判断是不是异步请求，因为异步请求要求返回json格式的字符串
                String xRequestedWith = request.getHeader("x-requested-with");
                if(xRequestedWith != null && xRequestedWith.equalsIgnoreCase("XMLHttpRequest")){
                    Map<String, Object> map = new HashMap<>();
                    map.put("location", request.getContextPath() + "/login");
                    response.getWriter().write(CommonUtils.getJSONString(302, "用户未登录", map));
                }else {
                    response.sendRedirect(request.getContextPath() + "/login");
                }
                return false;
            }
        }

        return true;
    }
}
