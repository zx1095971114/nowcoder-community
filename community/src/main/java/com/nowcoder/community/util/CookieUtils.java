package com.nowcoder.community.util;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;


/**
 * @author : Zhou Xiang
 * @date : 2023/12/6  22:19
 * @description :跟cookie有关的静态方法
 **/
public class CookieUtils {
    /**
     * @Author Zhou Xiang
     * @Description 从请求中取出name为name的cookie的value
     * @Date 2023/12/6 22:20
     * @param request
     * @param name
     * @return java.lang.String
     **/
    public static String getValue(HttpServletRequest request, String name){
        if(request == null || name == null){
            throw new IllegalArgumentException("参数不能为null");
        }
        Cookie[] cookies = request.getCookies();
        if(cookies == null){
            return null;
        }
        for(Cookie cookie: cookies){
            if(cookie.getName().equals(name)){
                return cookie.getValue();
            }
        }

        return null;
    }
}
