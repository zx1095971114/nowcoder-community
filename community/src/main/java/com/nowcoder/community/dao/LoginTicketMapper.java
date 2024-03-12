package com.nowcoder.community.dao;

import com.nowcoder.community.entity.LoginTicket;
import org.apache.ibatis.annotations.*;

/**
 * @author : Zhou Xiang
 * @date : 2023/12/4  21:24
 * @description : 关于login_ticket表的sql操作
 **/
@Mapper
@Deprecated
public interface LoginTicketMapper {
    @Insert({"insert into login_ticket(user_id, ticket, status, expired) " +
            "values(#{userId}, #{ticket}, #{status}, #{expired}) "
    })
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertLoginTicket(LoginTicket loginTicket);

    @Select({"select id, user_id, ticket, status, expired " +
            "from login_ticket " +
            "where ticket = #{ticket} "})
    LoginTicket selectByTicket(String ticket);

    //这里的动态sql没有任何意义，纯粹是为了试一试mybatis在注解中写动态sql
    @Update({"<script> " +
            "update login_ticket " +
            "set status = 1 " +
            "where ticket = #{ticket} " +
            "<if test = \"ticket == #{ticket}\"> " +
            "and 3 = 3" +
            "</if> " +
            "</script> "
    })
    int updateStatus(String ticket);
}
