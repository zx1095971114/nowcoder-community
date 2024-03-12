package com.nowcoder.community.service.vo;

import com.nowcoder.community.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author : Zhou Xiang
 * @date : 2024/2/2  22:41
 * @description : 我关注的用户的视图(entityType = ENTITY_USER)
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FolloweeUserVo {
    //关注的用户
    private User user;
    //关注的时间
    private Date followTime;
}
