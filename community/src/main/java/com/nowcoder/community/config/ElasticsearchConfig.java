package com.nowcoder.community.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author : Zhou Xiang
 * @date : 2024/2/10  1:21
 * @description :elasticsearch的配置类，产生装配好的ElasticsearchClient
 **/
@Deprecated
//如果要启用，就把Configuration和Bean注解加上
//这里为了节约性能，暂时不用了
//@Configuration
public class ElasticsearchConfig {
    @Value("${spring.elasticsearch.uris}")
    private String[] urls;

    //根据供访问的url地址(9200端口)，提供相应的访问客户端供调用
    private ElasticsearchClient getElasticsearchClient(String url){
        // URL and API key
        String serverUrl = url;
        //暂时没有设计密码
//        String apiKey = "VnVhQ2ZHY0JDZGJrU...";

        // Create the low-level client
        RestClient restClient = RestClient
                .builder(HttpHost.create(serverUrl))
//                .setDefaultHeaders(new Header[]{
//                        new BasicHeader("Authorization", "ApiKey " + apiKey)
//                })

                .build();

        // Create the transport with a Jackson mapper
        ElasticsearchTransport transport = new RestClientTransport(
                restClient, new JacksonJsonpMapper());

        // And create the API client
        return new ElasticsearchClient(transport);
    }

    /**
     * @Author Zhou Xiang
     * @Description 提供外界的获取elasticsearch客户端的接口
     * @Date 2024/2/10 1:29
     * @Param void
     * @return co.elastic.clients.elasticsearch.ElasticsearchClient
     **/
//    @Bean
    public ElasticsearchClient getElasticsearchClient(){
        //因为暂时只有一个地址，所以就先写死了
        String url = urls[0];
        return getElasticsearchClient(url);
    }
}
