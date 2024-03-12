package com.nowcoder.community.controller.vo;

import com.nowcoder.community.entity.Message;
import com.nowcoder.community.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : Zhou Xiang
 * @date : 2023/12/25  12:40
 * @description :系统消息视图
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SystemMessageVo {
    //通知的信息内容
    private Message message;
    //发出通知的系统用户
    private User systemUser;
    //发出的动作代码
    private int behavior;
    //发出动作(点赞、评论)的用户
    private User behaviorUser;
    //动作的实体类型(点赞了评论、帖子)
    private int entityType;
    //动作实体所在的页面(点赞、评论都只能在discuss-detail中进行，关注就链接到用户详情页面;关注暂时只开发了关注个人，链接道个人主页)
    private String path;
}
