package com.nowcoder.community.service.iml;

import com.nowcoder.community.service.joint.DataService;
import com.nowcoder.community.util.RedisKeyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author : Zhou Xiang
 * @date : 2024/2/18  22:13
 * @description : DataService的实现类
 **/
@Service
class DataServiceImp implements DataService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisKeyUtils redisKeyUtils;

    @Override
    public void recordUV(String ip) {
        String uvKey = redisKeyUtils.getUV(new Date());
        stringRedisTemplate.opsForHyperLogLog().add(uvKey, ip);
    }

    @Override
    public long calculateUV(Date start, Date end) {
        if(start.equals(end)){
            String uvStart = redisKeyUtils.getUV(start);
            return stringRedisTemplate.opsForHyperLogLog().size(uvStart);
        }else{
            String key = redisKeyUtils.getUV(start, end);
            if(key == null){
                return -1;
            }else {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(start);
                List<String> originKeys = new ArrayList<>();
                while (!calendar.getTime().after(end)){
                    String originKey = redisKeyUtils.getUV(calendar.getTime());
                    originKeys.add(originKey);
                    calendar.add(Calendar.DATE, 1);
                }

                Long num = stringRedisTemplate.opsForHyperLogLog().union(key, originKeys.toArray(String[]::new));
                return num;
            }
        }
    }

    @Override
    public void recordDAU(int userId) {
        String key = redisKeyUtils.getDAU(new Date());
        stringRedisTemplate.opsForValue().setBit(key, userId, true);
    }

    @Override
    public long calculateDAU(Date start, Date end) {
        if (start.equals(end)) {
            //start 和 end相同，说明就是查一天的bitMap，直接给就行
            Long result = stringRedisTemplate.execute(new RedisCallback<Long>() {
                @Override
                public Long doInRedis(RedisConnection connection) throws DataAccessException {
                    String endKey = redisKeyUtils.getDAU(start);
                    return connection.bitCount(endKey.getBytes());
                }
            });

            return result == null ? -1 : result;
        } else {
            //不相等，说明要查几天的，所以要先收集要查的天数，再合并
            String endKey = redisKeyUtils.getDAU(start, end);
            if (endKey == null) {
                return -1;
            } else {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(start);
                List<byte[]> originKeys = new ArrayList<>();
                while (!calendar.getTime().after(end)) {
                    String originKey = redisKeyUtils.getDAU(calendar.getTime());
                    originKeys.add(originKey.getBytes());
                    calendar.add(Calendar.DATE, 1);
                }

                Long result = stringRedisTemplate.execute(new RedisCallback<Long>() {
                    @Override
                    public Long doInRedis(RedisConnection connection) throws DataAccessException {
                        String key = redisKeyUtils.getDAU(start, end);
                        connection.bitOp(RedisStringCommands.BitOperation.OR, key.getBytes(), originKeys.toArray(byte[][]::new));
                        return connection.bitCount(key.getBytes());
                    }
                });

                return result == null ? -1 : result;
            }
        }
    }
}
