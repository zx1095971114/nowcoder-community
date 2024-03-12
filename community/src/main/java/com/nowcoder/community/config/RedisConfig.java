package com.nowcoder.community.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @author : Zhou Xiang
 * @date : 2024/2/22  1:16
 * @description :
 **/
//@Configuration
public class RedisConfig {
//    @Primary
//    @Bean
    public StringRedisTemplate getStringRedisTemplate(RedisConnectionFactory redisConnectionFactory){
        LettuceConnectionFactory factory = (LettuceConnectionFactory) redisConnectionFactory;
        factory.setShareNativeConnection(false);
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate(factory);
        return stringRedisTemplate;
    }
}
