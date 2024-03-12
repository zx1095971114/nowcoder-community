package com.nowcoder.community.controller.vo;

import com.nowcoder.community.entity.Message;
import com.nowcoder.community.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : Zhou Xiang
 * @date : 2023/12/22  12:49
 * @description :登录用户的vo
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginUserVo {
    //登录的用户
    private User user;

    //用户的未读消息
    private int unReadCount;
}
