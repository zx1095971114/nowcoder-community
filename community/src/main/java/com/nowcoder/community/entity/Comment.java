package com.nowcoder.community.entity;

import java.util.Date;

/**
 * @author : Zhou Xiang
 * @date : 2023/12/15  16:17
 * @description :对应comment表的实体类
 **/
public class Comment {
    private int id;
    private int userId;
    //被评论的实体，1代表对帖子的评论，2代表对评论的评论
    private int entityType;
    //被评论的实体的id
    private int entityId;
    //对评论a的评论b进行回复时，被回复的人(b的主人)的id，若没有，就取0，注意，对评论a的评论b进行回复c时，这条c是属于a而非b的，b不会有自己的评论
    private int targetId;
    private String content;
    //帖子的状态，0代表正常，1代表删除
    private int status;
    private Date createTime;

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getEntityType() {
        return entityType;
    }

    public void setEntityType(int entityType) {
        this.entityType = entityType;
    }

    public int getEntityId() {
        return entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public int getTargetId() {
        return targetId;
    }

    public void setTargetId(int targetId) {
        this.targetId = targetId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date creatTime) {
        this.createTime = creatTime;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", userId=" + userId +
                ", entityType=" + entityType +
                ", entityId=" + entityId +
                ", targetId=" + targetId +
                ", content='" + content + '\'' +
                ", status=" + status +
                ", creatTime=" + createTime +
                '}';
    }
}
