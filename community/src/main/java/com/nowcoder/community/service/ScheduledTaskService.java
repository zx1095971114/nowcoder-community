package com.nowcoder.community.service;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * @author : Zhou Xiang
 * @date : 2024/2/22  13:45
 * @description : 每个服务器都要跑的定时任务
 **/
@Service
public class ScheduledTaskService {
    private static final Logger logger = LoggerFactory.getLogger(ScheduledTaskService.class);

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Scheduled(initialDelay = 30000, fixedRate = 60000)
    public void redisConnectionTask(){
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

    @Scheduled(initialDelay = 60000, fixedRate = 60000)
    public void elasticsearchConnectionTask(){
        String clusterVersion = elasticsearchTemplate.getClusterVersion();
        if(StringUtils.isBlank(clusterVersion)){
            logger.error("elasticsearch连接失败！");
        }else{
            logger.debug("elasticsearch连接成功，版本: " + clusterVersion);
        }
    }
}
