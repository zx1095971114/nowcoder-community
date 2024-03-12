package com.nowcoder.community;

import com.nowcoder.community.dao.*;
import com.nowcoder.community.entity.*;
import com.nowcoder.community.util.CommonUtils;
import com.nowcoder.community.util.Constants;
import org.assertj.core.util.DateUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.List;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MapperTest implements Constants {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private AlphaMapper alphaMapper;

    @Test
    public void testSelect(){
        User user = userMapper.selectById(101);
        System.out.println(user);
        user = userMapper.selectByName("guanyu");
        System.out.println(user);
        user = userMapper.selectByEmail("nowcoder103@sina.com");
        System.out.println(user);
    }
    @Test
    public void testUpdate(){
        userMapper.updateHeaderUrl(150, "http://www.nowcoder.com/152.jpg");
        userMapper.updatePassword(150, "123456789");
        userMapper.updateStatus(150, 1);
    }

    @Test
    public void testLoginTicketInsert(){
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setTicket("123456");
        loginTicket.setStatus(0);
        loginTicket.setUserId(45);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + 1 * 60 * 1000));

        loginTicketMapper.insertLoginTicket(loginTicket);
    }

    @Test
    public void testSelectAndUpdate(){
        LoginTicket loginTicket = loginTicketMapper.selectByTicket("123456");
        System.out.println(loginTicket);
        loginTicketMapper.updateStatus(loginTicket.getTicket());
        loginTicket = loginTicketMapper.selectByTicket("123456");
        System.out.println(loginTicket);
    }

    @Test
    public void insertDiscussPostTest(){
        DiscussPost discussPost = new DiscussPost();
        discussPost.setContent("RealTest");
        discussPost.setScore(0);
        discussPost.setTitle("test");
        discussPost.setUserId(155);
        discussPost.setType(0);
        discussPost.setStatus(0);
        discussPost.setCommentCount(0);
        discussPost.setCreateTime(new Date());
        discussPostMapper.insertDiscussPost(discussPost);
    }

    @Test
    public void selectDiscussPostByIdTest(){
        DiscussPost discussPost = discussPostMapper.selectDiscussPostById(288);
        System.out.println(discussPost);
    }

    @Test
    public void selectComments(){
        List<Comment> list = commentMapper.selectComments(-1,2,66, 0, 4);
        for(Comment comment: list){
            System.out.println(comment);
        }
    }

    @Test
    public void selectCommentCounts(){
        int count = commentMapper.selectCommentsCounts(-1, 2,66);
        System.out.println(count);
    }

    @Test
    public void insertComment(){
        Comment comment = new Comment();
        comment.setContent("test");
        comment.setStatus(0);
        comment.setUserId(10086);
        comment.setCreateTime(new Date());
        comment.setEntityType(1);
        comment.setEntityId(10086);
        comment.setTargetId(10086);
        commentMapper.insertComment(comment);
    }

    @Test
    public void testMessage(){
//        List<Message> messages = messageMapper.selectConversationLastMessage(111, 0, 5);
//        messages.forEach(
//                message -> {
//                    System.out.println(message);
//                }
//        );

//        System.out.println();
//        int count1 = messageMapper.selectConversationsCount(111);
//        System.out.println("conversationCount: " + count1);
//
//        System.out.println();
//        List<Message> messages1 = messageMapper.selectMessagesByConversationId("111_112", 0, 100);
//        messages1.forEach(
//                message -> {
//                    System.out.println(message);
//                }
//        );
//
//        System.out.println();
//        int count2 = messageMapper.selectMessageCountByConversationId("111_112");
//        System.out.println("conversationMessageCount: " + count2);
//
//        System.out.println();
//        int count3 = messageMapper.selectUnreadCount(111, null);
//        System.out.println("unreadCount1: " + count3);
//
        System.out.println();
        int count4 = messageMapper.selectUnreadCount(111, "111_145");
        System.out.println("unreadCount2: " + count4);
    }

    //把密码都改为123456789，方便查看情况
    @Test
    public void test() throws Exception{
        List<User> users = alphaMapper.selectUsers();
        for(User user: users){
            String password = "123456789";
            password = CommonUtils.MD5(password + user.getSalt());
            userMapper.updatePassword(user.getId(), password);
        }

    }

    @Test
    public void testSystemMessage(){
//        Message message = messageMapper.selectLastSystemMessageByTopic(111, TOPIC_TAG_COMMENT);
//        System.out.println(message);

//        int i = messageMapper.selectCountOfTopicMessage(145, TOPIC_TAG_COMMENT);
//        System.out.println(i);

//        int i = messageMapper.selectCountOfTopicMessage(145, null);
//        System.out.println(i);

//        int i = messageMapper.selectUnreadCountOfTopic(112, TOPIC_TAG_LIKE);
//        System.out.println(i);

//        int i = messageMapper.selectUnreadCountOfTopic(112, null);
//        System.out.println(i);

        List<Message> messages = messageMapper.selectSystemMessageByTopic(145, TOPIC_TAG_NOTICE_LIKE, 0, 10);
        for (Message message : messages) {
            System.out.println(message);
        }
    }
}
