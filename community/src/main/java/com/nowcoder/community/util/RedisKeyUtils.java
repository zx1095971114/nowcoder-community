package com.nowcoder.community.util;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author : Zhou Xiang
 * @date : 2023/12/23  15:04
 * @description :用来生成redis的key的工具类
 **/
@Component
public class RedisKeyUtils {
    //分隔符
    private static final String SPLIT = ":";
    //以实体为主键的点赞数的前缀(某实体被点赞了多少下)
    private String PREFIX_LIKE_ENTITY;

    //以用户为主键的点赞数的前缀(某用户总共被点了多少赞)
    private String PREFIX_LIKE_USER;

    //某用户关注了哪些实体的键前缀
    private String PREFIX_FOLLOWEE;

    //某用户被哪些用户关注的键前缀
    private String PREFIX_FOLLOWER;

    //存验证码的前缀
    private String PREFIX_KAPTCHA;

    //登录凭证
    private String PREFIX_LOGIN_TICKET;

    //缓存中的用户
    private String PREFIX_USER;

    //重置密码的验证码
    private String PREFIX_RESET_CODE;

    //UV(unique visitor，独立访客，这里的访客是通过IP来标记的),可以是某一天或者某段时间
    private String PREFIX_UV;

    //DAU(daily active user，日活用户，这里的访客必须是已经登录的)，可以是某一天或者某段时间
    private String PREFIX_DAU;

    //与帖子相关的主键
    private String PREFIX_POST;

    //缓存中的热帖页面，这个不实时更新
    private String PREFIX_HOT_POSTS;

    //缓存中的总帖子数目，也不要实时更新
    private String PREFIX_USER_POST_COUNT;

    //日期的格式化方法
    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @PostConstruct
    public void init(){
        PREFIX_LIKE_ENTITY = "like" + SPLIT + "entity";
        PREFIX_LIKE_USER = "like" + SPLIT + "user";
        PREFIX_FOLLOWEE = "followee";
        PREFIX_FOLLOWER = "follower";
        PREFIX_KAPTCHA = "kaptcha";
        PREFIX_LOGIN_TICKET = "loginTicket";
        PREFIX_USER = "user";
        PREFIX_RESET_CODE = "resetCode";
        PREFIX_UV = "uv";
        PREFIX_DAU = "dau";
        PREFIX_POST = "post";
        PREFIX_HOT_POSTS = "hotPosts";
        PREFIX_USER_POST_COUNT= "userPostCount";
    }
    /**
     * @Author Zhou Xiang
     * @Description 根据被点赞的实体类型和实体id，构建出要存的点赞在redis中要存的键，
     * @Date 2023/12/23 15:18
     * @param entityType 实体类型
     * @param entityId 实体id
     * @return java.lang.String 获得的键，最终为like:entity:{entityType}:{entityId}
     **/
    public String getEntityLikeKey(int entityType, int entityId){
        String result = PREFIX_LIKE_ENTITY + SPLIT + entityType + SPLIT + entityId;
        return result;
    }

    /**
     * @Author Zhou Xiang
     * @Description 根据用户，构建出用户被点赞数在redis中的键
     * @Date 2023/12/23 23:03
     * @param userId 用户id
     * @return java.lang.String 获得的键，最终为like:user:{userId}
     **/
    public String getUserLikeKey(int userId){
        String result = PREFIX_LIKE_USER + SPLIT + userId;
        return result;
    }

    /**
     * @Author Zhou Xiang
     * @Description 获取userId用户关注的entityType类型的实体的entityId集合
     * @Date 2024/2/1 20:50
     * @param userId 用户id
     * @param entityType 实体类型
     * @return java.lang.String 获得的键，最终为followee:userId:entityType
     **/
    public String getFolloweeKey(int userId, int entityType){
        String result = PREFIX_FOLLOWEE + SPLIT + userId + SPLIT + entityType;
        return result;
    }

    /**
     * @Author Zhou Xiang
     * @Description 获取entityType类型entityId的实体的粉丝的userId的集合的键
     * @Date 2024/2/1 20:56
     * @param entityType 实体类型
     * @param entityId 实体id
     * @return java.lang.String 获得的键，最终形式为follower:entityType:entityId
     **/
    public String getFollowerKey(int entityType, int entityId){
        String result = PREFIX_FOLLOWER + SPLIT + entityType + SPLIT + entityId;
        return result;
    }

    /**
     * @Author Zhou Xiang
     * @Description 存验证码的key，里面存的是owner对应的验证码
     * @Date 2024/2/4 21:59
     * @param owner 在获取验证码时，临时发放的给用户的凭证
     * @return java.lang.String key为kaptcha:{owner}
     **/
    public String getKaptchaKey(String owner){
        return PREFIX_KAPTCHA + SPLIT + owner;
    }

