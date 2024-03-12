package com.nowcoder.community.service.joint;

import com.nowcoder.community.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author : Zhou Xiang
 * @date : 2023/12/23  15:24
 * @description :点赞相关的服务
 **/
@Service
public interface LikeService {
    /**
     * @Author Zhou Xiang
     * @Description 进行点赞
     * @Date 2023/12/23 15:27
     * @param userId 点赞的用户的id
     * @param entityType 被点赞的实体类型
     * @param entityId 被点赞的实体id
     * @param entityUserId 被点赞的实体的作者，(这里不到mysql中去查是为了节约时间)
     * @param postId 被点赞的帖子或评论所在的帖子
     * @return void
     **/
    void like(int userId, int entityType, int entityId, int entityUserId,int postId);

    /**
     * @Author Zhou Xiang
     * @Description 查询某个实体点赞的数量
     * @Date 2023/12/23 15:42
     * @param entityType 实体类型
     * @param entityId 实体id
     * @return long 实体的点赞数量
     **/
    long getLikeCount(int entityType, int entityId);

    /**
     * @Author Zhou Xiang
     * @Description 查询某个用户对某个实体的点赞情况
     * @Date 2023/12/23 15:45
     * @param userId 用户id
     * @param entityType 实体类型
     * @param entityId 实体id
     * @return int
     **/
    int getLikeStatus(int userId, int entityType, int entityId);

    /**
     * @Author Zhou Xiang
     * @Description 查询某个用户被点赞的数量(包括帖子和评论)
     * @Date 2023/12/24 9:50
     * @param userId 用户的id
     * @return long 查到的用户被点赞的数量
     **/
    long getUserLikeCount(int userId);
}
