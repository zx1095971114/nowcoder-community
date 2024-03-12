package com.nowcoder.community.quartz;

import org.apache.commons.lang3.StringUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.erhlc.ElasticsearchRestTemplate;
import org.springframework.stereotype.Component;

/**
 * @author : Zhou Xiang
 * @date : 2024/2/22  13:13
 * @description : 周期性连接elasticsearch保持连接
 **/
@Deprecated
//@Component
public class ElasticSearchConnectionJob implements Job {
//    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;
    private static final Logger logger = LoggerFactory.getLogger(ElasticSearchConnectionJob.class);

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        String clusterVersion = elasticsearchTemplate.getClusterVersion();
        if(StringUtils.isBlank(clusterVersion)){
            logger.error("elasticsearch连接失败！");
        }else{
            logger.debug("elasticsearch连接成功，版本: " + clusterVersion);
        }
    }
}
