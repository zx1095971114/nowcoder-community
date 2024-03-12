package com.nowcoder.community.controller.vo;

import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : Zhou Xiang
 * @date : 2023/12/15  17:44
 * @description : 对评论的评论的视图
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comment2CommentVo {
    //评论详情
    private Comment comment;
    //发起评论的作者
    private User user;
    //被评论的人
    private User target;
    //该评论被点赞的数量
    private long likeCount;
    //当前用户对该评论的点赞状态
    private String likeStatus;
}
