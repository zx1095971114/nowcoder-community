package com.nowcoder.community.config.messageQueue;

import com.alibaba.fastjson.JSON;
import com.nowcoder.community.entity.Event;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author : Zhou Xiang
 * @date : 2023/12/24  20:05
 * @description : 消息队列中的生产者
 **/
@Component
public class Producer {

    private static final Logger logger = LoggerFactory.getLogger(Producer.class);

    @Autowired
    private DefaultMQProducer defaultMQProducer;

    /**
     * @Author Zhou Xiang
     * @Description 处理消息的方法
     * @Date 2023/12/24 20:09
     * @param event
     * @return void
     **/
    public void produce(Event event) {
        //发的消息统一发json字符串，收的时候统一自己转
        String message = JSON.toJSONString(event);

        //创建消息
        //标准topic为topic:tag
        String[] topicAndTag = event.getTopicWithTag().split(":");
        String topic = topicAndTag[0];
        String tag = topicAndTag[1];
        Message payLoad = new Message(topic, tag, message.getBytes());

        try {
            //发送消息，点赞要快一点响应，所以用异步请求
            //注意异步请求要在返回的时候再关producer
            defaultMQProducer.send(payLoad, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    //由spring来管理
                    //关闭生产者
//                    defaultMQProducer.shutdown();
                }

                @Override
                public void onException(Throwable throwable) {
                    //由spring来管理
                    //关闭生产者
//                    defaultMQProducer.shutdown();
                    logger.error("发送消息失败!" + throwable.getMessage());
                    for (StackTraceElement element : throwable.getStackTrace()) {
                        logger.error(element.toString());
                    }
                }
            });

//            defaultMQProducer.sendOneway(payLoad);
//            defaultMQProducer.shutdown();
        } catch (Exception e) {
            logger.error("系统消息发送失败！" + e.getMessage());
            for (StackTraceElement element : e.getStackTrace()) {
                logger.error(element.toString());
            }
        }

    }
}
