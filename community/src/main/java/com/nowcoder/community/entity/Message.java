package com.nowcoder.community.entity;


import lombok.ToString;
import org.apache.commons.lang3.builder.ToStringExclude;

import java.util.Date;

@ToString
public class Message {

  private int id;
  private int fromId;
  private int toId;
  //为conversation发生对象id的拼接，如111_112注意小的在前;如果是系统消息这里存的就是topic:tag的名称
  private String conversationId;
  //如果是系统消息，就是event中，没存在表里的字段的json字符串
  private String content;
  private int status;
  private Date createTime;


  public int getId() {
    return id;
  }


  public int getFromId() {
    return fromId;
  }

  public void setFromId(int fromId) {
    this.fromId = fromId;
  }


  public int getToId() {
    return toId;
  }

  public void setToId(int toId) {
    this.toId = toId;
  }


  public String getConversationId() {
    return conversationId;
  }

  public void setConversationId(String conversationId) {
    this.conversationId = conversationId;
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

  public void setCreateTime(Date createTime) {
    this.createTime = createTime;
  }

}
