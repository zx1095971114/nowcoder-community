package com.nowcoder.community.service.joint;

import com.nowcoder.community.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface UserService extends UserDetailsService {
    /**
     * @Author Zhou Xiang
     * @Description 通过id来查询用户
     * @Date 2023/11/30 20:54
     * @Param [int] [id]
     * @return com.nowcoder.community.entity.User
     **/
    User findUserById(int id);

    /**
     * @Author Zhou Xiang
     * @Description 提供注册服务
     * @Date 2023/12/3 10:21
     * @param user 传进来的注册用户
     * @return java.util.Map<java.lang.String,java.lang.Object> 封装的错误提示信息
     **/
    Map<String, Object> register(User user);

    /**
     * @Author Zhou Xiang
     * @Description 提供激活服务，改用户的激活状态
     * @Date 2023/12/3 17:47
     * @param id 激活的用户id
     * @param activationCode 用户的激活码
     * @return int 是否激活成功的状态码
     **/
    int activation(int id, String activationCode);

    /**
     * @Author Zhou Xiang
     * @Description 执行登录逻辑
     * @Date 2023/12/5 9:55
     * @param username 账号
     * @param password 密码
     * @param expiredSeconds 多久过期，以s为单位
     * @return java.util.Map<java.lang.String,java.lang.String> 返回错误信息或者是生成的ticket
     **/
    Map<String, String> login(String username, String password, long expiredSeconds);

    /**
     * @Author Zhou Xiang
     * @Description 登出服务
     * @Date 2023/12/5 12:19
     * @param ticket 登录的凭证
     * @return void
     **/
    void logout(String ticket);

    /**
     * @Author Zhou Xiang
     * @Description 向邮箱中发送验证码邮件
     * @Date 2023/12/5 16:34
     * @param code 验证码
     * @param email 发送的邮箱
     * @return java.util.Map<java.lang.String,java.lang.String> 封装错误信息，如果没有，会返回一个空map
     **/
    Map<String, String> sendVerificationCode(String code, String email);

    /**
     * @Author Zhou Xiang
     * @Description 根据邮箱改密码
     * @Date 2023/12/5 22:05
     * @param email 邮箱
     * @param password 新密码
     * @return java.util.Map<java.lang.String,java.lang.String> 产生的问题
     **/
    Map<String, String> resetPasswordByEmail(String email, String password);

    /**
     * @Author Zhou Xiang
     * @Description 根据ticket查询用户，如果查不到或者凭证过期，返回null
     * @Date 2023/12/6 21:28
     * @param ticket 浏览器凭证ticket
     * @return com.nowcoder.community.entity.User
     **/
    User findUserByTicket(String ticket);

    /**
     * @Author Zhou Xiang
     * @Description 改变用户的头像
     * @Date 2023/12/8 21:42
     * @param url
     * @param userId
     * @return int 记录改变的条数
     **/
    int changeHeaderUrl(String url, int userId);

    /**
     * @Author Zhou Xiang
     * @Description 用户在修改资料界面修改密码,这里要求传的密码是加密过的
     * @Date 2023/12/9 10:15
     * @param password
     * @param userId
     * @return int
     **/
    int resetPasswordById(String password, int userId);

    /**
     * @Author Zhou Xiang
     * @Description 根据用户名找用户
     * @Date 2023/12/22 21:32
     * @param username 用户的用户名
     * @return com.nowcoder.community.entity.User 找到的用户
     **/
    User findUserByUsername(String username);
}
