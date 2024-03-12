package com.nowcoder.community.service.iml;

import com.alibaba.fastjson2.JSON;
import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.joint.UserService;
import com.nowcoder.community.util.CommonUtils;
import com.nowcoder.community.util.Constants;
import com.nowcoder.community.util.MailClient;
import com.nowcoder.community.util.RedisKeyUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
class UserServiceImp implements UserService, Constants {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private TemplateEngine engine;
    @Autowired
    private MailClient mailClient;
    @Autowired
    private RedisKeyUtils redisKeyUtils;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
//    @Autowired
//    private LoginTicketMapper loginTicketMapper;
    @Value("${community.path.domain}")
    private String domain;
    @Value("${server.servlet.context-path}")
    private String contextPath;


    @Override
    public User findUserById(int id){
        User user = getUserCache(id);
        if(user == null){
            user = setUserCache(id);
        }
        return user;
    }

    @Override
    public Map<String, Object> register(User user) {
        Map<String, Object> map = new HashMap<>();

        //检查注册的username，email,password是否合法
        if(user == null){
            throw new IllegalArgumentException("参数User不能为空");
        }
        if(user.getPassword().length() < 8){
            map.put("passwordMsg", "密码长度不能小于8位");
            return map;
        }
        User findedUser = userMapper.selectByName(user.getUsername());
        if(findedUser != null){
            map.put("usernameMsg", "用户名重复");
            return map;
        }
        findedUser = userMapper.selectByEmail(user.getEmail());
        if(findedUser != null){
            map.put("emailMsg", "邮箱已注册");
            return map;
        }

        //补全user的其他信息
        String salt = CommonUtils.generateUUID().substring(0, 5);
        //存入数据库的密码应该被加密过
        String password = CommonUtils.MD5(user.getPassword() + salt);
        user.setPassword(password);
        user.setSalt(salt);
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(CommonUtils.generateUUID());
        user.setHeaderUrl("http://images.nowcoder.com/head/" + String.valueOf(new Random().nextInt(1001)) + "t.png");
        user.setCreateTime(new Date());

        //存入数据库，并发送邮件
        userMapper.insertUser(user);
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        //链接形式为http://localhost:8080/community/activation/153/27aa24fe6517454db46f4d569dd5eb21
        String href = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("href", href);
        String content = engine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(), "牛客网激活账号(仿牛客网项目测试)", content);

        return map;
    }

    @Override
    public int activation(int id, String activationCode) {
        User user = userMapper.selectById(id);
        String realCode = user.getActivationCode();
        int status = user.getStatus();
        if(status == ACTIVATED){
            return ACTIVATION_REPEAT;
        }else{
            if(realCode.equals(activationCode)){
                userMapper.updateStatus(id, ACTIVATED);
                deleteUserCache(id);
                return ACTIVATION_SUCCESS;
            }else {
                return ACTIVATION_FAILURE;
            }
        }
    }

    @Override
    public Map<String, String> login(String username, String password, long expiredSeconds) {
        Map<String, String> map = new HashMap<>();
        //检查用户名，密码是否为空
        if(StringUtils.isBlank(username)){
            map.put("usernameMsg", "用户名不能为空");
            return map;
        }
        if(StringUtils.isBlank(password)){
            map.put("passwordMsg", "用户名不能为空");
            return map;
        }

        //验证用户名和密码
        User user = userMapper.selectByName(username);
        if(user == null){
            map.put("usernameMsg", "用户名不正确");
            return map;
        }
        if(user.getStatus() == NOT_ACTIVATED){
            map.put("usernameMsg", "该账号未激活");
            return map;
        }
        //因为存的是加密过的密码，所以要比较加密后的结果(相同字符串使用MD5加密后结果相同)
        password = CommonUtils.MD5(password + user.getSalt());
        if(!user.getPassword().equals(password)){
            map.put("passwordMsg", "密码错误，请重新输入");
            return map;
        }

        //生成ticket，存入数据库
        LoginTicket loginTicket = new LoginTicket();
        //使用UUID可以近似保证在大多数情况下生成的ticket是唯一的(除非在短时间内生成大量的UUID，这种情况我们一般就忽略了)
        String ticket = CommonUtils.generateUUID();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(ticket);
        loginTicket.setStatus(TICKET_VALID);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000));
