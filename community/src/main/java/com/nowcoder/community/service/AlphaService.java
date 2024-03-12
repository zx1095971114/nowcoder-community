package com.nowcoder.community.service;

import com.nowcoder.community.dao.AlphaDao;
import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.CommonUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.apache.ibatis.transaction.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
//@Scope("prototype")
public class AlphaService {
    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DiscussPostMapper discussPostMapper;

    private static final Logger logger = LoggerFactory.getLogger(AlphaService.class);

    public AlphaService(){
        System.out.println("创建实例");
    }

    @PostConstruct
    public void init(){
        System.out.println("初始化");
    }

    @PreDestroy
    public void destroy(){
        System.out.println("销毁实例");
    }

    @Autowired
    private AlphaDao alphaDao;
    public String service(){
        System.out.println("service");
        return alphaDao.alphaDao();
    }


    //测试事务的隔离等级，注解方式
    //业务为注册新人后自动发帖，需要修改两个表，且要保证这两个表修改的一致性
    //isolation表示事务的隔离级别，分四种
    //propagation是该事务的传播等级，主要有三种
    // REQUIRED 表示该事务如果是内部事务，则会隔离性和外部保持一致，否则自己会新建一个事务
    // REQUIRES_NEW 表示如果有外部事务，会暂停外部事务，开启自己的事务
    // NESTED 表示如果存在外部事务，就嵌套在外部事务中执行，否则和REQUIRED一致
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public String alphaIn(){
        User user = new User();
        String salt = CommonUtils.generateUUID().substring(0, 5);
        user.setPassword(CommonUtils.MD5("123" + salt));
        user.setCreateTime(new Date());
        user.setUsername("1");
        user.setHeaderUrl("www.baidu.com");
        user.setEmail("123@qq.com");
        user.setActivationCode("1234");

        DiscussPost discussPost = new DiscussPost();
        discussPost.setTitle("新人报道");
        discussPost.setContent("我来了");
        userMapper.insertUser(user);
        discussPostMapper.insertDiscussPost(discussPost);

        //模拟出错，看是否都会回滚
        int a = 3 / 0;

        return "ok";
    }

    //编程事务，使用transactionTemplate
    //使用transactionTemplate方法的execute方法即可保证事务的隔离性
    //execute的方法里写逻辑
    public void alphaIn2(){
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        //这里的泛型代表执行该事务的返回值
        //底层会将execute的步骤按照上面指定的事务等级执行
        //底层是将doInTransaction的操作纳入当前线程的事务管理，然后继续按顺序执行doInTransaction的代码
        //如果在doInTransaction中出现错误，就会回滚doInTransaction中的代码
        String result = transactionTemplate.execute(new TransactionCallback<String>() {

            @Override
            public String doInTransaction(TransactionStatus status) {
                User user = new User();
                String salt = CommonUtils.generateUUID().substring(0, 5);
                user.setPassword(CommonUtils.MD5("123" + salt));
                user.setCreateTime(new Date());
                user.setUsername("1");
                user.setHeaderUrl("www.baidu.com");
                user.setEmail("123@qq.com");
                user.setActivationCode("1234");

                DiscussPost discussPost = new DiscussPost();
                discussPost.setTitle("新人报道");
                discussPost.setContent("我来了");
                userMapper.insertUser(user);
                discussPostMapper.insertDiscussPost(discussPost);

                //模拟出错，看是否都会回滚
                int a = 3 / 0;

                return "ok";
            }
        });
    }

    public <T, V> void a(Map<T, V> map){
        for(Map.Entry<T, V> entry: map.entrySet()){
            System.out.println(entry.getKey().toString() + ":" + entry.getValue().toString());
        }
    }

    //Spring普通线程池的简便方法，以后调用这个方法的时候，会自动从线程池里取出一个线程，来异步地执行这个逻辑
    @Async
    public void execute1(){
        System.out.println("现在的线程是" + Thread.currentThread().getName());
//        logger.debug("execute1");
    }

    // 定义一个定时任务，初始延迟是10000ms，频率是1000ms
//    @Scheduled(initialDelay = 10000, fixedRate = 1000)
    public void fixedTask(){
        logger.debug("定时任务");
    }

}
