package com.nowcoder.community.config;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * @author : Zhou Xiang
 * @date : 2023/12/4  16:02
 * @description :Kaptcha的配置类
 **/
@Configuration
public class KaptchaConfig {
    @Bean
    public Producer getKaptchaProducer(){
        Properties properties = new Properties();
        properties.put("kaptcha.image.width", "100");
        properties.put("kaptcha.image.height", "40");
        properties.put("kaptcha.textproducer.font.size", "32");
        properties.put("kaptcha.textproducer.font.color", "black");
        properties.put("kaptcha.textproducer.char.string", "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        properties.put("kaptcha.textproducer.char.length", "4");
        //这里不加额外的噪声，不然部署到服务器还要装依赖
        properties.put("kaptcha.noise.imp", "com.google.code.kaptcha.impl.NoNoise");


        DefaultKaptcha producer = new DefaultKaptcha();
        Config config = new Config(properties);
        producer.setConfig(config);
        return producer;
    }
}
