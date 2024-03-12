package com.nowcoder.community.service.joint;

import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author : Zhou Xiang
 * @date : 2024/2/18  17:32
 * @description : 统计网站的数据的方法
 **/
@Service
public interface DataService {
    /**
     * @Author Zhou Xiang
     * @Description 在uv中添加某一个ip来过
     * @Date 2024/2/18 17:33
     * @param ip 要添加的ip
     **/
    void recordUV(String ip);

    /**
     * @Author Zhou Xiang
     * @Description 计算某一段时间内的uv
     * @Date 2024/2/18 17:36
     * @param start 起始时间
     * @param end 终止时间
     * @return long 统计出uv数量，如果返回-1，说明start的时间大于end时间
     **/
    long calculateUV(Date start, Date end);

    /**
     * @Author Zhou Xiang
     * @Description 在dau中添加一个用户来过
     * @Date 2024/2/18 17:42
     * @param userId 该用户的id
     **/
    void recordDAU(int userId);

    /**
     * @Author Zhou Xiang
     * @Description 计算某一段时间内的dau，如果返回-1，说明start的时间大于end时间
     * @Date 2024/2/18 17:43
     * @param start 起始时间
     * @param end 结束时间
     * @return long 统计出的dau数量
     **/
    long calculateDAU(Date start, Date end);
}