    /**
     * @Author Zhou Xiang
     * @Description 用户登录凭证的key，里面存的是LoginTicket
     * @Date 2024/2/4 23:28
     * @param ticket 用cookie存的登录凭证
     * @return java.lang.String key形式为LoginTicket:{ticket}
     **/
    public String getLoginTicket(String ticket){
        return PREFIX_LOGIN_TICKET + SPLIT + ticket;
    }

    /**
     * @Author Zhou Xiang
     * @Description 缓存的用户的key，里面存的是User
     * @Date 2024/2/4 23:32
     * @param userId 用户的id
     * @return java.lang.String key形式为user:{userId}
     **/
    public String getUserKey(int userId){
        return PREFIX_USER + SPLIT + userId;
    }

    /**
     * @Author Zhou Xiang
     * @Description 用户忘记密码时存验证码的key，存的是验证码
     * @Date 2024/2/5 0:22
     * @param owner 识别用户的临时凭证
     * @return java.lang.String key为resetCode:{owner}
     **/
    public String getResetCode(String owner){
        return PREFIX_RESET_CODE + SPLIT + owner;
    }
    
    /**
     * @Author Zhou Xiang
     * @Description 获取某一天的uv的key,里面是uv的hyperloglog
     * @Date 2024/2/18 17:14 
     * @param date 某一天的日期
     * @return java.lang.String 形如 uv:2024-03-06
     **/
    public String getUV(Date date){
        String handledDate = dateFormat.format(date);
        return PREFIX_UV + SPLIT + handledDate;
    }

    /**
     * @Author Zhou Xiang
     * @Description 获取起始时间和截止时间的内uv的key，里面是uv的hyperloglog
     * @Date 2024/2/18 17:21
     * @param start 起始时间
     * @param end 截止时间
     * @return java.lang.String 形如 uv:2024-03-06:2024-05-07， 注意返回null表示日期不合法
     **/
    public String getUV(Date start, Date end){
        if(start.after(end)){
            return null;
        }

        String startDate = dateFormat.format(start);
        String endDate = dateFormat.format(end);

        return PREFIX_UV + SPLIT + startDate + SPLIT + endDate;
    }

    /**
     * @Author Zhou Xiang
     * @Description 获取某一天的dav的key,里面是dav的BitMap，userId标识offset，offset为userId的位置是1表示该用户那一天是DAU
     * @Date 2024/2/18 17:27
     * @param date 某一天的日期
     * @return java.lang.String 形如 dav:2024-03-06
     **/
    public String getDAU(Date date){
        String stringDate = dateFormat.format(date);
        return PREFIX_DAU + SPLIT + stringDate;
    }

    /**
     * @Author Zhou Xiang
     * @Description 获取某一段时间的dav的key,里面是dav的BitMap
     * @Date 2024/2/18 17:30
     * @param start 起始时间
     * @param end 终止时间
     * @return java.lang.String 形如 dau:2024-03-06:2024-05-07， 注意返回null表示日期不合法
     **/
    public String getDAU(Date start, Date end){
        if(start.after(end)){
            return null;
        }

        String startDate = dateFormat.format(start);
        String endDate = dateFormat.format(end);

        return PREFIX_DAU + SPLIT + startDate + SPLIT + endDate;
    }

    /**
     * @Author Zhou Xiang
     * @Description 获取某一段时间内，需要改变的帖子的id的key，是一个set，里面存帖子的id
     * @Date 2024/2/20 17:24
     * @Param void
     * @return java.lang.String 形如post:score
     **/
    public String getPostScore(){
        return PREFIX_POST + SPLIT + "score";
    }

    /**
     * @Author Zhou Xiang
     * @Description 存的是DiscussPost的Set，里面是offset limit的条件下，orderMode = 1的帖子
     * @Date 2024/2/23 0:21
     * @param offset 偏移量
     * @param limit 每页的数量
     * @return java.lang.String 形如hotPosts:offset:limit
     **/
    public String getHotPosts(int offset, int limit){
        return PREFIX_HOT_POSTS + SPLIT + offset + SPLIT + limit;
    }

    /**
     * @Author Zhou Xiang
     * @Description 某用户的所有帖子数量，-1表示所有用户，存的就是帖子数量
     * @Date 2024/2/23 0:24
     * @param userId 用户id，-1表示所有用户
     * @return java.lang.String
     **/
    public String getUserPostCount(int userId){
        return PREFIX_USER_POST_COUNT + SPLIT + userId;
    }
}
