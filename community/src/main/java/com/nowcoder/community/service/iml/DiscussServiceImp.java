package com.nowcoder.community.service.iml;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.nowcoder.community.config.messageQueue.Producer;
import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Event;
import com.nowcoder.community.service.joint.DiscussService;
import com.nowcoder.community.util.Constants;
import com.nowcoder.community.util.RedisKeyUtils;
import com.nowcoder.community.util.SensitiveFilter;
import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
class DiscussServiceImp implements DiscussService {
    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Autowired
    private SensitiveFilter sensitiveFilter;
    @Autowired
    private Producer producer;
    @Autowired
    private RedisKeyUtils redisKeyUtils;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final Logger logger = LoggerFactory.getLogger(DiscussServiceImp.class);
    private final int TYPE_NORMAL = 0;
    private final int TYPE_TOP = 1;
    private final int STATUS_NORMAL = 0;
    private final int STATUS_ESSENCE = 0;
    private final int STATUS_UNFRIEND = 0;

    @Value("${caffeine.hotPostPages.max-size}")
    private int hotPostMaxSize;
    @Value("${caffeine.hotPostPages.expiredSeconds}")
    private int hotPostExpireSeconds;
    private static final int HOT_POST_REDIS_EXPIRED_SECONDS = 8 * 60;
    @Value("${caffeine.postCount.max-size}")
    private int postCountMaxSize;
    @Value("${caffeine.postCount.expiredSeconds}")
    private int postCountExpireSeconds;
    private static final int POST_COUNT_REDIS_EXPIRED_SECONDS = 8 * 60;
    /*caffeine的核心接口是Cache，它有两个主要实现类LoadingCache, AsyncLoadingCache
    它支持的泛型的第一个参数是标识这个缓存的主键，第二个参数是这个主键对应的缓存值
     */

    //只配热门模式的缓存，其主键为offset:limit，因为时间排序的模式下，变化太快，缓存刷新太频繁，且热门帖子的刷新是可以滞后的
    private LoadingCache<String, List<DiscussPost>> pagesCache;
    //配置了某用户的帖子数的缓存，key为userId，-1表示全体成员，可以配本地缓存是因为5min内页数是不会变化太大的
    private LoadingCache<Integer, Integer> postCountCache;

    //缓存初始化
    @PostConstruct
    public void init(){
        pagesCache = Caffeine.newBuilder()
                .maximumSize(hotPostMaxSize)
                .expireAfterWrite(hotPostExpireSeconds, TimeUnit.SECONDS)
                .build(new CacheLoader<String, List<DiscussPost>>() {
                    //没有数据时获取数据的方法，传的参数是key
                    @Override
                    public List<DiscussPost> load(String key) throws Exception {
                        if(StringUtils.isBlank(key)){
                            logger.error("错误的参数");
                            throw new IllegalArgumentException("错误的参数");
                        }

                        String[] splits = key.split(":");
                        if(splits.length != 2){
                            logger.error("错误的参数");
                            throw new IllegalArgumentException("错误的参数");
                        }

                        int offset = Integer.parseInt(splits[0]);
                        int limit = Integer.parseInt(splits[1]);



                        //二级缓存，查redis
                        String redisKey = redisKeyUtils.getHotPosts(offset, limit);
                        BoundSetOperations<String, String> operations = stringRedisTemplate.boundSetOps(redisKey);
                        if(operations.size() != 0){
                            Set<String> members = operations.members();
                            List<DiscussPost> list = new ArrayList<>();
                            for (String member : members) {
                                DiscussPost discussPost = JSONObject.parseObject(member, DiscussPost.class);
                                list.add(discussPost);
                            }
                            return list;
                        }else{
                            //二级缓存没命中，查DB
                            List<DiscussPost> result = discussPostMapper.selectDiscussPost(-1, offset, limit, 1);
                            //存到redis里面
                            String[] values = new String[result.size()];
                            for (int i = 0; i < result.size(); i++) {
                                values[i] = JSON.toJSONString(result.get(i));
                            }
                            stringRedisTemplate.opsForSet().add(redisKey, values);
                            stringRedisTemplate.expire(redisKey, HOT_POST_REDIS_EXPIRED_SECONDS, TimeUnit.SECONDS);

                            return result;
                        }
                    }
                });

        postCountCache = Caffeine.newBuilder()
                .maximumSize(postCountMaxSize)
                .expireAfterWrite(postCountExpireSeconds, TimeUnit.SECONDS)
                .build(new CacheLoader<Integer, Integer>() {
                    @Override
                    public Integer load(Integer userId) throws Exception {
                        String redisKey = redisKeyUtils.getUserPostCount(userId);
                        String value = stringRedisTemplate.opsForValue().get(redisKey);

                        if(!StringUtils.isBlank(value)){
                            return Integer.valueOf(value);
                        }else{
                            int result = discussPostMapper.selectDiscussPostNum(userId);
                            stringRedisTemplate.opsForValue().set(redisKey, String.valueOf(result));
                            stringRedisTemplate.expire(redisKey, POST_COUNT_REDIS_EXPIRED_SECONDS, TimeUnit.SECONDS);
                            return result;
                        }
                    }
                });
    }

