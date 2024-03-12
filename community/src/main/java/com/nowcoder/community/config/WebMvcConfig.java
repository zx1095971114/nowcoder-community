package com.nowcoder.community.config;

import com.nowcoder.community.controller.interceptor.AlphaInterceptor;
import com.nowcoder.community.controller.interceptor.DataInterceptor;
import com.nowcoder.community.controller.interceptor.LoginInterceptor;
import com.nowcoder.community.controller.interceptor.LoginRequiredInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author : Zhou Xiang
 * @date : 2023/12/6  20:56
 * @description : 拦截器的配置类
 **/
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    //这个是实验的，为减小服务器负担，现在废弃了
//    @Autowired
//    private AlphaInterceptor alphaInterceptor;

//    现在不再使用拦截器来进行认证和授权，而是通过spring security在filter处进行身份认证和权限控制
    @Autowired
    private LoginInterceptor loginInterceptor;

//    现在不再使用拦截器来进行认证和授权，而是通过spring security在filter处进行身份认证和权限控制
//    @Autowired
//    private LoginRequiredInterceptor loginRequiredInterceptor;

    @Autowired
    private DataInterceptor dataInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(alphaInterceptor)
//                .excludePathPatterns("/**/*.css", "/**/*.png", "/**/*.js", "/**/*.jpg", "/**/*.jpeg")
//                .addPathPatterns("/login", "/register");
//
        registry.addInterceptor(loginInterceptor).
                excludePathPatterns("/**/*.css", "/**/*.png", "/**/*.js", "/**/*.jpg", "/**/*.jpeg");
//
//        registry.addInterceptor(loginRequiredInterceptor).
//                excludePathPatterns("/**/*.css", "/**/*.png", "/**/*.js", "/**/*.jpg", "/**/*.jpeg");

        registry.addInterceptor(dataInterceptor).
                excludePathPatterns("/**/*.css", "/**/*.png", "/**/*.js", "/**/*.jpg", "/**/*.jpeg");
    }
}
