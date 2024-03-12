package com.nowcoder.community.service.joint;

import com.nowcoder.community.entity.DiscussPost;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author : Zhou Xiang
 * @date : 2024/2/11  19:58
 * @description : 查询服务
 **/
@Service
public interface ElasticsearchService {
    /**
     * @Author Zhou Xiang
     * @Description 向elasticsearch的discuss_post序列中添加一条新数据
     * @Date 2024/2/11 20:03
     * @param discussPost 保存的帖子
     * @return void
     **/
    void saveDiscussPost(DiscussPost... discussPost);

    /**
     * @Author Zhou Xiang
     * @Description  向elasticsearch的discuss_post序列中删除一条数据
     * @Date 2024/2/11 20:06
     * @param discussPostId
     * @return void
     **/
    void deleteDiscussPost(int discussPostId);

    /**
     * @Author Zhou Xiang
     * @Description 在discussPost中查询
     * @Date 2024/2/11 20:09
     * @param keyword 查询关键字
     * @param currentPage 当前页面数，从1开始
     * @param limit 每页有多少条数据
     * @return java.util.List<com.nowcoder.community.entity.DiscussPost>
     **/
    List<DiscussPost> searchDiscussPost(String keyword, int currentPage, int limit);
}
