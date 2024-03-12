package com.nowcoder.community.service.joint;

import com.nowcoder.community.service.vo.FolloweeUserVo;
import com.nowcoder.community.service.vo.FollowerVo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author : Zhou Xiang
 * @date : 2024/2/1  21:03
 * @description : 与关注有关的服务
 **/
@Service
public interface FollowService {
    /**
     * @Author Zhou Xiang
     * @Description 某个用户关注某类型的某个实体的方法
     * @Date 2024/2/1 21:05
     * @param userId 用户id
     * @param entityType 实体类型
     * @param entityId 实体id
     * @return void
     **/
    void follow(int userId, int entityType, int entityId);

    /**
     * @Author Zhou Xiang
     * @Description 某用户取关某实体的方法
     * @Date 2024/2/1 21:06
     * @param userId 用户id
     * @param entityType 实体类型
     * @param entityId 实体id
     * @return void
     **/
    void unfollow(int userId, int entityType, int entityId);

    /**
     * @Author Zhou Xiang
     * @Description 获取某个用户关注某个类型的实体的数目
     * @Date 2024/2/1 21:49
     * @param userId 用户id
     * @param entityType 实体类型
     * @return long 用户关注的某类型实体的数目
     **/
    long getFolloweeCount(int userId, int entityType);

    /**
     * @Author Zhou Xiang
     * @Description 获取某类型某实体的粉丝数目
     * @Date 2024/2/1 21:51
     * @param entityType 实体类型
     * @param entityId 实体id
     * @return long 获取某类型某实体的粉丝数目
     **/
    long getFollowerCount(int entityType, int entityId);

    /**
     * @Author Zhou Xiang
     * @Description 判断某用户是不是某类型的某实体的粉丝
     * @Date 2024/2/1 21:53
     * @param userId 用户id
     * @param entityType 实体类型
     * @param entityId 实体id
     * @return boolean 是返回true，不是返回false
     **/
    boolean isFollowerOfFollowee(int userId, int entityType, int entityId);


    /**
     * @Author Zhou Xiang
     * @Description 查某用户关注的用户
     * @Date 2024/2/2 22:50
     * @param userId 待查的用户
     * @param offset 从哪里开始查(包括)
     * @param limit 查几个
     * @return java.util.List<com.nowcoder.community.service.vo.FolloweeUserVo>
     **/
    List<FolloweeUserVo> getFolloweeUsers(int userId, int offset, int limit);

    /**
     * @Author Zhou Xiang
     * @Description 查某用户的粉丝
     * @Date 2024/2/2 22:51
     * @param userId 待查的用户
     * @param offset 从哪里开始查(包括)
     * @param limit 查几个
     * @return java.util.List<com.nowcoder.community.service.vo.FollowerVo>
     **/
    List<FollowerVo> getFollowers(int userId, int offset, int limit);
}
