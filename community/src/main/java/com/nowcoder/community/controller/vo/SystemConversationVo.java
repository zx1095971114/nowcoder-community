package com.nowcoder.community.controller.vo;

import com.nowcoder.community.entity.Message;
import com.nowcoder.community.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : Zhou Xiang
 * @date : 2023/12/25  10:38
 * @description :系统消息页面的视图实体
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SystemConversationVo {
    //某主题未读的系统消息数目
    private int unRead;
    //最后一条系统信息行为的作者
    private User entityUser;
    //某主题所有的消息数目
    private int allCount;
    //某主题最后一条系统消息
    private Message lastMessage;
    //某主题最后一条系统消息的动作实体类型
    private int entityType;
}
