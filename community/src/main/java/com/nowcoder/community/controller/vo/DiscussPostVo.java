package com.nowcoder.community.controller.vo;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : Zhou Xiang
 * @date : 2024/2/11  21:16
 * @description :帖子的视图
 **/

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DiscussPostVo {
    private User user;
    private DiscussPost discussPost;
    private int likeCount;
}
