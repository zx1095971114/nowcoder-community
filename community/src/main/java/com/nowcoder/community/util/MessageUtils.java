package com.nowcoder.community.util;

/**
 * @author : Zhou Xiang
 * @date : 2023/12/22  12:31
 * @description : 信息有关的工具
 **/
public class MessageUtils {
    /**
     * @Author Zhou Xiang
     * @Description 将fromId和toId拼接成conversationId
     * @Date 2023/12/22 12:32
     * @param fromId 消息发起者
     * @param toId 消息接受者
     * @return java.lang.String 拼接好的conversationId
     **/
    public static String getConversationId(int fromId, int toId){
        if(fromId < toId){
            return String.valueOf(fromId) + "_" + String.valueOf(toId);
        }else {
            return String.valueOf(toId) + "_" + String.valueOf(fromId);
        }
    }
}
