package com.nowcoder.community.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author : Zhou Xiang
 * @date : 2023/12/10  21:08
 * @description :通过拦截器的方式，实现被该注解修饰的方法必须已经登录才能访问，
 * 现在不再使用拦截器来进行认证和授权，而是通过spring security在filter处进行身份认证和权限控制
 **/
@Deprecated
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LoginRequired {
}
