package com.nowcoder.community.dao;

import com.nowcoder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DiscussPostMapper {
    /**
     * @Author Zhou Xiang
     * @Description 根据offset和limit来查DiscussPost的某一些记录的所有属性，如果userId == -1，就是查所有用户的帖子，否则就是查特定用户的帖子
     * @Date 2023/11/30 21:29
     * @param offset 偏移量
     * @param limit 每页条数限制
     * @param userId 用户id，为-1表示查全体成员
     * @param orderMode 排序模式，0表示按时间排，1表示按score排
     * @return java.util.List<com.nowcoder.community.entity.DiscussPost>
     **/
    List<DiscussPost> selectDiscussPost(int userId, int offset, int limit, int orderMode);

    /**
     * @Author Zhou Xiang
     * @Description 查询discussPost的所有记录数
     * @Date 2023/11/30 21:30
     * @Param userId 如果为-1，表示查所有用户的记录数，否则是查某一个用户的
     * @return int
     **/
    int selectDiscussPostNum(int userId);

    /**
     * @Author Zhou Xiang
     * @Description 向discussPost中插入一条discussPost
     * @Date 2023/12/13 14:38
     * @Param void
     * @return int 更改的数据条数
     **/
    int insertDiscussPost(DiscussPost discussPost);

    DiscussPost selectDiscussPostById(int id);

    /**
     * @Author Zhou Xiang
     * @Description 改变commentCount的数量
     * @Date 2023/12/21 17:14
     * @param id 要增加数量的discussPost的id
     * @param count 改变后的commentCount数量
     * @return int 改变的记录条数
     **/
    int updateCommentCount(int id, int count);

    /**
     * @Author Zhou Xiang
     * @Description 改变type的类型，0为普通，1为置顶
     * @Date 2024/2/16 11:36
     * @param id 要改变的discussPost的id
     * @param type 改变后的type
     * @return int 改变的记录条数
     **/
    int updateType(int id, int type);

    /**
     * @Author Zhou Xiang
     * @Description 改变status的类型，0为正常，1为精华，2为拉黑(相当于删除)
     * @Date 2024/2/16 11:38
     * @param id 要改变的discussPost的id
     * @param status 改变后的status
     * @return int 改变的记录条数
     **/
    int updateStatus(int id, int status);

    /**
     * @Author Zhou Xiang
     * @Description 更新帖子的分数
     * @Date 2024/2/20 21:02
     * @param id 要更新的帖子id
     * @param score 更新后的分数
     * @return int 改变记录的条数
     **/
    int updateScore(int id, double score);
}
