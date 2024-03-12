package com.nowcoder.community.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.controller.vo.LastMessageVo;
import com.nowcoder.community.controller.vo.MessageVo;
import com.nowcoder.community.controller.vo.SystemConversationVo;
import com.nowcoder.community.controller.vo.SystemMessageVo;
import com.nowcoder.community.entity.Message;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.joint.MessageService;
import com.nowcoder.community.service.joint.UserService;
import com.nowcoder.community.util.CommonUtils;
import com.nowcoder.community.util.Constants;
import com.nowcoder.community.util.HostHolder;
import org.apache.ibatis.annotations.DeleteProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author : Zhou Xiang
 * @date : 2023/12/22  12:01
 * @description : 与信息有关的页面
 **/
@Controller
@RequestMapping(path = "message")
public class MessageController implements Constants {
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private UserService userService;
    @Autowired
    private MessageService messageService;

    @LoginRequired
    @RequestMapping(path = "conversation_list", method = RequestMethod.GET)
    public String getConversationList(Model model, Page page){
        User host = hostHolder.getUser();
        //分页设置
        page.setPath("/message/conversation_list");
        page.setLimit(5);
        page.setRecordsCount(messageService.findCountOfConversation(host.getId()));

        //所有会话的最后一条信息设置
        List<LastMessageVo> conversations = new ArrayList<>();
        List<Message> conversationLastMessages = messageService.findConversationLastMessages(host.getId(), page.getOffset(), page.getLimit());
        for(Message message: conversationLastMessages){
            //这里的target是非本次登录用户的人
            int targetId = (message.getFromId() == host.getId()) ? message.getToId() : message.getFromId();
            User target = userService.findUserById(targetId);
            int unRead = messageService.findUnreadCount(host.getId(), message.getConversationId());
            int allCount = messageService.findCountOfMessagesOfConversation(message.getConversationId());
            LastMessageVo lastMessageVo = new LastMessageVo(target, unRead, message, allCount);
            conversations.add(lastMessageVo);
        }
        model.addAttribute("conversations", conversations);
        int allFriendUnread = messageService.findUnreadCount(host.getId());
        model.addAttribute("allFriendUnread", allFriendUnread);
        int allSystemUnread = messageService.findUnreadCountOfAllSystemMessage(host.getId());
        model.addAttribute("allSystemUnread", allSystemUnread);

        return "site/letter";
    }

    @LoginRequired
    @RequestMapping(path = "conversation_details/{conversationId}", method = RequestMethod.GET)
    public String getConversationDetails(Model model, @RequestParam(name = "currentPage", required = false) Integer currentPage,
                                         @PathVariable("conversationId")String conversationId){
        User host = hostHolder.getUser();
        //分页设置
        Page page = new Page();
        page.setPath("/message/conversation_details/" + conversationId);
        page.setLimit(5);
        page.setRecordsCount(messageService.findCountOfMessagesOfConversation(conversationId));
        //因为是时间正序显示的，所以一开始要是最后一页才能显示最新消息,而且必须知道recordsCount以后才能知道准确值
        if(currentPage == null){
            //如果没有传参数，那就定位到最后一页
            page.setCurrentPage(page.getPageCount());
        }else {
            page.setCurrentPage(currentPage);
        }
        model.addAttribute("page", page);

        //获取这是和谁的对话
        String[] ids = conversationId.split("_");
        String targetIdStr = (host.getId() == Integer.parseInt(ids[0])) ? ids[1] : ids[0];
        User target = userService.findUserById(Integer.parseInt(targetIdStr));
        model.addAttribute("target", target);

        //获取分页中的所有消息
        List<MessageVo> messageVos = new ArrayList<>();
        List<Message> messages = messageService.findMessagesOfConversation(conversationId, page.getOffset(), page.getLimit());
        for(Message message: messages){
            MessageVo messageVo = new MessageVo();
            messageVo.setFrom(userService.findUserById(message.getFromId()));
            messageVo.setMessage(message);
            messageVos.add(messageVo);
        }
        model.addAttribute("messageVos", messageVos);

        //修改信息状态为已读
        messageService.readMessage(messages);

        return "site/letter-detail";
    }

    @LoginRequired
    @RequestMapping(path = "send", method = RequestMethod.POST)
    @ResponseBody
    public String sendMessage(String toName, String content){
//        int a = 1/ 0;


        int fromId = hostHolder.getUser().getId();
        int toId = userService.findUserByUsername(toName).getId();
        int result = messageService.sendMessage(fromId, toId, content);
        if(result == 1){
            return CommonUtils.getJSONString(200, "发送成功");
        }else {
            return CommonUtils.getJSONString(500, "发送失败");
        }
    }

