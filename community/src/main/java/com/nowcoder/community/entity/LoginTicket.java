package com.nowcoder.community.entity;

import java.util.Date;

/**
 * @author : Zhou Xiang
 * @date : 2023/12/4  21:16
 * @description :存放登录凭证表的信息，对应mysql的login_ticket表
 **/
public class LoginTicket {
    @Deprecated
    private int id;
    private int userId;
    private String ticket;
    //标识是否该ticket是否失效，0表示有效，1表示失效
    private int status;
    private Date expired;

    public int getId() {
        return id;
    }

    @Deprecated
    public void setId(int id) {
        this.id = id;
    }

    @Deprecated
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getExpired() {
        return expired;
    }

    public void setExpired(Date expired) {
        this.expired = expired;
    }

    @Override
    public String toString() {
        return "LoginTicket{" +
                "id=" + id +
                ", userId=" + userId +
                ", ticket='" + ticket + '\'' +
                ", status=" + status +
                ", expired=" + expired +
                '}';
    }
}
