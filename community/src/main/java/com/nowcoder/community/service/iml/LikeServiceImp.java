package com.nowcoder.community.service.iml;

import com.nowcoder.community.entity.Event;
import com.nowcoder.community.config.messageQueue.Producer;
import com.nowcoder.community.service.joint.LikeService;
import com.nowcoder.community.util.Constants;
import com.nowcoder.community.util.RedisKeyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @author : Zhou Xiang
 * @date : 2023/12/23  15:46
 * @description : LikeService的实现类
 **/
@Service
class LikeServiceImp implements LikeService, Constants {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private RedisKeyUtils redisKeyUtils;
    @Autowired
    private Producer producer;

    @Override
    public void like(int userId, int entityType, int entityId, int entityUserId, int postId) {
        //点赞时要同时改变实体的赞和用户的赞
        String entityKey = redisKeyUtils.getEntityLikeKey(entityType, entityId);
        String userKey = redisKeyUtils.getUserLikeKey(entityUserId);
        Boolean isMember = stringRedisTemplate.execute(new SessionCallback<Boolean>() {
            @Override
            public Boolean execute(RedisOperations operations) throws DataAccessException {
                Boolean isMember = operations.opsForSet().isMember(entityKey, String.valueOf(userId));
                operations.multi();
                if (isMember) {
                    operations.opsForSet().remove(entityKey, String.valueOf(userId));
                    operations.opsForValue().decrement(userKey);
                } else {
                    operations.opsForSet().add(entityKey, String.valueOf(userId));
                    operations.opsForValue().increment(userKey);
                }
                operations.exec();
                return isMember;
            }
        });

        //发送系统消息,注意，这里的isMember表示在用户点下点赞按钮之前用户是不是已经点赞了
        //如果已经点赞，就是true，这次是取消
        if(!isMember){
            //生产消息，让系统发送
            Event event = new Event();
            event.setTopicWithTag(TOPIC_TAG_NOTICE_LIKE);
            event.setUserId(userId);
            event.setEntityType(entityType);
            event.setEntityId(entityId);
            event.setEntityUserId(entityUserId);
            event.setContentAttribute("postId", Integer.valueOf(postId));
            producer.produce(event);
        }

        if(entityType == ENTITY_DISCUSS_POST){
            //影响到了score，需要加入算score的set中
            String key = redisKeyUtils.getPostScore();
            stringRedisTemplate.opsForSet().add(key, String.valueOf(entityId));
        }

    }

    @Override
    public long getLikeCount(int entityType, int entityId) {
        String key = redisKeyUtils.getEntityLikeKey(entityType, entityId);
        return stringRedisTemplate.opsForSet().size(key);
    }

    @Override
    public int getLikeStatus(int userId, int entityType, int entityId) {
        String key = redisKeyUtils.getEntityLikeKey(entityType, entityId);
        boolean isMember = stringRedisTemplate.opsForSet().isMember(key, String.valueOf(userId));
        //这里不直接返回boolean是为了以后开发踩一下之类的功能做准备
        return (isMember) ? LIKE : NOT_LIKE;
    }

    @Override
    public long getUserLikeCount(int userId) {
        String userKey = redisKeyUtils.getUserLikeKey(userId);
        if(stringRedisTemplate.hasKey(userKey)){
            return Long.parseLong(stringRedisTemplate.opsForValue().get(userKey));
        }else {
            return 0;
        }
    }
}
