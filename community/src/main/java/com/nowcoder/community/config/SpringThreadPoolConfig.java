package com.nowcoder.community.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author : Zhou Xiang
 * @date : 2024/2/19  21:40
 * @description :
 **/
@Configuration
// 开启定时线程池
@EnableScheduling
// 开启异步(可以使用注解来标识Runnable的task，简化开启线程的书写)
@EnableAsync
public class SpringThreadPoolConfig {
}
