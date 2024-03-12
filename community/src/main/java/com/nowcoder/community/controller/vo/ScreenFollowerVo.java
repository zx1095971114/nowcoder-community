package com.nowcoder.community.controller.vo;

import com.nowcoder.community.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author : Zhou Xiang
 * @date : 2024/2/3  0:00
 * @description : 用户的所有粉丝视图
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScreenFollowerVo {
    //展现的某一个用户(粉丝)
    private User user;
    //关注user的时间
    private Date followTime;
    //当前登录用户是否关注了他
    private boolean isFollowed;
}