//        loginTicketMapper.insertLoginTicket(loginTicket);
        String loginTicketKey = redisKeyUtils.getLoginTicket(ticket);
        stringRedisTemplate.opsForValue().set(loginTicketKey, JSON.toJSONString(loginTicket));

        map.put("ticket", ticket);
        return map;
    }

    @Override
    public void logout(String ticket) {
//        loginTicketMapper.updateStatus(ticket);
        if(StringUtils.isBlank(ticket)){
            return;
        }

        String loginTicketKey = redisKeyUtils.getLoginTicket(ticket);
        LoginTicket loginTicket = JSON.parseObject(stringRedisTemplate.opsForValue().get(loginTicketKey), LoginTicket.class);
        loginTicket.setStatus(1);
        stringRedisTemplate.opsForValue().set(loginTicketKey, JSON.toJSONString(loginTicket));
    }

    @Override
    public Map<String, String> sendVerificationCode(String code, String email) {
        Map<String, String> map = new HashMap<>();
        emailCheck(email, map);
        if(!map.isEmpty()){
            return map;
        }

        Context context = new Context();
        context.setVariable("email", email);
        context.setVariable("code", code);
        String content = engine.process("/mail/forget", context);
        String subject = "牛客网找回密码（仿牛客网项目）";
        mailClient.sendMail(email, subject, content);

        return map;
    }

    @Override
    public Map<String, String> resetPasswordByEmail(String email, String password) {
        Map<String, String> map = new HashMap<>();
        //这里不查空是因为前面的js已经保证了password不会为空
        if(password.length() < 8){
            map.put("passwordMsg", "密码不能小于8位");
            return map;
        }
        User user = emailCheck(email, map);
        if(!map.isEmpty()){
            return map;
        }
        String salt = user.getSalt();
        password = CommonUtils.MD5(password + salt);
        userMapper.updatePassword(user.getId(), password);
        deleteUserCache(user.getId());

        return map;
    }

    @Override
    public User findUserByTicket(String ticket) {
        //检查ticket是否为空，过期
        if(StringUtils.isBlank(ticket)){
            return null;
        }
//        LoginTicket loginTicket = loginTicketMapper.selectByTicket(ticket);
        String loginTicketKey = redisKeyUtils.getLoginTicket(ticket);
        LoginTicket loginTicket = JSON.parseObject(stringRedisTemplate.opsForValue().get(loginTicketKey), LoginTicket.class);

        if(loginTicket == null || loginTicket.getStatus() == TICKET_INVALID){
            return null;
        }
        Date now = new Date();
        if(now.after(loginTicket.getExpired())){
            return null;
        }

        //检查找到的user是不是为空或者未激活
        int id = loginTicket.getUserId();
        User user = userMapper.selectById(id);
        if(user == null){
            return null;
        }
        if(user.getStatus() == NOT_ACTIVATED){
            return null;
        }
        return user;
    }

    @Override
    public int changeHeaderUrl(String url, int userId) {
        int changeLine = userMapper.updateHeaderUrl(userId, url);
        deleteUserCache(userId);
        return changeLine;
    }

    @Override
    public int resetPasswordById(String password, int userId) {
        int changeLine = userMapper.updatePassword(userId, password);
        deleteUserCache(userId);
        return changeLine;
    }

    @Override
    public User findUserByUsername(String username) {
        if(StringUtils.isBlank(username)){
            return null;
        }
        return userMapper.selectByName(username);
    }

    /**
     * @Author Zhou Xiang
     * @Description 找回密码时，无论是发验证码还是重置密码都要检查邮箱
     * @Date 2023/12/5 22:11
     * @param email 邮箱
     * @param map 结果放到map中
     * @return User 找到的user
     **/
    private User emailCheck(String email, Map<String, String> map){
        if(StringUtils.isBlank(email)){
            map.put("emailMsg", "请填写邮箱");
            return null;
        }
        User user = userMapper.selectByEmail(email);
        if(user == null){
            map.put("emailMsg", "邮箱不存在");
            return null;
        }
        if(user.getStatus() == NOT_ACTIVATED){
            map.put("emailMsg", "该邮箱对应账号未激活");
            return user;
        }
        return user;
    }

    //从redis缓存中取用户
    private User getUserCache(int userId){
        String userKey = redisKeyUtils.getUserKey(userId);
        String userString = stringRedisTemplate.opsForValue().get(userKey);
        if(userString == null){
            return null;
        }
        return JSON.parseObject(userString, User.class);
    }

    //向缓存中加入数据，并返回加入的数据
    private User setUserCache(int userId){
        String userKey = redisKeyUtils.getUserKey(userId);
        User user = userMapper.selectById(userId);
        if(user != null){
            stringRedisTemplate.opsForValue().set(userKey, JSON.toJSONString(user), 60, TimeUnit.MINUTES);
        }

        return user;
    }

    //删除缓存中的数据
    private void deleteUserCache(int userId){
        User user = getUserCache(userId);
        if(user != null){
            stringRedisTemplate.delete(redisKeyUtils.getUserKey(userId));
        }
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.findUserByUsername(username);
    }
}
