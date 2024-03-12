package com.nowcoder.community.service.iml;

import com.alibaba.fastjson2.JSON;
import com.nowcoder.community.dao.MessageMapper;
import com.nowcoder.community.entity.Event;
import com.nowcoder.community.entity.Message;
import com.nowcoder.community.service.joint.MessageService;
import com.nowcoder.community.util.MessageUtils;
import com.nowcoder.community.util.SensitiveFilter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.*;

import static com.nowcoder.community.util.Constants.SYSTEM_USER_ID;

/**
 * @author : Zhou Xiang
 * @date : 2023/12/22  11:57
 * @description :
 **/
@Service
class MessageServiceImp implements MessageService {
    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Autowired
    private MessageMapper messageMapper;
    @Override
    public List<Message> findConversationLastMessages(int userId, int offset, int limit) {
        return messageMapper.selectConversationLastMessage(userId, offset, limit);
    }

    @Override
    public int findCountOfConversation(int userId) {
        return messageMapper.selectConversationsCount(userId);
    }

    @Override
    public List<Message> findMessagesOfConversation(String conversationId, int offset, int limit) {
        return messageMapper.selectMessagesByConversationId(conversationId, offset, limit);
    }

    @Override
    public int findCountOfMessagesOfConversation(String conversationId) {
        return messageMapper.selectMessageCountByConversationId(conversationId);
    }

    @Override
    public int findUnreadCount(int userId) {
        return messageMapper.selectUnreadCount(userId, null);
    }

    @Override
    public int findUnreadCount(int userId, String conversationId) {
        return messageMapper.selectUnreadCount(userId, conversationId);
    }

    @Override
    public int sendMessage(int fromId, int toId, String content) {
        //如果内容为空，直接返回
        if(StringUtils.isBlank(content)){
            return 0;
        }

        //过滤敏感词和html标签
        content = HtmlUtils.htmlEscape(content);
        content = sensitiveFilter.filterSensitiveWords(content);
        Message message = new Message();
        message.setContent(content);
        message.setStatus(0);
        message.setCreateTime(new Date());
        message.setFromId(fromId);
        message.setToId(toId);
        String conversationId = MessageUtils.getConversationId(fromId, toId);
        message.setConversationId(conversationId);

        return messageMapper.insertMessage(message);
    }

    @Override
    public int readMessage(List<Message> messages) {
        //messageIds为空就不要访问数据库了，浪费资源
        if(messages == null || messages.isEmpty()){
            return 0;
        }

        List<Integer> messageIds = new ArrayList<>();
        for (Message message: messages){
            messageIds.add(message.getId());
        }
        //1表示已读，后期要都修改成常量
        return messageMapper.updateMessagesStatus(messageIds, 1);
    }

    @Override
    public int sendSystemMessage(Event event) {
        Message message = new Message();
        message.setFromId(SYSTEM_USER_ID);
        message.setToId(event.getEntityUserId());
        message.setConversationId(event.getTopicWithTag());
        //表示未读
        message.setStatus(0);
        message.setCreateTime(new Date());
        //内容就把其他event的内容拼到json中
        Map<String, Object> content = new HashMap<>();
        content.put("entityType", event.getEntityType());
        content.put("entityId", event.getEntityId());
        content.put("userId", event.getUserId());
        content.putAll(event.getContent());
        message.setContent(JSON.toJSONString(content));

        return messageMapper.insertMessage(message);
    }

    @Override
    public Message findLastSystemMessage(int userId, String topic) {
        return messageMapper.selectLastSystemMessageByTopic(userId, topic);
    }

    @Override
    public int findCountOfTopicMessage(int userId, String topic) {
        return messageMapper.selectCountOfTopicMessage(userId, topic);
    }

    @Override
    public int findCountOfAllSystemMessage(int userId) {
        return messageMapper.selectCountOfTopicMessage(userId, null);
    }

    @Override
    public int findUnreadCountOfTopic(int userId, String topic) {
        return messageMapper.selectUnreadCountOfTopic(userId, topic);
    }

    @Override
    public int findUnreadCountOfAllSystemMessage(int userId) {
        return messageMapper.selectUnreadCountOfTopic(userId, null);
    }

    @Override
    public List<Message> findSystemMessageByTopic(int userId, String topic, int offset, int limit) {
        return messageMapper.selectSystemMessageByTopic(userId, topic, offset, limit);
    }

    @Override
    @Deprecated
    public int readSystemMessage(int userId, int messageId) {
        List<Integer> list = new ArrayList<>();
        list.add(userId);
        return messageMapper.updateMessagesStatus(list, 1);
    }


}
