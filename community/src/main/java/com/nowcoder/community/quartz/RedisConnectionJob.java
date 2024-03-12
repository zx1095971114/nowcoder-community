package com.nowcoder.community.quartz;

import org.apache.commons.lang3.StringUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author : Zhou Xiang
 * @date : 2024/2/22  12:34
 * @description : 保持redis连接的job
 **/
@Deprecated
//@Component
public class RedisConnectionJob implements Job {
//    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final Logger logger = LoggerFactory.getLogger(RedisConnectionJob.class);

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        RedisConnectionFactory factory = stringRedisTemplate.getConnectionFactory();
        if(factory == null){
            logger.error("stringRedisTemplate无法获取RedisConnectionFactory");
            return;
        }

        String pong = factory.getConnection().ping();

        if(StringUtils.isBlank(pong) || !pong.equals("PONG")){
            logger.error("Redis连接异常！");
        }else {
            logger.debug("Redis正常连接");
        }

    }
}
