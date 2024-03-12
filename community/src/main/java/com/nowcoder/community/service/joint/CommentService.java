package com.nowcoder.community.service.joint;

import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.entity.Page;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author : Zhou Xiang
 * @date : 2023/12/15  17:03
 * @description : 与评论有关的service
 **/
@Service
public interface CommentService {
    /**
     * @Author Zhou Xiang
     * @Description 根据分页信息和被回复的实体的id查comments
     * @Date 2023/12/15 17:13
     * @param entityType 被回复的实体类型
     * @param entityId 被回复的实体id
     * @param limit 分页的limit
     * @param offset 分页的offset
     * @return java.util.List<com.nowcoder.community.entity.Comment> 查询到的所有回复
     **/
    List<Comment> findCommentsByEntityId(int entityType, int entityId, int offset, int limit);

    /**
     * @Author Zhou Xiang
     * @Description 根据被评论的实体id查评论数量
     * @Date 2023/12/15 17:16
     * @param entityType 被回复的实体类型
     * @param entityId 被评论的实体id
     * @return int 评论数量
     **/
    int findCommentCountByEntityId(int entityType, int entityId);

    /**
     * @Author Zhou Xiang
     * @Description 增加一条评论
     * @Date 2023/12/21 11:35
     * @param userId 用户id
     * @param entityType 被评论的实体类型
     * @param entityId 被评论的实体id
     * @param targetId 如果是评论的评论，可能是回复某一个人的，这是被回复对象的id
     * @param content 评论的内容
     * @param postId 该评论所在的帖子的id
     * @return int 改变的记录的条数
     **/
    int addComment(int userId, int entityType, int entityId, int targetId, String content, int postId);

    /**
     * @Author Zhou Xiang
     * @Description 查某用户发布的评论
     * @Date 2024/2/4 11:57
     * @param userId 用户id
     * @param offset 偏移量
     * @param limit 每页多少条数据
     * @return java.util.List<com.nowcoder.community.entity.Comment>
     **/
    List<Comment> findComments2PostByUserId(int userId, int offset, int limit);

    /**
     * @Author Zhou Xiang
     * @Description 查某个用户的评论总数
     * @Date 2024/2/4 11:59
     * @param userId 用户id
     * @return int
     **/
    int findComment2PostCountByUserId(int userId);
}
