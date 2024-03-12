package com.nowcoder.community;

import org.junit.jupiter.api.Test;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

/**
 * @author : Zhou Xiang
 * @date : 2024/2/20  0:32
 * @description :
 **/
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class QuartzTest {
    @Autowired
    private Scheduler scheduler;

    // 测试删除方法
    @Test
    public void testDelete() throws SchedulerException {
        System.out.println(scheduler.deleteJob(new JobKey("redisConnectionJob", "communityJobGroup")));
    }
}
