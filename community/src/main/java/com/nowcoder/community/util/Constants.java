package com.nowcoder.community.util;

/**
 * @author : Zhou Xiang
 * @date : 2023/12/3  17:51
 * @description :项目中的常量
 **/
//在interface中，字段默认是public static final的
public interface Constants {
    //激活成功
    int ACTIVATION_SUCCESS = 0;
    //重复激活
    int ACTIVATION_REPEAT = 1;
    //激活失败
    int ACTIVATION_FAILURE = 2;

    //账户已激活的状态码
    int ACTIVATED = 1;
    //账户未激活的状态码
    int NOT_ACTIVATED = 0;

    //用户的登录ticket有效
    int TICKET_VALID = 0;
    //用户的登录ticket无效
    int TICKET_INVALID = 1;

    //默认状态的登录超时时间s
    int DEFAULT_TIMEOUT = 12 * 3600;
    //记住状态的登录超时时间s
    int REMEMBER_ME_TIMEOUT = 30 * 24 * 3600;

    //comment表的entityType
    //对帖子的评论
    int COMMENT2DISCUSS_POST = 1;
    //对评论的评论
    int COMMENT2COMMENT = 2;

    //comment表的status
    //正常
    int COMMENT_NORMAL = 0;
    //被删除
    int COMMENT_DELETED = 1;

    //用户点赞了
    int LIKE = 1;
    //用户没点赞
    int NOT_LIKE = 0;

    //各种点赞，评论实体的代码
    int ENTITY_DISCUSS_POST = 1;
    int ENTITY_COMMENT = 2;
    int ENTITY_USER = 3;

    //消息队列的主题名称
    String TOPIC_TAG_NOTICE_COMMENT = "topic_notice:tag_comment";
    String TOPIC_TAG_NOTICE_LIKE = "topic_notice:tag_like";
    String TOPIC_TAG_NOTICE_FOLLOW = "topic_notice:tag_follow";

    String TOPIC_TAG_UPDATE_POST = "topic_elastic_post:tag_update";

    String TOPIC_TAG_DELETE_POST = "topic_elastic_post:tag_delete";

    //系统用户的id
    int SYSTEM_USER_ID = 1;

    //各种行为的代码
    //点赞
    int BEHAVIOR_LIKE = 1;
    //评论
    int BEHAVIOR_COMMENT = 2;
    //关注
    int BEHAVIOR_FOLLOW = 3;

    //各种用户权限
    //普通用户 0
    String AUTHORITY_USER = "user";
    //版主 2
    String AUTHORITY_MODERATOR = "moderator";
    //管理员 1
    String AUTHORITY_ADMIN = "admin";
}
