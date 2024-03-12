package com.nowcoder.community.config.messageQueue;

import com.alibaba.fastjson2.JSON;
import com.nowcoder.community.entity.Event;
import com.nowcoder.community.service.joint.MessageService;
import com.nowcoder.community.util.Constants;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author : Zhou Xiang
 * @date : 2023/12/24  20:26
 * @description : 消费者
 **/
@Configuration
//@RocketMQMessageListener(
//        topic = "topic_notice",
//        consumerGroup = "cg_notice",
//        selectorType = SelectorType.TAG,
//        selectorExpression = "tag_comment || tag_like"
//)
//实现了InitializingBean接口的类创建的方法会在spring中所有的Bean设置完属性以后调用
public class Consumer implements Constants, InitializingBean {
    @Value("${rocketmq.name-server}")
    private String NAMESRV_ADDR;
    private final Logger logger = LoggerFactory.getLogger(Consumer.class);
    @Autowired
    private MessageService messageService;
    //消费者组名
    private static final String CONSUMER_GROUP = "cg_notice";
    //订阅的主题
    private static final String TOPIC = "topic_notice";
    //主题下的tags
    private static final String TAGS = "tag_comment || tag_like || tag_follow";

    @Override
    public void afterPropertiesSet() throws Exception {
        //创建消费者
        //pushConsumer和pullConsumer在本质上都是pullConsumer，push只是在连接去取消息的时候保持连接不断开一段时间
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(CONSUMER_GROUP);
        //连上namesvr
        consumer.setNamesrvAddr(NAMESRV_ADDR);
        //订阅一个topic
        consumer.subscribe(TOPIC, TAGS);
        //设置一个监听器，表示获取到消息以后的处理逻辑
        consumer.setMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                MessageExt messageExt = msgs.get(0);

                //处理消息,msgs中固定只会有一个消息
                if(messageExt == null){
                    logger.error("不能处理空消息!");
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }

                String contentStr = new String(messageExt.getBody());
                Event event = JSON.parseObject(contentStr, Event.class);
                if(event == null){
                    logger.error("消息格式不正确!");
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }

                messageService.sendSystemMessage(event);


                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });

        //启动消费者
        consumer.start();
    }
}
