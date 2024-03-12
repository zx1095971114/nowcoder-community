package com.nowcoder.community.controller;

import com.google.code.kaptcha.Producer;
import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.joint.UserService;
import com.nowcoder.community.util.CommonUtils;
import com.nowcoder.community.util.Constants;
import com.nowcoder.community.util.HostHolder;
import com.nowcoder.community.util.RedisKeyUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.A;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author : Zhou Xiang
 * @date : 2023/12/2  21:24
 * @description :控制登录界面，包括登录，注册功能
 **/
@Controller
public class LoginPageController implements Constants {
    private static final Logger logger = LoggerFactory.getLogger(LoggerFactory.class);

    @Autowired
    private UserService userService;
    @Autowired
    private Producer producer;
    @Value("${server.servlet.context-path}")
    private String contextPath;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private RedisKeyUtils redisKeyUtils;
    @Autowired
    private HostHolder hostHolder;

    @RequestMapping(path = "/register", method = RequestMethod.GET)
    public String getRegisterPage(){
        return "site/register";
    }

    @RequestMapping(path = "/login", method = RequestMethod.GET)
    public String getLoginPage(){
        return "site/login";
    }

    @RequestMapping(path = "/forget", method = RequestMethod.GET)
    public String getForgetPage(){
        return "site/forget";
    }

    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public String register(Model model, User user){
        Map<String, Object> map = userService.register(user);
        if(map == null || map.isEmpty()){
            model.addAttribute("message", "注册成功，我们已经向您的邮箱发送了一封激活邮件，请尽快激活！");
            model.addAttribute("href", "/index");
            return "site/operate-result";
        }else{
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("emailMsg", map.get("emailMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            return "site/register";
        }
    }

    //链接形式http://localhost:8080/community/activation/153/27aa24fe6517454db46f4d569dd5eb21
    @RequestMapping(path = "/activation/{userId}/{code}", method = RequestMethod.GET)
    public String activation(Model model,
                             @PathVariable(name = "userId") int userId, @PathVariable(name = "code") String code){
        int activationResult = userService.activation(userId, code);
        switch (activationResult){
            case ACTIVATION_SUCCESS:
                model.addAttribute("message","激活成功，您的账号可以正常使用了");
                model.addAttribute("href", "/login");
                break;
            case ACTIVATION_REPEAT:
                model.addAttribute("message","重复激活，该账号已经激活了");
                model.addAttribute("href", "/index");
                break;
            case ACTIVATION_FAILURE:
                model.addAttribute("message","激活失败，激活码错误");
                model.addAttribute("href", "/index");
                break;
        }
        return "site/operate-result";
    }

    /**
     * @Author Zhou Xiang
     * @Description 获取随机生成的验证码图片
     * @Date 2023/12/4 16:34
     * @Param void
     * @return void
     **/
    @RequestMapping(path = "/kaptcha", method = RequestMethod.GET)
    public void getKaptcha(/*HttpSession session, */HttpServletResponse response){
        String text = producer.createText();
        BufferedImage image = producer.createImage(text);
//        session.setAttribute("kaptcha", text);
        String owner = CommonUtils.generateUUID();
        Cookie cookie = new Cookie("owner", owner);
        //在哪个路径下会带上这个cookie
        cookie.setPath(contextPath);
        //最大存活s数
        cookie.setMaxAge(60);
        response.addCookie(cookie);

        String kaptchaKey = redisKeyUtils.getKaptchaKey(owner);
        //放入redis
        stringRedisTemplate.opsForValue().set(kaptchaKey, text, 60, TimeUnit.SECONDS);

        //输出图片
        response.setContentType("image/png");
        try {
            OutputStream outputStream = response.getOutputStream();
            ImageIO.write(image, "png", outputStream);
        } catch (IOException e) {
            logger.error("验证码生成错误: " + e.getStackTrace());
        }
    }

    @RequestMapping(path = "login", method = RequestMethod.POST)
    public String login(String username, String password, String code, boolean rememberMe,
                        HttpServletResponse response, Model model,
                        /*HttpSession session*/@CookieValue("owner") String owner){
        model.addAttribute("username", username);
        model.addAttribute("password", password);
        model.addAttribute("rememberMe", rememberMe);
        //检查验证码
        if(StringUtils.isBlank(code)){
            model.addAttribute("codeMsg", "验证码不能为空");
            return "site/login";
        }

//        String kaptcha = (String) session.getAttribute("kaptcha");

        String kaptchaKey = redisKeyUtils.getKaptchaKey(owner);
        String kaptcha = stringRedisTemplate.opsForValue().get(kaptchaKey);
        if(!code.equalsIgnoreCase(kaptcha)){
            model.addAttribute("codeMsg", "验证码错误");
            return "site/login";
        }

        //登录
        int expiredSeconds = (rememberMe) ? (REMEMBER_ME_TIMEOUT) : (DEFAULT_TIMEOUT);
        Map<String, String> map = userService.login(username, password, expiredSeconds);
        String ticket = map.get("ticket");
        if(StringUtils.isBlank(ticket)){
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            return "site/login";
        }

        //让cookie记录ticket
        Cookie cookie = new Cookie("ticket", ticket);
        cookie.setPath(contextPath);
        cookie.setMaxAge(expiredSeconds);
        response.addCookie(cookie);

        return "redirect:index";
    }

    @RequestMapping(path = "/logout", method = RequestMethod.GET)
    @LoginRequired
    public String logout(@CookieValue("ticket") String ticket){
        userService.logout(ticket);
        //清除SecurityContext
        SecurityContextHolder.clearContext();
        hostHolder.removeUser();
        return "redirect:index";
    }

    @RequestMapping(path = "/verify", method = RequestMethod.POST)
    @ResponseBody
    public String getVerificationCode(/*HttpSession session*/String email, HttpServletResponse response){
        String code = CommonUtils.generateUUID().substring(0, 5);
        String owner = CommonUtils.generateUUID();
        String resetCodeKey = redisKeyUtils.getResetCode(owner);

        Cookie cookie = new Cookie("resetOwner", owner);
//        session.setAttribute("verificationCode", code);
        //验证码失效时间，s为单位
        final int verifyTimeout = 5 * 60;
        cookie.setMaxAge(verifyTimeout);
//        session.setMaxInactiveInterval(verifyTimeout);
        cookie.setPath(contextPath);
        response.addCookie(cookie);
        //暂时设置可删除的验证码
        stringRedisTemplate.opsForValue().set(resetCodeKey, code, verifyTimeout, TimeUnit.SECONDS);

        Map<String, String> map = userService.sendVerificationCode(code, email);
        if(!map.isEmpty()){
//            model.addAttribute("emailMsg", map.get("emailMsg"));
            return CommonUtils.getJSONString(400, "email格式错误", map);
        }

        return CommonUtils.getJSONString(200, "邮件发送成功，请在五分钟内进行验证！");
    }

    @RequestMapping(path = "/forget", method = RequestMethod.POST)
    @ResponseBody
    public String resetPassword(Model model, /*HttpSession session*/ @CookieValue("resetOwner") String owner,
                                String password, String email, String code){
//        model.addAttribute("password", password);
//        model.addAttribute("email", email);

        String resetCode = null;
        String resetCodeKey = null;
        if(owner != null){
            resetCodeKey = redisKeyUtils.getResetCode(owner);
            resetCode = stringRedisTemplate.opsForValue().get(resetCodeKey);
        }

//        if(session.getAttribute("verificationCode") == null){
        Map<String, String> map = new HashMap<>();
        if(resetCode == null){
//            model.addAttribute("codeMsg", "验证码过期");
            map.put("codeMsg", "验证码过期！");
            return CommonUtils.getJSONString(400, "验证码过期！");
        }

//        String realCode = session.getAttribute("verificationCode").toString();
        if(!code.equals(resetCode)){
            map.put("codeMsg", "验证码错误！");
//            model.addAttribute("codeMsg", "验证码错误");
            return CommonUtils.getJSONString(400, "验证码错误！", map);
        }

        map = userService.resetPasswordByEmail(email, password);
        if(!map.isEmpty()){
//            model.addAttribute("emailMsg", map.get("emailMsg"));
//            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            return CommonUtils.getJSONString(400, "密码或邮箱格式错误！", map);
        }

//        model.addAttribute("message", "修改密码成功");
//        model.addAttribute("href", "/logout");
        //暂时删除验证码
        stringRedisTemplate.delete(resetCodeKey);
        map.put("message", "修改密码成功");
        map.put("href", "/index");
        return CommonUtils.getJSONString(200, "重置密码成功！", map);
    }

    @RequestMapping(path = "/operate-result", method = RequestMethod.GET)
    public String getOperateResult(String message, String href, Model model){
        model.addAttribute("message", message);
        model.addAttribute("href", href);
        return "site/operate-result";
    }

    @RequestMapping(path = "/denied", method = RequestMethod.GET)
    public String getDenied(){
        return "error/404";
    }
}
