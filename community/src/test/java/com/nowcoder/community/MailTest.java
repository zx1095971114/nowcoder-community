package com.nowcoder.community;

import com.nowcoder.community.util.MailClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * @author : Zhou Xiang
 * @date : 2023/12/2  17:15
 * @description :
 **/
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MailTest {
    @Autowired
    private MailClient mailClient;
    @Autowired
    private TemplateEngine engine;

    @Test
    public void textMailTest(){
        String to = "1095971114@qq.com";
        String topic = "community Test";
        String content = "Hello world!";
        mailClient.sendMail(to, topic, content);
    }

    @Test
    public void htmlMailTest(){
        Context context = new Context();
        context.setVariable("username", "tryingWorm");
        String content = engine.process("mail/demo", context);
        System.out.println(content);
        mailClient.sendMail("1095971114@qq.com", "html test", content);
    }
}
