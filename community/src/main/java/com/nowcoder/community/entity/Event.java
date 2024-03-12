package com.nowcoder.community.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : Zhou Xiang
 * @date : 2023/12/24  19:47
 * @description :向消息队列中发消息时事件的实体，封装消息内容
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    //发送的主题
    private String topicWithTag;
    //谁做了这件事
    private int userId;
    //这两个属性感觉没什么用
    //行为操作的实体类型，如点赞的是评论还是帖子；评论的是评论还是帖子；关注的是人还是帖子
    private int entityType;
    //操作实体的id
    private int entityId;
    //实体作者的信息，也是通知要发给的人
    private int entityUserId;
    //其他属性封装位置
    private Map<String, Object> content = new HashMap<>();

    public Event setContentAttribute(String key, Object value){
        content.put(key, value);
        return this;
    }

    public Object getContentValue(String key){
        return content.get(key);
    }
}
