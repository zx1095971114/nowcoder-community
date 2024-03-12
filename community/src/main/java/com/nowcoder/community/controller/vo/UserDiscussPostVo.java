package com.nowcoder.community.controller.vo;

import com.nowcoder.community.entity.DiscussPost;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : Zhou Xiang
 * @date : 2024/2/3  22:42
 * @description : 用户的某个帖子的简况
 **/
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDiscussPostVo {
    //帖子本身
    private DiscussPost discussPost;
    //帖子被点赞的数目
    private long likeCount;
}
