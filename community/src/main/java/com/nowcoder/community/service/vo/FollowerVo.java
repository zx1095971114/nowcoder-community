package com.nowcoder.community.service.vo;

import com.nowcoder.community.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author : Zhou Xiang
 * @date : 2024/2/2  22:46
 * @description : 我的粉丝的视图
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FollowerVo {
    //粉丝用户
    private User user;
    //关注的时间
    private Date followTime;
}
