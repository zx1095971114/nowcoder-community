package com.nowcoder.community.config;

import com.nowcoder.community.quartz.AlphaJob;
import com.nowcoder.community.quartz.PostScoreRefreshJob;
import com.nowcoder.community.quartz.RedisConnectionJob;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

/**
 * @author : Zhou Xiang
 * @date : 2024/2/19  22:24
 * @description : 在application.properties中配置后会存到远程的数据库中，否则会直接存在本地内存中、
 **/
@Configuration
public class QuartzConfig {
    /*配置JobDetail
    这里虽然配置的是JobDetail，但我这里声明的是JobDetailFactoryBean，JobDetail是通过JobDetailFactoryBean来配置的，
    里面封装了JobDetail的实例化过程，
    将*FactoryBean通过@Bean注入spring容器，就可以通过@Autowired在其他地方注入*
     */
//    @Bean
    public JobDetailFactoryBean alphaJobDetail(){
        JobDetailFactoryBean jobDetailFactoryBean = new JobDetailFactoryBean();
        jobDetailFactoryBean.setJobClass(AlphaJob.class);
        jobDetailFactoryBean.setName("alphaJobDetail");
        jobDetailFactoryBean.setGroup("alphaJobGroup");
        //是否会长久保存
        jobDetailFactoryBean.setDurability(true);
        //是否可恢复
        jobDetailFactoryBean.setRequestsRecovery(true);

        return jobDetailFactoryBean;
    }

    /*配置Trigger，有SimpleTrigger或者CronTrigger，前者只能配置简单的Trigger，后者可以配置复杂的Trigger
    JobDetail alphaJobDetail作为参数，如果spring中有许多可以注入的JobDetail，会优先选择名称为"alphaJobDetail"的Bean来装配
    这里的名称为方法的名称、比如这里，前面的方法为alphaJobDetail，所以就直接注入前面的Bean
     */
//    @Bean
    public SimpleTriggerFactoryBean getSimpleTrigger(JobDetail alphaJobDetail){
        SimpleTriggerFactoryBean simpleTriggerFactoryBean = new SimpleTriggerFactoryBean();
        //这里设置对应的jobDetail
        simpleTriggerFactoryBean.setJobDetail(alphaJobDetail);
        simpleTriggerFactoryBean.setName("alphaTrigger");
        simpleTriggerFactoryBean.setGroup("alphaTriggerGroup");
        //这里设置重复的时间间隔
        simpleTriggerFactoryBean.setRepeatInterval(3000);
        // JobDataMap用来存储Job的信息，用默认的实现就行了
        simpleTriggerFactoryBean.setJobDataMap(new JobDataMap());

        return simpleTriggerFactoryBean;
    }

    //配置定时算分的任务的JobDetail
    @Bean
    public JobDetailFactoryBean postScoreRefreshJobDetail(){
        JobDetailFactoryBean jobDetailFactoryBean = new JobDetailFactoryBean();
        jobDetailFactoryBean.setJobClass(PostScoreRefreshJob.class);
        jobDetailFactoryBean.setName("postScoreRefreshJob");
        jobDetailFactoryBean.setGroup("communityJobGroup");
        //是否会长久保存
        jobDetailFactoryBean.setDurability(true);
        //是否可恢复
        jobDetailFactoryBean.setRequestsRecovery(true);

        return jobDetailFactoryBean;
    }

    //配置定时算分的任务的Trigger
    @Bean
    public SimpleTriggerFactoryBean getPostScoreRefreshTrigger(JobDetail postScoreRefreshJobDetail){
        SimpleTriggerFactoryBean simpleTriggerFactoryBean = new SimpleTriggerFactoryBean();
        //这里设置对应的jobDetail
        simpleTriggerFactoryBean.setJobDetail(postScoreRefreshJobDetail);
        simpleTriggerFactoryBean.setName("postScoreRefreshTrigger");
        simpleTriggerFactoryBean.setGroup("communityTriggerGroup");
        //这里设置重复的时间间隔，演示，所以设置一个10min
        simpleTriggerFactoryBean.setRepeatInterval(1000 * 60 * 10);
        // JobDataMap用来存储Job的信息，用默认的实现就行了
        simpleTriggerFactoryBean.setJobDataMap(new JobDataMap());

        return simpleTriggerFactoryBean;
    }


    //配置定时连接redis的任务的JobDetail
//    @Bean
    public JobDetailFactoryBean redisConnectionJobDetail(){
        JobDetailFactoryBean jobDetailFactoryBean = new JobDetailFactoryBean();
        jobDetailFactoryBean.setJobClass(RedisConnectionJob.class);
        jobDetailFactoryBean.setName("redisConnectionJob");
        jobDetailFactoryBean.setGroup("communityJobGroup");
        //是否会长久保存
        jobDetailFactoryBean.setDurability(true);
        //是否可恢复
        jobDetailFactoryBean.setRequestsRecovery(true);

        return jobDetailFactoryBean;
    }

    //配置定时连接Redis的任务的Trigger
//    @Bean
    public SimpleTriggerFactoryBean getRedisConnectionTrigger(JobDetail redisConnectionJobDetail){
        SimpleTriggerFactoryBean simpleTriggerFactoryBean = new SimpleTriggerFactoryBean();
        //这里设置对应的jobDetail
        simpleTriggerFactoryBean.setJobDetail(redisConnectionJobDetail);
        simpleTriggerFactoryBean.setName("redisConnectionTrigger");
        simpleTriggerFactoryBean.setGroup("communityTriggerGroup");
        //这里设置重复的时间间隔，每分钟连接一次就行
        simpleTriggerFactoryBean.setRepeatInterval(1000 * 60);
        // JobDataMap用来存储Job的信息，用默认的实现就行了
        simpleTriggerFactoryBean.setJobDataMap(new JobDataMap());

        return simpleTriggerFactoryBean;
    }

}
