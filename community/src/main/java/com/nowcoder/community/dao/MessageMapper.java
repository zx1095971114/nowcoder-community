package com.nowcoder.community.dao;

import com.nowcoder.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author : Zhou Xiang
 * @date : 2023/12/22  9:13
 * @description : 私信列表message的相关查询
 **/
@Mapper
public interface MessageMapper {
    /**
     * @Author Zhou Xiang
     * @Description 查询某用户和所有用户的对话的最后一条私信
     * @Date 2023/12/22 9:22
     * @param userId 查询的用户id
     * @param offset 偏移量
     * @param limit 条数限制
     * @return java.util.List<com.nowcoder.community.entity.Message> 查询到的私信数量
     **/
    List<Message> selectConversationLastMessage(int userId, int offset, int limit);

    /**
     * @Author Zhou Xiang
     * @Description 查询某用户的所有对话数量
     * @Date 2023/12/22 9:46
     * @param userId 用户id
     * @return int 查询到的数量
     **/
    int selectConversationsCount(int userId);

    /**
     * @Author Zhou Xiang
     * @Description 查询某对话中的所有消息
     * @Date 2023/12/22 9:26
     * @param conversationId 对话的id
     * @param offset 偏移量
     * @param limit 条数限制
     * @return java.util.List<com.nowcoder.community.entity.Message> 该对话的所有消息
     **/
    List<Message> selectMessagesByConversationId(String conversationId, int offset, int limit);

    /**
     * @Author Zhou Xiang
     * @Description 查询某对话的所有消息数目
     * @Date 2023/12/22 9:34
     * @param conversationId 对话的id
     * @return int 查到的消息数目
     **/
    int selectMessageCountByConversationId(String conversationId);

    /**
     * @Author Zhou Xiang
     * @Description 查询某用户的某对话的未读消息数量
     * @Date 2023/12/22 9:27
     * @param userId 用户的id
     * @param conversationId 对话id (可以没有，如果没有就是查某用户的所有未读消息数量)
     * @return int 查到的数量
     **/
    int selectUnreadCount(int userId, String conversationId);

    /**
     * @Author Zhou Xiang
     * @Description 向表中插入一条信息
     * @Date 2023/12/22 21:04
     * @param message 插入的消息
     * @return int 改变的记录条数
     **/
    int insertMessage(Message message);

    /**
     * @Author Zhou Xiang
     * @Description 更改messages的状态
     * @Date 2023/12/22 22:08
     * @param messageIds 要修改的messages的id
     * @param status 修改后的状态
     * @return int 改变的记录条数
     **/
    int updateMessagesStatus(List<Integer> messageIds, int status);

    /**
     * @Author Zhou Xiang
     * @Description 根据主题和用户查用户某个主题(conversationId)下的最后一条系统消息
     * @Date 2023/12/25 8:49
     * @param userId 用户id
     * @param topic 主题
     * @return com.nowcoder.community.entity.Message
     **/
    Message selectLastSystemMessageByTopic(int userId, String topic);

    /**
     * @Author Zhou Xiang
     * @Description 查询用户某个主题系统消息的总消息数
     * @Date 2023/12/25 8:52
     * @param userId 用户id
     * @param topic 主题，可以为空，为空就是查所有主题(现在不是，是查不包括follow的)
     * @return int 查到的数量
     **/
    int selectCountOfTopicMessage(int userId, String topic);

    /**
     * @Author Zhou Xiang
     * @Description 查询某个用户某个主题下未读的系统消息数目
     * @Date 2023/12/25 8:54
     * @param userId 用户id
     * @param topic 主题，可以为空，为空就是查所有主题(现在不是，是查不包括follow的)
     * @return int 查询到的数目
     **/
    int selectUnreadCountOfTopic(int userId, String topic);

    /**
     * @Author Zhou Xiang
     * @Description 查询某用户某主题下所有的系统消息
     * @Date 2023/12/25 8:59
     * @param userId 用户id
     * @param topic 查询的主题
     * @param offset 偏移量
     * @param limit 条数限制
     * @return java.util.List<com.nowcoder.community.entity.Message> 查到的消息
     **/
    List<Message> selectSystemMessageByTopic(int userId, String topic, int offset, int limit);
}
