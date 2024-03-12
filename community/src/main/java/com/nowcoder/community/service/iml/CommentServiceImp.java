package com.nowcoder.community.service.iml;

import com.nowcoder.community.dao.CommentMapper;
import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.entity.Event;
import com.nowcoder.community.config.messageQueue.Producer;
import com.nowcoder.community.service.joint.CommentService;
import com.nowcoder.community.util.Constants;
import com.nowcoder.community.util.RedisKeyUtils;
import com.nowcoder.community.util.SensitiveFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Date;
import java.util.List;

/**
 * @author : Zhou Xiang
 * @date : 2023/12/15  17:17
 * @description : commentService的实现类
 **/
@Service
class CommentServiceImp implements CommentService, Constants {
    private static final Logger logger = LoggerFactory.getLogger(CommentServiceImp.class);

    private final CommentMapper commentMapper;

    private final DiscussPostMapper discussPostMapper;

    private final SensitiveFilter sensitiveFilter;

    private final Producer producer;

    private final TransactionTemplate transactionTemplate;

    private final RedisKeyUtils redisKeyUtils;

    private final StringRedisTemplate stringRedisTemplate;

    public CommentServiceImp(CommentMapper commentMapper, SensitiveFilter sensitiveFilter,
                             DiscussPostMapper discussPostMapper, Producer producer,
                             TransactionTemplate transactionTemplate, RedisKeyUtils redisKeyUtils,
                             StringRedisTemplate stringRedisTemplate) {
        this.commentMapper = commentMapper;
        this.sensitiveFilter = sensitiveFilter;
        this.discussPostMapper = discussPostMapper;
        this.producer = producer;
        this.transactionTemplate = transactionTemplate;
        this.redisKeyUtils = redisKeyUtils;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public List<Comment> findCommentsByEntityId(int entityType, int entityId, int offset, int limit) {
        return commentMapper.selectComments(-1, entityType, entityId, offset, limit);
    }

    @Override
    public int findCommentCountByEntityId(int entityType, int entityId) {
        return commentMapper.selectCommentsCounts(-1, entityType, entityId);
    }

    @Override
//    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public int addComment(int userId, int entityType, int entityId, int targetId, String content, int postId) {
//        long t0 = System.currentTimeMillis();

        Comment comment = new Comment();
        comment.setUserId(userId);
        comment.setEntityType(entityType);
        comment.setEntityId(entityId);
        comment.setTargetId(targetId);
        //存内容之前要敏感词过滤
        content = sensitiveFilter.filterSensitiveWords(content);
        comment.setContent(content);
        comment.setStatus(Constants.COMMENT_NORMAL);
        comment.setCreateTime(new Date());

        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        Integer result = transactionTemplate.execute(new TransactionCallback<Integer>() {
            @Override
            public Integer doInTransaction(TransactionStatus status) {
                int result = commentMapper.insertComment(comment);
                if(entityType == Constants.COMMENT2DISCUSS_POST){
                    int count = commentMapper.selectCommentsCounts(-1, entityType, entityId);
                    discussPostMapper.updateCommentCount(entityId, count);
                }
                return result;
            }
        });
//        long t1 = System.currentTimeMillis();
//        logger.info("更新discuss_post和comment用时" + (double)(t1 - t0) / 1000 + "s");


        if(entityType == Constants.COMMENT2DISCUSS_POST){
            //如果是对帖子的评论，还要更新elasticsearch的数据
            Event event = new Event();
            //这里的entityType是指要更新的是elasticsearch的哪一个索引
            event.setEntityType(Constants.ENTITY_DISCUSS_POST);
            event.setEntityId(entityId);
            event.setTopicWithTag(Constants.TOPIC_TAG_UPDATE_POST);
            producer.produce(event);

//            long t2 = System.currentTimeMillis();
//            logger.info("发送更新elasticsearch消息耗时" + (double)(t2 - t1) / 1000 + "s");

            //影响到了score，需要加入算score的set中
            String key = redisKeyUtils.getPostScore();
            stringRedisTemplate.opsForSet().add(key, String.valueOf(entityId));

//            long t3 = System.currentTimeMillis();
//            logger.info("向redis中加入算score的id耗时" + (double)(t3 - t2) / 1000 + "s");

        }
//        long t4 = System.currentTimeMillis();

        //系统消息
        Event event = new Event();
        event.setTopicWithTag(TOPIC_TAG_NOTICE_COMMENT);
        event.setUserId(userId);
        event.setEntityType(entityType);
        event.setEntityId(entityId);
        event.setEntityUserId(targetId);
        event.setContentAttribute("postId", postId);
        producer.produce(event);

//        long t5 = System.currentTimeMillis();
//        logger.info("发送系统消息耗时" + (double)(t5 - t4) / 1000 + "s");

        return result;
    }

    @Override
    public List<Comment> findComments2PostByUserId(int userId, int offset, int limit) {
        return commentMapper.selectComments(userId, COMMENT2DISCUSS_POST, -1, offset, limit);
    }

    @Override
    public int findComment2PostCountByUserId(int userId) {
        return commentMapper.selectCommentsCounts(userId, COMMENT2DISCUSS_POST, -1);
    }
}
