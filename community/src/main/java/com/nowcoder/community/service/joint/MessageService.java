package com.nowcoder.community.service.joint;

import com.nowcoder.community.entity.Event;
import com.nowcoder.community.entity.Message;
import org.apache.commons.digester.annotations.rules.BeanPropertySetter;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author : Zhou Xiang
 * @date : 2023/12/22  11:46
 * @description : 与信息有关的service
 **/
@Service
public interface MessageService {
    /**
     * @Author Zhou Xiang
     * @Description 查询某用户会话的最后一条信息
     * @Date 2023/12/22 11:48
     * @param userId 用户id
     * @param offset 偏移量
     * @param limit 限制
     * @return java.util.List<com.nowcoder.community.entity.Message> 查到的所有信息
     **/
    List<Message> findConversationLastMessages(int userId, int offset, int limit);
    /**
     * @Author Zhou Xiang
     * @Description 查询用户所有对话数
     * @Date 2023/12/22 11:50
     * @param userId 用户id
     * @return int 查到的数量
     **/
    int findCountOfConversation(int userId);

    /**
     * @Author Zhou Xiang
     * @Description 根据对话id，查对话中所有的信息
     * @Date 2023/12/22 11:51
     * @param conversationId  对话的id
     * @param offset 偏移量
     * @param limit 限制
     * @return java.util.List<com.nowcoder.community.entity.Message> 查到的信息
     **/
    List<Message> findMessagesOfConversation(String conversationId, int offset, int limit);

    /**
     * @Author Zhou Xiang
     * @Description 查询某对话的总信息条数
     * @Date 2023/12/22 11:53
     * @param conversationId 查询的会话id
     * @return int 查询到的信息条数
     **/
    int findCountOfMessagesOfConversation(String conversationId);

    /**
     * @Author Zhou Xiang
     * @Description 查询某用户所有未读消息的条数
     * @Date 2023/12/22 11:55
     * @param userId 用户id
     * @return int 查到的信息条数
     **/
    int findUnreadCount(int userId);

    /**
     * @Author Zhou Xiang
     * @Description 查询某用户某对话中所有未读信息的条数
     * @Date 2023/12/22 11:56
     * @param userId 用户id
     * @param conversationId 会话id
     * @return int 查到的信息条数
     **/
    int findUnreadCount(int userId, String conversationId);

    /**
     * @Author Zhou Xiang
     * @Description 发送私信
     * @Date 2023/12/22 21:10
     * @param fromId 信息的发起者
     * @param toId 信息的接受者
     * @param content 信息内容
     * @return int 改变记录的条数
     **/
    int sendMessage(int fromId, int toId, String content);

    /**
     * @Author Zhou Xiang
     * @Description 将所有messages中的message的状态改为已读
     * @Date 2023/12/22 22:15
     * @param messages 要修改状态的messages
     * @return int 修改的条数
     **/
    int readMessage(List<Message> messages);

    /**
     * @Author Zhou Xiang
     * @Description 发送系统消息
     * @Date 2023/12/24 22:12
     * @param event 系统消息对应的消息事件
     * @return int 改变的记录条数
     **/
    int sendSystemMessage(Event event);

    /**
     * @Author Zhou Xiang
     * @Description 查询某用户某主题的最后一条系统消息
     * @Date 2023/12/25 9:44
     * @param userId 用户id
     * @param topic 主题
     * @return com.nowcoder.community.entity.Message 查到的消息
     **/
    Message findLastSystemMessage(int userId, String topic);

    /**
     * @Author Zhou Xiang
     * @Description 查询用户某个主题系统消息的总消息数
     * @Date 2023/12/25 9:48
     * @param userId 用户id
     * @param topic 主题
     * @return int
     **/
    int findCountOfTopicMessage(int userId, String topic);

    /**
     * @Author Zhou Xiang
     * @Description 查询某用户所有的系统消息数目
     * @Date 2023/12/25 9:54
     * @param userId 用户id
     * @return int 系统消息总数
     **/
    int findCountOfAllSystemMessage(int userId);

    /**
     * @Author Zhou Xiang
     * @Description 查询某个用户某个主题下未读的系统消息数目
     * @Date 2023/12/25 9:49
     * @param userId 用户id
     * @param topic 主题
     * @return int
     **/
    int findUnreadCountOfTopic(int userId, String topic);

    /**
     * @Author Zhou Xiang
     * @Description 查询某用户所有的未读系统消息数目
     * @Date 2023/12/25 9:55
     * @param userId 用户id
     * @return int 所有未读消息数目
     **/
    int findUnreadCountOfAllSystemMessage(int userId);

    /**
     * @Author Zhou Xiang
     * @Description 查询某用户某主题下所有的系统消息
     * @Date 2023/12/25 9:51 
     * @param userId 用户id
     * @param topic 查询的主题
     * @param offset 偏移量
     * @param limit 条数限制
     * @return java.util.List<com.nowcoder.community.entity.Message>
     **/
    List<Message> findSystemMessageByTopic(int userId, String topic, int offset, int limit);

    /**
     * @Author Zhou Xiang
     * @Description 阅读某一条系统消息
     * @Date 2023/12/25 17:30
     * @param userId 阅读的用户
     * @param messageId 被阅读的信息的id
     * @return int 改变的记录条数
     **/
    @Deprecated
    int readSystemMessage(int userId, int messageId);
}
