package com.nowcoder.community.dao;

import com.nowcoder.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author : Zhou Xiang
 * @date : 2023/12/15  16:26
 * @description :评论comment表有关的查询
 **/
@Mapper
public interface CommentMapper {
    /**
     * @Author Zhou Xiang
     * @Description 根据被评价的实体查所有评论
     * @Date 2023/12/15 16:29
     * @param entityType 被评价的实体的类型，-1表示所有
     * @param entityId 被评价的实体的id，为-1时表示查所有该类型的评论
     * @param offset 偏移量
     * @param limit 限制
     * @param userId 用户id，为-1时表示是查所有用户
     * @return java.util.List<com.nowcoder.community.entity.Comment> 查询到的comment列表
     **/
    List<Comment> selectComments(int userId, int entityType, int entityId, int offset, int limit);

    /**
     * @Author Zhou Xiang
     * @Description 根据被评价的实体查所有评论的数目
     * @Date 2023/12/15 16:31
     * @param entityId 被评价的实体，-1表示所有
     * @param entityType 被评价的实体类型，-1表示所有
     * @param userId 用户id，-1表示所有
     * @return int 评论数目
     **/
    int selectCommentsCounts(int userId, int entityType, int entityId);

    /**
     * @Author Zhou Xiang
     * @Description 向数据库中插入一条评论
     * @Date 2023/12/21 11:07
     * @param comment 插入的评论
     * @return int 改变的记录的数量
     **/
    int insertComment(Comment comment);
}
