package com.nowcoder.community.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.text.SimpleDateFormat;

@Configuration
public class AlphaConfig {
    @Bean
    public SimpleDateFormat getSimpleDateFormat(){
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    @Bean
    @Primary
    public SimpleDateFormat getSimpleDateFormat2(){
        return new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
    }
}
