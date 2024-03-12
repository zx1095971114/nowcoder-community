package com.nowcoder.community.util;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

/**
 * @author : Zhou Xiang
 * @date : 2023/12/2  16:57
 * @description : 发送邮件的工具类
 **/

@Component
public class MailClient {
    @Autowired
    private JavaMailSender sender;

    @Value("${spring.mail.username}")
    private String from;
    private static final Logger logger = LoggerFactory.getLogger(MailClient.class);

    public void sendMail(String to, String subject, String content){
        try {
            MimeMessage message = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            sender.send(message);
        } catch (MessagingException e) {
            logger.error("发送邮件异常: " + e.getMessage());
        }
    }
}
