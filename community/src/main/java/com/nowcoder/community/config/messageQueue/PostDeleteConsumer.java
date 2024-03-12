package com.nowcoder.community.config.messageQueue;

import com.alibaba.fastjson2.JSON;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Event;
import com.nowcoder.community.service.joint.DiscussService;
import com.nowcoder.community.service.joint.ElasticsearchService;
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

import java.util.List;

/**
 * @author : Zhou Xiang
 * @date : 2024/2/16  16:24
 * @description : 删帖消息的消费者
 **/
@Configuration
public class PostDeleteConsumer implements InitializingBean {
    @Value("${rocketmq.name-server}")
    private String NAMESRV_ADDR;
    private final Logger logger = LoggerFactory.getLogger(PostDeleteConsumer.class);
    @Autowired
    private ElasticsearchService elasticsearchService;

    //消费者组名
    private static final String CONSUMER_GROUP = "cg_post_delete";
    //订阅的主题
    private static final String TOPIC = "topic_elastic_post";
    //主题下的tags
    private static final String TAGS = "tag_delete";

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

                int entityType = event.getEntityType();
                //该事件的entityType表示是要更新elasticsearch的哪个索引
                if(entityType == Constants.ENTITY_DISCUSS_POST){
                    int postId = event.getEntityId();
                    elasticsearchService.deleteDiscussPost(postId);
                }else {
                    logger.error("elasticsearch中没有" + entityType + "类型的索引");
                }

                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });

        //启动消费者
        consumer.start();
    }
}
