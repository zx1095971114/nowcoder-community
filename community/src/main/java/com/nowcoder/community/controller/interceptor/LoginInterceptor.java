package com.nowcoder.community.controller.interceptor;

import com.nowcoder.community.controller.vo.LoginUserVo;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.joint.MessageService;
import com.nowcoder.community.service.joint.UserService;
import com.nowcoder.community.util.CookieUtils;
import com.nowcoder.community.util.HostHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author : Zhou Xiang
 * @date : 2023/12/6  21:13
 * @description :在进页面前的拦截器，用于实现显示登录信息的需求
 **/
@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Autowired
    private UserService userService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private MessageService messageService;
    @Value("${server.servlet.context-path}")
    private String CONTEXT_PATH;
    private static final Logger logger = LoggerFactory.getLogger(LoginInterceptor.class);

//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
////        logger.debug("url" + request.getRequestURI());
////
////        String ticket = CookieUtils.getValue(request, "ticket");
////        if(StringUtils.isBlank(ticket)){
////            return true;
////        }
////        User user = userService.findUserByTicket(ticket);
////        if(user == null){
////            return true;
////        }
////        //如果登录了还想登录或者注册，就重定向到index,阻止这次请求
////        //这里是因为我不使用spring security自带的logout才产生问题，因为spring-security发现我没登录后，会重定向到login，这样，我的logout就永远无法触发了
//////        if(request.getRequestURI().equals(CONTEXT_PATH + "/login") || request.getRequestURI().equals(CONTEXT_PATH + "/register")){
//////            response.sendRedirect(CONTEXT_PATH + "/index");
//////            return false;
//////        }
////
////        // 为了在本次请求中存取user，而且要考虑多用户并发访问的情景，
////        // 所以不能简单地使用一个静态变量来存(user会被其他用户更改)
////        // 我们使用ThreadLocal来存取
////        // 持有获取到的user
////        hostHolder.setUser(user);
//
//        /* 因为我加入了spring security，又不想改变认证的方式，
//        所以我在原来的认证方式(interceptor)的地方存入了spring security需要的SecurityContext
//         */
//        //但是，security的授权在interceptor之前，在这里加上的话，授权时还是找不到token，会被认为没登录，所以还是改成全由spring security控制吧
////        Authentication token = new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
////        SecurityContextHolder.setContext(new SecurityContextImpl(token));
//
//        return true;
//    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        //获取持有的user
        User user = hostHolder.getUser();
        if(user == null || modelAndView == null){
            return;
        }

        int count = messageService.findUnreadCount(user.getId()) + messageService.findUnreadCountOfAllSystemMessage(user.getId());
        LoginUserVo loginUserVo = new LoginUserVo(user, count);
        modelAndView.addObject("loginUser", loginUserVo);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //在使用完以后要即使清除user，避免一直存着，占用内存
        hostHolder.removeUser();
        SecurityContextHolder.clearContext();
    }
}
