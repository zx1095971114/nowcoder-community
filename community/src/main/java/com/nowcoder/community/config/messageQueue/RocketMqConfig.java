package com.nowcoder.community.config.messageQueue;

import jakarta.annotation.PreDestroy;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author : Zhou Xiang
 * @date : 2024/2/21  0:09
 * @description :
 **/
@Configuration
public class RocketMqConfig {
    private static final Logger logger = LoggerFactory.getLogger(RocketMqConfig.class);

    @Value("${rocketmq.producer.group}")
    private String producerGroup;

    @Value("${rocketmq.name-server}")
    private String namesrvAddr;

    private DefaultMQProducer producer;

    @Bean
    public DefaultMQProducer defaultMQProducer() {
        producer = new DefaultMQProducer(producerGroup);
        producer.setNamesrvAddr(namesrvAddr);
        try {
            producer.start();
        } catch (Exception e) {
            // 处理异常
            logger.error("启动spring创建的DefaultMQProducer失败！");
        }
        return producer;
    }

    @PreDestroy
    public void destroy() {
        if (producer != null) {
            producer.shutdown();
        }
    }
}
