package com.nowcoder.community.config;

import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.joint.UserService;
import com.nowcoder.community.util.CommonUtils;
import com.nowcoder.community.util.Constants;
import com.nowcoder.community.util.CookieUtils;
import com.nowcoder.community.util.HostHolder;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.http.HttpRequest;

/**
 * @author : Zhou Xiang
 * @date : 2024/2/14  22:01
 * @description : spring security的配置类，spring security可以管理认证和授权，但是，因为我已经写好了认证的逻辑，
 * 所以，我只使用spring security来重构我的授权(原来的管理方式也是通过interceptor和注释来判断是不是要登录，太粗糙，如果不同权限比较多就不好写了)
 **/
@Configuration
public class SecurityConfig {
    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationManagerBuilder builder) throws Exception {
//        builder.authenticationProvider(new AuthenticationProvider() {
//            @Override
//            public Authentication authenticate(Authentication authentication) throws AuthenticationException {
//                String username = authentication.getName();
//                String password = (String) authentication.getCredentials();
//
//                User user = userService.findUserByUsername(username);
//                if(user == null){
//                    throw new UsernameNotFoundException("账号不存在!");
//                }
//
//                password = CommonUtils.MD5(password + user.getSalt());
//                if(password == null){
//                    throw new BadCredentialsException("密码不为空!");
//                }
//
//                if(!password.equals(user.getPassword())){
//                    throw new BadCredentialsException("密码错误!");
//                }
//
//                return new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
//            }
//
//            @Override
//            public boolean supports(Class<?> authentication) {
//                return UsernamePasswordAuthenticationToken.class.equals(authentication);
//            }
//        });
//
//        return builder.build();
//    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        //登录相关配置
//        http.formLogin(configurer -> configurer
//                .loginPage("/login") //这里的意思是login页面是配置的GET "/login"页面
//                .loginProcessingUrl("/login")  //这里的意思是filter发现有POST "/login"的请求就进行拦截，运用的是前面AuthenticationManager的逻辑
//                .successHandler(new AuthenticationSuccessHandler() {
//                    @Override
//                    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
//                        response.sendRedirect(request.getContextPath() + "/index");
//                    }
//                })
//                .failureHandler(new AuthenticationFailureHandler() {
//                    @Override
//                    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
//                        //这是转发，比起重定向，可以给请求带上参数
//                        if(exception instanceof UsernameNotFoundException){
//                            request.setAttribute("usernameMsg", exception.getMessage());
//                        }else if(exception instanceof BadCredentialsException){
//                            request.setAttribute("passwordMsg", exception.getMessage());
//                        }else {
//                            throw new RuntimeException("错误的AuthenticationException: " + exception.getMessage());
//                        }
//                        //因为是dispatcher，所以肯定是已经知道了context-path的
//                        request.getRequestDispatcher("/login").forward(request, response);
//                    }
//                })
//        );

        //登出相关配置
        http.logout(configurer -> configurer
                .logoutUrl("/securityLogout")
//                .logoutSuccessHandler(new LogoutSuccessHandler() {
//                    @Override
//                    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
//                        response.sendRedirect(request.getContextPath() + "/index");
//                    }
//                })
        );

        //因为想用自己的认证逻辑，所以在进入Filter之前，将interceptor要做的事情写好
        http.addFilterBefore(new Filter() {
            //将认证的逻辑提前自己实现
            @Override
            public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
                HttpServletRequest httpRequest = (HttpServletRequest) request;
                HttpServletResponse httpResponse = (HttpServletResponse) response;

//                String path = httpRequest.getServletPath();
//                if(path.matches("^/(.)*(.css)$")
//                        || path.matches("^/(.)*(.js)$")
//                        || path.matches("^/(.)*(.png)$")
//                        || path.matches("^/(.)*(.jpg)$")
//                        || path.matches("^/(.)*(.jpeg)$")
//                ){
//                    //TODO： 跳过后面的filter，直接进入spring mvc中分配静态资源的dispatcher
//
//                }

                logger.debug("url" + httpRequest.getRequestURI());

                String ticket = CookieUtils.getValue(httpRequest, "ticket");

                hostHolder.removeUser();
                SecurityContextHolder.setContext(new SecurityContextImpl(null));
                if(!StringUtils.isBlank(ticket)){
                    User user = userService.findUserByTicket(ticket);
                    if(user != null){
                        hostHolder.setUser(user);
                        Authentication token = new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
                        SecurityContextHolder.setContext(new SecurityContextImpl(token));
                    }
                }

                chain.doFilter(httpRequest, httpResponse);
            }
        }, BasicAuthenticationFilter.class);

        //配置授权信息
        http.authorizeHttpRequests((requests) -> requests
                .requestMatchers(
                        "/comment/add_comment/*",
                        "/discussPost/publish",
                        "/follow",
                        "/unfollow",
                        "/like",
                        "/message/**",
                        "/user/setting",
                        "/user/saveHeader",
                        "/user/resetPassword"
                )
                .hasAnyAuthority(
                        Constants.AUTHORITY_USER,
                        Constants.AUTHORITY_MODERATOR,
                        Constants.AUTHORITY_ADMIN
                )
                .requestMatchers(
                        "/discussPost/top",
                        "/discussPost/untop",
                        "/discussPost/refinement",
                        "/discussPost/unrefinement"
                )
                .hasAnyAuthority(
                        Constants.AUTHORITY_MODERATOR
                )
                .requestMatchers(
                        "/discussPost/delete",
                        "/data/**",
                        "/actuator/**"
                )
                .hasAnyAuthority(
                        Constants.AUTHORITY_ADMIN
                )
                .anyRequest().permitAll()
            );

        //配置权限不够/没有登录时候的处理
        http.exceptionHandling(configurer -> configurer
                //没有登录
                .authenticationEntryPoint(new AuthenticationEntryPoint() {
                    @Override
                    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
                        //判断异步请求
                        String xRequestedWith = request.getHeader("x-requested-with");
                        if(xRequestedWith != null && xRequestedWith.equals("XMLHttpRequest")){
                            //异步请求，发回json
                            response.setContentType("application/plain;charset=UTF-8");
                            PrintWriter writer = response.getWriter();
                            writer.write(CommonUtils.getJSONString(403, "用户未登录！"));
                        }else {
                            //同步请求，重定向到登录
                            response.sendRedirect(request.getContextPath() + "/login");
                        }
                    }
                })
                //权限不足
                .accessDeniedHandler(new AccessDeniedHandler() {
                    @Override
                    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
                        //判断异步请求
                        String xRequestedWith = request.getHeader("x-requested-with");
                        if(xRequestedWith != null && xRequestedWith.equals("XMLHttpRequest")){
                            //异步请求，发回json
                            response.setContentType("application/plain;charset=utf-8");
                            PrintWriter writer = response.getWriter();
                            writer.write(CommonUtils.getJSONString(403, "没有权限！"));
                        }else {
                            //同步请求，重定向到登录
                            response.sendRedirect(request.getContextPath() + "/denied");
                        }
                    }
                })
        );

        //关闭csrf的要求
        http.csrf(csrf -> csrf
                .disable()
        );

        //不用filter来拦截logout，用我自己的逻辑，就是将logoutUrl指定到别的路径，然后不处理
//                .formLogin((form) -> form
//                        .loginPage("/login")
//                        .permitAll()
//                )
//                .logout((logout) -> logout.permitAll());

//        http.addFilterBefore()

        return http.build();
    }
}
