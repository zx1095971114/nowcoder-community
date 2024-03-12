package com.nowcoder.community;

import com.nowcoder.community.service.AlphaService;
import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;
import java.util.*;

/**
 * @author : Zhou Xiang
 * @date : 2023/12/20  22:44
 * @description : 专门测试alpha的(用以测试一些小demo)
 **/
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class AlphaTest {
    @Autowired
    private AlphaService service;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Value("${rocketmq.name-server}")
    private String NAMESRV_ADDR;

    @Test
    public void testTransactional(){
        service.alphaIn();
    }

    @Test
    public void testTransactional2(){
        service.alphaIn2();
    }

    @Test
    public void testRedis(){
//        stringRedisTemplate.opsForValue().set("test:community", "test1");
//        long a = stringRedisTemplate.opsForSet().add("test:community", "a", "b");
//        System.out.println(a);
        stringRedisTemplate.opsForValue().increment("test1");
    }
    
    //redis事务使用演示
    @Test
    public void testRedisTransaction(){
        List<String> o = stringRedisTemplate.execute(new SessionCallback<>() {
            @Override
            public List<String> execute(RedisOperations operations) {
                List<Object> result;
                operations.opsForValue().set("e", "a");
                try {
                    //标识事务的开始
                    operations.multi();
                    operations.opsForSet().add("a", "f");
                    operations.opsForSet().add("a", "d");
                    int b = 1 / 0;
                    operations.opsForSet().add("a", "t");
                    Set<String> a = operations.opsForSet().members("a");
                    //这里，因为还没有执行,所以返回的是一个空的set，注意不是null
                    System.out.println(a);
                    //执行所有提交的事务,这里的返回值是所有事件依次返回的结果，是List<Object>
                    //注意，执行完以后就不能
                    result = operations.exec();
                } catch (Exception e) {
                    //回滚事务,注意discard()方法调用必须在operations.exec()之前，因为执行完operations.exec()以后，redis已经把事务执行完了
                    operations.discard();
                    throw new RuntimeException(e);
                }
                List<String> result1 = new ArrayList<>();
                for(Object o: result){
                    result1.add(o.toString());
                }
                //这里返回什么，stringRedisTemplate.execute就会返回什么
                return result1;
            }
        });

        System.out.println("o: " + o);
    }

    @Test
    public void testAsynchronous() throws RemotingException, InterruptedException, MQClientException, IOException {
        DefaultMQProducer producer = new DefaultMQProducer("testProducerGroup");
        producer.setNamesrvAddr(NAMESRV_ADDR);
        Message message = new Message("myTestTopic","Asynchronous Message".getBytes());
        producer.start();

        producer.send(message, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                System.out.println("Thread: " + Thread.currentThread().getName());
                System.out.println("发送成功");
            }

            @Override
            public void onException(Throwable e) {
                e.printStackTrace();
                System.out.println("发送失败");
            }
        });

        System.out.println("Thread: " + Thread.currentThread().getName());

        System.in.read();
    }



}