    @Override
    public List<DiscussPost> findDiscussPost(int offset, int limit, int orderMode){
        if(orderMode == 1){
            return pagesCache.get(String.valueOf(offset) + ":" + String.valueOf(limit));
        }
        return discussPostMapper.selectDiscussPost(-1, offset, limit, orderMode);
    }

    @Override
    public int findDiscussPostCount(){
        return postCountCache.get(-1);
//        return discussPostMapper.selectDiscussPostNum(-1);
    }

    @Override
    public int addDiscussPost(String title, String content, int userId) {
        if(StringUtils.isBlank(title) || StringUtils.isBlank(content)){
            return 0;
        }
        //对title和content做html标签转义
        title = HtmlUtils.htmlEscape(title);
        content = HtmlUtils.htmlEscape(content);
        //对title和content做敏感词过滤
        title = sensitiveFilter.filterSensitiveWords(title);
        content = sensitiveFilter.filterSensitiveWords(content);

        DiscussPost discussPost = new DiscussPost();
        discussPost.setUserId(userId);
        discussPost.setTitle(title);
        discussPost.setContent(content);
        discussPost.setType(TYPE_NORMAL);
        discussPost.setStatus(STATUS_NORMAL);
        discussPost.setCreateTime(new Date());
        discussPost.setCommentCount(0);
        discussPost.setScore(0);

        discussPostMapper.insertDiscussPost(discussPost);

        /*用消息队列实现存入elasticsearch中
        这里不把discussPost传入是为了减少信息，加快速度，因为查询内容更新的实时性要求没那么高
        这里不把向mysql中插入数据也放进去是因为mysql的实时性要求更高一点，用户想要立即获得反馈
         */
        Event event = new Event();
        event.setTopicWithTag(Constants.TOPIC_TAG_UPDATE_POST);
        event.setEntityType(Constants.ENTITY_DISCUSS_POST);
        event.setEntityId(discussPost.getId());
        producer.produce(event);

        //影响到了score，需要加入算score的set中
        String key = redisKeyUtils.getPostScore();
        stringRedisTemplate.opsForSet().add(key, String.valueOf(discussPost.getId()));

        return 1;
    }

    @Override
    public DiscussPost findDiscussPostById(int id) {
        return discussPostMapper.selectDiscussPostById(id);
    }

    @Override
    public List<DiscussPost> findDiscussPostByUserId(int userId, int offset, int limit, int orderMode) {
        return discussPostMapper.selectDiscussPost(userId, offset, limit, orderMode);
    }

    @Override
    public int findDiscussPostCountByUserId(int userId) {
        return postCountCache.get(userId);
//        return discussPostMapper.selectDiscussPostNum(userId);
    }

    @Override
    public void setTop(int... ids) {
        for (int id : ids) {
            discussPostMapper.updateType(id, 1);

            //更新elasticsearch中的数据
            Event event = new Event();
            event.setTopicWithTag(Constants.TOPIC_TAG_UPDATE_POST);
            event.setEntityType(Constants.ENTITY_DISCUSS_POST);
            event.setEntityId(id);
            producer.produce(event);
        }
    }

    @Override
    public void setUntop(int... ids) {
        for (int id : ids) {
            discussPostMapper.updateType(id, 0);

            //更新elasticsearch中的数据
            Event event = new Event();
            event.setTopicWithTag(Constants.TOPIC_TAG_UPDATE_POST);
            event.setEntityType(Constants.ENTITY_DISCUSS_POST);
            event.setEntityId(id);
            producer.produce(event);
        }
    }

    @Override
    public void setRefinement(int... ids) {
        for (int id : ids) {
            discussPostMapper.updateStatus(id, 1);

            //更新elasticsearch中的数据
            Event event = new Event();
            event.setTopicWithTag(Constants.TOPIC_TAG_UPDATE_POST);
            event.setEntityType(Constants.ENTITY_DISCUSS_POST);
            event.setEntityId(id);
            producer.produce(event);

            //影响到了score，需要加入算score的set中
            String key = redisKeyUtils.getPostScore();
            stringRedisTemplate.opsForSet().add(key, String.valueOf(id));
        }
    }

    @Override
    public void setUnrefinement(int... ids) {
        for (int id : ids) {
            discussPostMapper.updateStatus(id, 0);

            //更新elasticsearch中的数据
            Event event = new Event();
            event.setTopicWithTag(Constants.TOPIC_TAG_UPDATE_POST);
            event.setEntityType(Constants.ENTITY_DISCUSS_POST);
            event.setEntityId(id);
            producer.produce(event);

            //影响到了score，需要加入算score的set中
            String key = redisKeyUtils.getPostScore();
            stringRedisTemplate.opsForSet().add(key, String.valueOf(id));
        }
    }

    @Override
    public void setDeleted(int... ids) {
        for (int id : ids) {
            discussPostMapper.updateStatus(id, 2);

            //更新elasticsearch中的数据
            Event event = new Event();
            event.setTopicWithTag(Constants.TOPIC_TAG_DELETE_POST);
            event.setEntityType(Constants.ENTITY_DISCUSS_POST);
            event.setEntityId(id);
            producer.produce(event);
        }
    }

    @Override
    public void updateScore(int id, double score) {
        discussPostMapper.updateScore(id, score);
    }


}
