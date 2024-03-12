package com.nowcoder.community;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.service.joint.DiscussService;
import com.nowcoder.community.util.RedisKeyUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.BitFieldSubCommands;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * @author : Zhou Xiang
 * @date : 2024/2/17  17:13
 * @description : redis的测试类
 **/
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class RedisTest {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private RedisKeyUtils redisKeyUtils;
    @Autowired
    private DiscussService discussService;

    /* Hyperloglog类型用于统计基数，所谓基数，就是一组数据中的每一个不重复的数据组成的集合
        比如，数据[1,1,3,5,5,7,7,7,9]，其基数就是[1,3,5,7,9],它用来对其进行统计，
        注意Hyperloglog本质是一种算法，它并没有精确地存储每一个输入的值，它可以保证无论输入多少数据(最多2^64个不同基数)，
        总能在12K(Hyperloglog算法必定占12K，但是redis对其做了优化，在计数比较小时，它的存储空间采用稀疏矩阵存储，空间占用很小，
        仅仅在计数慢慢变大，稀疏矩阵占用空间渐渐超过了阈值时才会一次性转变成稠密矩阵，才会占用 12k 的空间。)
        的内存消耗下存下这些数据，代价是统计的误差率在0.81%
        其主要应用之一是统计网站的UV(独立访客，unique visitor，就是每个人访问这个网站只被统计一次)
    */
    @Test
    public void hyperloglogTest(){
        String key = "hyperloglog:01";
        Random random = new Random();

        List<String> list = new ArrayList<>();
        for (int i = 0; i < 100000; i++) {
            list.add(String.valueOf(i));
            int num = random.nextInt(0, 100000);
            list.add(String.valueOf(num));
        }

        stringRedisTemplate.opsForHyperLogLog().add(key, list.toArray(new String[0]));
        System.out.println(stringRedisTemplate.opsForHyperLogLog().size(key));
    }

    @Test
    public void testUnion(){
        List<String> list = new ArrayList<>();
        String key2 = "hyperloglog:02";
        for (int i = 0; i < 10000; i++) {
            list.add(String.valueOf(i));
        }
        stringRedisTemplate.opsForHyperLogLog().add(key2, list.toArray(new String[0]));

        list.clear();
        String key3 = "hyperloglog:03";
        for (int i = 5000; i < 15000; i++) {
            list.add(String.valueOf(i));
        }
        stringRedisTemplate.opsForHyperLogLog().add(key3, list.toArray(new String[0]));

        list.clear();
        String key4 = "hyperloglog:04";
        for (int i = 10000; i < 20000; i++) {
            list.add(String.valueOf(i));
        }
        stringRedisTemplate.opsForHyperLogLog().add(key4, list.toArray(new String[0]));

        String keyUnion = "hyperloglog:union";
        System.out.println(stringRedisTemplate.opsForHyperLogLog().union(keyUnion, key2, key3, key4));

//        System.out.println(stringRedisTemplate.opsForHyperLogLog().size(keyUnion));
    }

    /* * bitMap不是redis的一种特殊支持类型，它本质是string类型，可以理解为每个元素只占1bit的bit数组
    bitMap的个元素只有两种状态，一种是1，一种是0，所以就可以理解为一个类似于[0,1,1,1,0]这样的数组，
    0对应false，1对应true，典型应用就是签到活动，
    * */
    @Test
    public void testBitMap(){
        String key = "bitMap:01";
        //记录，注意，这里的offset是从0开始的，没有设置的位会被初始化为true
        System.out.println(stringRedisTemplate.opsForValue().setBit(key, 1, true));
        stringRedisTemplate.opsForValue().setBit(key, 3, true);
        stringRedisTemplate.opsForValue().setBit(key, 5, true);

        //查询
        System.out.println(stringRedisTemplate.opsForValue().getBit(key, 1));
        System.out.println(stringRedisTemplate.opsForValue().getBit(key, 3));
        System.out.println(stringRedisTemplate.opsForValue().getBit(key, 4));



        //统计
        Integer result = stringRedisTemplate.execute(new RedisCallback<Integer>() {
            @Override
            public Integer doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.bitCount(key.getBytes()).intValue();
            }
        });
        System.out.println(result);
    }

    //可以对bitmap做与，或非的运算
    @Test
    public void testBitMapOr(){
        String key2 = "bitMap:02";
        stringRedisTemplate.opsForValue().setBit(key2, 0, true);
        stringRedisTemplate.opsForValue().setBit(key2, 1, true);
        stringRedisTemplate.opsForValue().setBit(key2, 2, true);

        String key3 = "bitMap:03";
        stringRedisTemplate.opsForValue().setBit(key3, 2, true);
        stringRedisTemplate.opsForValue().setBit(key3, 3, true);
        stringRedisTemplate.opsForValue().setBit(key3, 4, true);

        String key4 = "bitMap:04";
        stringRedisTemplate.opsForValue().setBit(key4, 4, true);
        stringRedisTemplate.opsForValue().setBit(key4, 5, true);
        stringRedisTemplate.opsForValue().setBit(key4, 6, true);

        String key = "bitMap:ops";
        Integer result = stringRedisTemplate.execute(new RedisCallback<Integer>() {
            @Override
            public Integer doInRedis(RedisConnection connection) throws DataAccessException {
                Long aLong = connection.bitOp(RedisStringCommands.BitOperation.OR, key.getBytes(), key2.getBytes(), key3.getBytes(), key4.getBytes());
//                return aLong.intValue();
                return connection.bitCount(key.getBytes()).intValue();
            }
        });
        System.out.println(result);

        for (int i = 0; i < 7; i++) {
            System.out.println(stringRedisTemplate.opsForValue().getBit(key, i));
        }
    }

    @Test
    public void rename(){
        Set<String> keys = stringRedisTemplate.keys("community:*");

        for (String key : keys) {
            String newKey = key.substring(10);
            stringRedisTemplate.rename(key, newKey);
//            System.out.println(newKey);
        }
    }

    @Test
    public void test(){
        String key = redisKeyUtils.getPostScore();
        List<DiscussPost> discussPosts = discussService.findDiscussPost(0, 10000, 0);
        List<String> ids = new ArrayList<>();
        for (DiscussPost discussPost : discussPosts) {
            ids.add(String.valueOf(discussPost.getId()));
        }

        stringRedisTemplate.opsForSet().add(key, ids.toArray(String[]::new));
    }
}
