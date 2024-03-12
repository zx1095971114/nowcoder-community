package com.nowcoder.community.service.joint;

import com.nowcoder.community.entity.DiscussPost;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DiscussService {
    /**
     * @Author Zhou Xiang
     * @Description 根据偏移量和条数限制查找帖子
     * @Date 2023/11/30 21:03
     * @param offset 偏移量，即从哪一条开始查
     * @param limit 条数限制，限制查多少条
     * @param orderMode 排序方式，0表示按时间从后往前，1表示按score从大到小
     * @return java.util.List<com.nowcoder.community.entity.DiscussPost>
     **/
    List<DiscussPost> findDiscussPost(int offset, int limit, int orderMode);

    /**
     * @Author Zhou Xiang
     * @Description 查询所有帖子数目
     * @Date 2023/11/30 21:32
     * @Param void
     * @return int
     **/
    int findDiscussPostCount();

    /**
     * @Author Zhou Xiang
     * @Description 将发布的帖子存到数据库
     * @Date 2023/12/13 14:55
     * @param title 帖子的标题
     * @param content 帖子的内容
     * @param userId 发帖用户
     * @return int 改变的记录条数
     **/
    int addDiscussPost(String title, String content, int userId);

    /**
     * @Author Zhou Xiang
     * @Description 根据帖子的id查到帖子
     * @Date 2023/12/13 16:52
     * @param id 帖子的id
     * @return com.nowcoder.community.entity.DiscussPost
     **/
    DiscussPost findDiscussPostById(int id);

    /**
     * @Author Zhou Xiang
     * @Description 查找某个用户的帖子
     * @Date 2024/2/3 22:35
     * @param userId 用户id
     * @param offset 偏移量
     * @param limit 限制
     * @param orderMode 排序方式
     * @return java.util.List<com.nowcoder.community.entity.DiscussPost>
     **/
    List<DiscussPost> findDiscussPostByUserId(int userId, int offset, int limit, int orderMode);

    /**
     * @Author Zhou Xiang
     * @Description 查询某个用户的所有帖子总数
     * @Date 2024/2/3 22:51
     * @param userId 用户的id
     * @return int
     **/
    int findDiscussPostCountByUserId(int userId);

    /**
     * @Author Zhou Xiang
     * @Description  将帖子改为置顶
     * @Date 2024/2/16 16:01
     * @param ids 需要置顶的帖子的id
     **/
    void setTop(int... ids);

    /**
     * @Author Zhou Xiang
     * @Description 将帖子取消置顶
     * @Date 2024/2/16 16:04
     * @param ids 需要置顶的帖子的id
     * @return void
     **/
    void setUntop(int... ids);

    /**
     * @Author Zhou Xiang
     * @Description 将帖子加精
     * @Date 2024/2/16 16:06
     * @param ids 要加精的id
     **/
    void setRefinement(int... ids);

    /**
     * @Author Zhou Xiang
     * @Description 取消帖子加精
     * @Date 2024/2/16 16:07
     * @param ids 要取消加精的帖子id
     **/
    void setUnrefinement(int... ids);

    /**
     * @Author Zhou Xiang
     * @Description 拉黑帖子
     * @Date 2024/2/16 16:08
     * @param ids 要设为拉黑的帖子的id
     * @return void
     **/
    void setDeleted(int... ids);

    /**
     * @Author Zhou Xiang
     * @Description 更新帖子的分数
     * @Date 2024/2/20 21:05
     * @param id 要更新的帖子的id
     * @param score 更新后的帖子的分数
     **/
    void updateScore(int id, double score);
}
