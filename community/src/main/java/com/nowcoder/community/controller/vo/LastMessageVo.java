package com.nowcoder.community.controller.vo;

import com.nowcoder.community.entity.Message;
import com.nowcoder.community.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : Zhou Xiang
 * @date : 2023/12/22  12:06
 * @description : 私信列表首页的每一组对话的最后一条信息
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LastMessageVo {
    //与哪个用户的对话
    private User target;
    //该会话中未读消息的数量
    private int unReadCount;
    //该会话的最后一条未读消息
    private Message lastMessage;
    //该会话的总信息条数
    private int messageCount;
}
