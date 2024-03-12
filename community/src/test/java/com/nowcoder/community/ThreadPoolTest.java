package com.nowcoder.community;

import com.nowcoder.community.entity.AlphaEntity;
import com.nowcoder.community.service.AlphaService;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.test.context.ContextConfiguration;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.*;

/**
 * @author : Zhou Xiang
 * @date : 2024/2/19  11:53
 * @description : 线程池的测试类，
 * 注意，在分布式环境下，jdk和spring的定时任务线程池都会出现问题，因为设置存储在内存中，
 * 服务器a不知道服务器b是否已经执行过了该定时任务，解决方法是使用Quartz，它可以把配置持久化到数据库中，这样就能解决这个问题了
 **/
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class ThreadPoolTest {

    //打日志来看看时间，线程
    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolTest.class);

    // jdk的普通线程池，里面是固定只有5个线程
    private final ExecutorService executorService = Executors.newFixedThreadPool(5);

    // spring定时任务的线程池
    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(5);

    @Autowired
    private AlphaService alphaService;

    //spring的普通线程池
    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    //spring的定时任务线程池
    @Autowired
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;


    // jdk普通线程池
    @Test
    public void testExecutorService(){
        Runnable task = new Runnable() {
            @Override
            public void run() {
                logger.debug("jdk普通线程!");
            }
        };

        for (int i = 0; i < 10; i++) {
            //这里就会开一个新线程来执行task里面定义的run()
            executorService.execute(task);
        }

        //阻塞该进程，防止程序过早退出
        Scanner scanner = new Scanner(System.in);
        scanner.nextInt();
    }

//    jdk定时线程池
    @Test
    public void testScheduledExecutorService(){
        Runnable task = new Runnable() {
            @Override
            public void run() {
                logger.debug("jdk定时任务!");
            }
        };

        scheduledExecutorService.scheduleWithFixedDelay(task, 10, 1, TimeUnit.SECONDS);

        Scanner scanner = new Scanner(System.in);
        scanner.nextInt();
    }

//    spring普通线程池
    @Test
    public void testThreadPoolTaskExecutor(){
        Runnable task = new Runnable() {
            @Override
            public void run() {
                logger.debug("spring 普通线程池！");
            }
        };

        for (int i = 0; i < 10; i++) {
            threadPoolTaskExecutor.submit(task);
        }

        Scanner scanner = new Scanner(System.in);
        scanner.nextInt();
    }

//    spring定时线程池
    @Test
    public void testThreadPoolScheduled(){
        Runnable task = new Runnable() {
            @Override
            public void run() {
                logger.debug("spring 定时线程池！");
            }
        };

        threadPoolTaskScheduler.scheduleAtFixedRate(task, Instant.now().plusSeconds(10), Duration.of(1, ChronoUnit.SECONDS));

        Scanner scanner = new Scanner(System.in);
        scanner.nextInt();
    }

    // spring普通线程池的简单写法
    @Test
    public void testThreadPoolTaskExecutorSimple(){
        for (int i = 0; i < 10; i++) {
            alphaService.execute1();
        }

        System.out.println("主线程是: " + Thread.currentThread().getName());
        Scanner scanner = new Scanner(System.in);
        scanner.nextInt();
    }

    // spring定时任务的简化版本
    @Test
    public void testThreadPoolScheduledSimple(){
        Scanner scanner = new Scanner(System.in);
        scanner.nextInt();
    }
}
