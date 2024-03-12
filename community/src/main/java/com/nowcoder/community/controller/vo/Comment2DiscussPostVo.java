package com.nowcoder.community.controller.vo;

import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author : Zhou Xiang
 * @date : 2023/12/15  17:40
 * @description :对帖子的评论的视图
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comment2DiscussPostVo {
    //评论的主体
    private Comment comment;
    //发起评论的用户
    private User user;
    //评论的评论
    private List<Comment2CommentVo> comments2comment;
    //对该评论的评论数
    private int commentsCount;
    //该评论被点赞的数量
    private long likeCount;
    //当前用户对该评论的点赞状态
    private String likeStatus;
}
