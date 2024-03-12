package com.nowcoder.community.service.iml;

import com.nowcoder.community.entity.Event;
import com.nowcoder.community.config.messageQueue.Producer;
import com.nowcoder.community.service.joint.FollowService;
import com.nowcoder.community.service.joint.UserService;
import com.nowcoder.community.service.vo.FolloweeUserVo;
import com.nowcoder.community.service.vo.FollowerVo;
import com.nowcoder.community.util.RedisKeyUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static com.nowcoder.community.util.Constants.ENTITY_USER;
import static com.nowcoder.community.util.Constants.TOPIC_TAG_NOTICE_FOLLOW;


/**
 * @author : Zhou Xiang
 * @date : 2024/2/1  21:28
 * @description :
 **/
@Service
class FollowServiceImp implements FollowService {
    @Autowired
    private RedisKeyUtils redisKeyUtils;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private Producer producer;

    @Autowired
    private UserService userService;

    @Override
    public void follow(int userId, int entityType, int entityId) {
        String followeeKey = redisKeyUtils.getFolloweeKey(userId, entityType);
        String followerKey = redisKeyUtils.getFollowerKey(entityType, entityId);

        stringRedisTemplate.execute(new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                long now = System.currentTimeMillis();
                operations.multi();
                operations.opsForZSet().add(followeeKey, String.valueOf(entityId), now);
                operations.opsForZSet().add(followerKey, String.valueOf(userId), now);
                return operations.exec();
            }
        });

        //关注之后要发系统消息
        Event event = new Event();
        event.setTopicWithTag(TOPIC_TAG_NOTICE_FOLLOW);
        event.setEntityType(entityType);
        event.setEntityId(entityId);
        event.setUserId(userId);
        //暂时只有关注人的功能
        if(entityType == ENTITY_USER){
            event.setEntityUserId(entityId);
        }
        producer.produce(event);

    }

    @Override
    public void unfollow(int userId, int entityType, int entityId) {
        String followeeKey = redisKeyUtils.getFolloweeKey(userId, entityType);
        String followerKey = redisKeyUtils.getFollowerKey(entityType, entityId);

        stringRedisTemplate.execute(new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                operations.multi();
                operations.opsForZSet().remove(followeeKey, String.valueOf(entityId));
                operations.opsForZSet().remove(followerKey, String.valueOf(userId));
                return operations.exec();
            }
        });
    }

    @Override
    public long getFolloweeCount(int userId, int entityType) {
        String followeeKey = redisKeyUtils.getFolloweeKey(userId, entityType);
        return stringRedisTemplate.opsForZSet().zCard(followeeKey);
    }

    @Override
    public long getFollowerCount(int entityType, int entityId) {
        String followerKey = redisKeyUtils.getFollowerKey(entityType, entityId);
        return stringRedisTemplate.opsForZSet().zCard(followerKey);
    }

    @Override
    public boolean isFollowerOfFollowee(int userId, int entityType, int entityId) {
        String followeeKey = redisKeyUtils.getFolloweeKey(userId, entityType);
        return stringRedisTemplate.opsForZSet().score(followeeKey, String.valueOf(entityId)) != null;
    }

    @Override
    public List<FolloweeUserVo> getFolloweeUsers(int userId, int offset, int limit) {
        String followeeKey = redisKeyUtils.getFolloweeKey(userId, ENTITY_USER);
        List<FolloweeUserVo> followeeUserVos = new ArrayList<>();
        //这里的range是包括后面的end的
        //这里Set的实现类是RedisTemplate自己实现的，所以可以兼容排序的数据
        Set<String> ids = stringRedisTemplate.opsForZSet().reverseRange(followeeKey, offset, offset + limit - 1);
        if(ids == null){
            return null;
        }

        for (String id : ids) {
            Double score = stringRedisTemplate.opsForZSet().score(followeeKey, id);
            FolloweeUserVo followeeUserVo = new FolloweeUserVo();
            followeeUserVo.setUser(userService.findUserById(Integer.parseInt(id)));
            followeeUserVo.setFollowTime(new Date(score.longValue()));
            followeeUserVos.add(followeeUserVo);
        }
        return followeeUserVos;
    }

    @Override
    public List<FollowerVo> getFollowers(int userId, int offset, int limit) {
        String followerKey = redisKeyUtils.getFollowerKey(ENTITY_USER, userId);
        List<FollowerVo> followerVos = new ArrayList<>();

        Set<String> ids = stringRedisTemplate.opsForZSet().reverseRange(followerKey, offset, offset + limit - 1);
        if(ids == null){
            return null;
        }

        for (String id : ids) {
            Double score = stringRedisTemplate.opsForZSet().score(followerKey, id);
            FollowerVo followerVo = new FollowerVo();
            followerVo.setUser(userService.findUserById(Integer.parseInt(id)));
            followerVo.setFollowTime(new Date(score.longValue()));
            followerVos.add(followerVo);
        }
        return followerVos;
    }
}
