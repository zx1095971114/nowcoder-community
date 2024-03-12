package com.nowcoder.community.quartz;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.service.joint.DiscussService;
import com.nowcoder.community.service.joint.ElasticsearchService;
import com.nowcoder.community.service.joint.LikeService;
import com.nowcoder.community.util.Constants;
import com.nowcoder.community.util.RedisKeyUtils;
import org.apache.commons.lang3.StringUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author : Zhou Xiang
 * @date : 2024/2/20  20:06
 * @description :
 **/
@Component
public class PostScoreRefreshJob implements Job {
    private static final Logger logger = LoggerFactory.getLogger(PostScoreRefreshJob.class);
    @Autowired
    private RedisKeyUtils redisKeyUtils;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private ElasticsearchService elasticsearchService;
    @Autowired
    private DiscussService discussService;
    @Autowired
    private LikeService likeService;

    //牛客纪元，就是牛客网开始运营的时间
    private static final Date epoch;

    //初始化牛客纪元
    static {
        try {
            epoch = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2014-08-01 00:00:00");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        String key = redisKeyUtils.getPostScore();
        BoundSetOperations<String, String> operations = stringRedisTemplate.boundSetOps(key);

        logger.info("[帖子算分任务开始]: 共" + operations.size() + "条帖子要重新算分");
        /*刷新mysql中帖子的分数
        帖子分数计算公式: 分数 = lg(精华分(加精就加75) + 评论数 * 10 + 点赞数 * 2) + (发布时间 - 牛客纪元)(按天数来算)
         */
        //用来存更新的帖子，以同步到elasticsearch中，只用一步而不是每更新一个都要重新同步
        List<DiscussPost> list = new ArrayList<>();
        while (operations.size() > 0){
            String idString = operations.pop();
            if(StringUtils.isBlank(idString)){
                logger.error("查到的要更新的id为空");
                continue;
            }
            DiscussPost post = discussService.findDiscussPostById(Integer.parseInt(idString));
            if(post == null){
                logger.info("要更新的帖子" + idString + "已经被删除了！");
            }else {
                double score = calculateScore(post);
                discussService.updateScore(post.getId(), score);
                post.setScore(score);
                list.add(post);
            }
        }

        if(!list.isEmpty()){
            elasticsearchService.saveDiscussPost(list.toArray(DiscussPost[]::new));
        }
        logger.info("[帖子算分任务结束]: 帖子算分的任务完成！");

    }

    /*给一个帖子，计算它的分数
    帖子分数计算公式: 分数 = lg(精华分(加精就加75) + 评论数 * 10 + 点赞数 * 2) + (发布时间 - 牛客纪元)(按天数来算)
     */
    private double calculateScore(@NonNull DiscussPost discussPost){
        boolean wonderful = (discussPost.getStatus() == 1);
        int commentCount = discussPost.getCommentCount();
        long likeCount = likeService.getLikeCount(Constants.ENTITY_DISCUSS_POST, discussPost.getId());
        Date createTime = discussPost.getCreateTime();

        long inLog = (wonderful ? 75 : 0) + (long)commentCount * 10 + likeCount * 2;
        //注意，算log的时候要求真数(lgb中的b为真数，10为底数)不能为0
        double score = Math.log10(Math.max(1, inLog))
                + (double) (createTime.getTime() - epoch.getTime()) / (1000 * 3600 * 24);

        return score;
    }

}
