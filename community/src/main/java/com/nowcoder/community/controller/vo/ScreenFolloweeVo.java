package com.nowcoder.community.controller.vo;

import com.nowcoder.community.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author : Zhou Xiang
 * @date : 2024/2/2  23:39
 * @description : 用户关注的用户的视图
 **/

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScreenFolloweeVo {
    //展现的某一个用户(up主)
    private User user;
    //关注时间
    private Date followTime;
    //当前登录用户是否关注了他
    private boolean isFollowed;
}
