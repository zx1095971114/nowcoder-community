package com.nowcoder.community.actuator;

import com.nowcoder.community.util.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * @author : Zhou Xiang
 * @date : 2024/2/23  17:06
 * @description : 新写的通过actuator暴露的endpoint
 **/
@Component
@Endpoint(id = "database")
public class DatabaseEndpoint {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseEndpoint.class);

    @Autowired
    private DataSource dataSource;

    //这个注解就是当通过get请求访问时访问的方法，一般返回的都是json
    @ReadOperation
    public String getDatabaseHealth(){
        try (Connection connection = dataSource.getConnection()) {
            return CommonUtils.getJSONString(200, "数据库连接正常");
        }catch (Exception e){
            logger.error("数据库连接异常: " + e.getMessage());
            return CommonUtils.getJSONString(500, "数据库连接异常！");
        }
    }
}
