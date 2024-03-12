package com.nowcoder.community.dao;

import com.nowcoder.community.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author : Zhou Xiang
 * @date : 2023/12/22  11:34
 * @description :
 **/
@Mapper
public interface AlphaMapper {
    @Select("select * " +
            "from user"
    )
    List<User> selectUsers();
}
