package com.nowcoder.community;

import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.service.joint.DiscussService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

/**
 * @author : Zhou Xiang
 * @date : 2024/2/22  23:13
 * @description : 压力测试
 **/
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class JmeterTest {
    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Test
    public void addData(){
        //特意不加到elasticsearch中，为了删除的时候方便，删300以后得帖子
        DiscussPost discussPost = discussPostMapper.selectDiscussPostById(109);
        for (int i = 0; i <  300000; i++) {
            discussPostMapper.insertDiscussPost(discussPost);
        }
    }
}
