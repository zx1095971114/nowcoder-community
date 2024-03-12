package com.nowcoder.community.controller;

import com.nowcoder.community.service.joint.DataService;
import com.nowcoder.community.util.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : Zhou Xiang
 * @date : 2024/2/18  23:18
 * @description : 有关数据统计的controller
 **/
@Controller
@RequestMapping(path = "data")
public class DataController {
    @Autowired
    private DataService dataService;

    //统计数据的主页
    @RequestMapping(path = "dataIndex", method = RequestMethod.GET)
    public String getData(){
        return "site/admin/data";
    }

    @RequestMapping(path = "calculateUV", method = RequestMethod.POST)
    @ResponseBody
    public String calculateUV(@DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
                              @DateTimeFormat(pattern = "yyyy-MM-dd") Date end){
        long uvs = dataService.calculateUV(start, end);
        if(uvs == -1){
            return CommonUtils.getJSONString(400, "错误的起止日期!");
        }else {
            Map<String, Long> map = new HashMap<>();
            map.put("uvCount", uvs);
            return CommonUtils.getJSONString(200, "查询成功", map);
        }
    }

    @RequestMapping(path = "calculateDAU", method = RequestMethod.POST)
    @ResponseBody
    public String calculateDAU(@DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
                              @DateTimeFormat(pattern = "yyyy-MM-dd") Date end){
        long daus = dataService.calculateDAU(start, end);
        if(daus == -1){
            return CommonUtils.getJSONString(400, "错误的起止日期!");
        }else {
            Map<String, Long> map = new HashMap<>();
            map.put("dauCount", daus);
            return CommonUtils.getJSONString(200, "查询成功", map);
        }
    }
}
