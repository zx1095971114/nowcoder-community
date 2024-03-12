package com.nowcoder.community.controller.vo;

import com.nowcoder.community.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : Zhou Xiang
 * @date : 2023/12/24  10:17
 * @description :用户详情页面视图
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInformationVo {
    //哪个用户
    private User user;
    //用户被点赞数
    private long likeCount;
    //用户关注的用户数目
    private long followeeCount;
    //用户的粉丝数目
    private long followerCount;
}