    @LoginRequired
    @RequestMapping(path = "notice", method = RequestMethod.GET)
    public String getSystemMessage(Model model){
        User host = hostHolder.getUser();
        //查到用户的未读消息
        int allFriendUnread = messageService.findUnreadCount(host.getId());
        model.addAttribute("allFriendUnread", allFriendUnread);
        int allSystemUnread = messageService.findUnreadCountOfAllSystemMessage(host.getId());
        model.addAttribute("allSystemUnread", allSystemUnread);

        //评论主题的系统消息
        SystemConversationVo commentConversationVo = getSystemConversationVo(TOPIC_TAG_NOTICE_COMMENT);
        model.addAttribute("commentConversationVo", commentConversationVo);

        //点赞主题的系统消息
        SystemConversationVo likeConversationVo = getSystemConversationVo(TOPIC_TAG_NOTICE_LIKE);
        model.addAttribute("likeConversationVo", likeConversationVo);

        //关注主题的系统消息
        SystemConversationVo followConversationVo = getSystemConversationVo(TOPIC_TAG_NOTICE_FOLLOW);
        model.addAttribute("followConversationVo", followConversationVo);

        return "site/notice";
    }

    //根据主题，获取SystemConversationVo的信息
    private SystemConversationVo getSystemConversationVo(String topic){
        User host = hostHolder.getUser();
        SystemConversationVo systemConversationVo = new SystemConversationVo();
        Message message = messageService.findLastSystemMessage(host.getId(), topic);
        if(message != null){
            JSONObject jsonObject = JSONObject.parseObject(message.getContent());
            systemConversationVo.setEntityUser(userService.findUserById((Integer) jsonObject.get("userId")));
            systemConversationVo.setUnRead(messageService.findUnreadCountOfTopic(host.getId(), topic));
            systemConversationVo.setAllCount(messageService.findCountOfTopicMessage(host.getId(), topic));
            systemConversationVo.setLastMessage(message);
            systemConversationVo.setEntityType((Integer) jsonObject.get("entityType"));
            return systemConversationVo;
        }else {
            return null;
        }
    }

    @LoginRequired
    @RequestMapping(path = "notice-detail/{topic}", method = RequestMethod.GET)
    public String getTopicNotices(@PathVariable("topic") String topic, Model model, Page page){
        User host = hostHolder.getUser();
        //设置分页
        page.setRecordsCount(messageService.findCountOfTopicMessage(host.getId(), topic));
        page.setPath("/message/notice-detail/" + topic);

        List<SystemMessageVo> systemMessageVos = new ArrayList<>();
        List<Message> systemMessages = messageService.findSystemMessageByTopic(host.getId(), topic, page.getOffset(), page.getLimit());
        for (Message systemMessage : systemMessages) {
            SystemMessageVo systemMessageVo = new SystemMessageVo();
            systemMessageVo.setMessage(systemMessage);
            JSONObject jsonObject = JSONObject.parseObject(systemMessage.getContent());
            systemMessageVo.setSystemUser(userService.findUserById(systemMessage.getFromId()));
            systemMessageVo.setBehaviorUser(userService.findUserById((Integer) jsonObject.get("userId")));
            systemMessageVo.setEntityType((Integer) jsonObject.get("entityType"));

            int behavior = 0;
            String path = "";
            switch (topic) {
                case TOPIC_TAG_NOTICE_LIKE:
                    behavior = BEHAVIOR_LIKE;
                    path = "/discussPost/detail/" + jsonObject.get("postId");
                    break;
                case TOPIC_TAG_NOTICE_COMMENT:
                    behavior = BEHAVIOR_COMMENT;
                    path = "/discussPost/detail/" + jsonObject.get("postId");
                    break;
                case TOPIC_TAG_NOTICE_FOLLOW:
                    behavior = BEHAVIOR_FOLLOW;
                    //暂时只有关注人，所以就先这么写
                    path = "/user/profile/" + jsonObject.get("userId");
                    break;
                default:
                    behavior = 0;
                    path = "";
            }
            systemMessageVo.setBehavior(behavior);
            systemMessageVo.setPath(path);

            systemMessageVos.add(systemMessageVo);
        }

        model.addAttribute("systemMessageVos", systemMessageVos);
        //将消息改为已读
        messageService.readMessage(systemMessages);
        return "site/notice-detail";
    }

//    @RequestMapping(path = "read-system-message", method = RequestMethod.PUT)
//    @ResponseBody
//    @LoginRequired
//    public String readSystemMessage(int messageId){
//        messageService.readSystemMessage(hostHolder.getUser().getId(), messageId);
//        return CommonUtils.getJSONString(200, "修改成功");
//    }
}
