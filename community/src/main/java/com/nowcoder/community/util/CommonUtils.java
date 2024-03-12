package com.nowcoder.community.util;

import com.alibaba.fastjson2.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author : Zhou Xiang
 * @date : 2023/12/3  10:07
 * @description : 常用的工具类封装
 **/
public class CommonUtils {
    /**
     * @Author Zhou Xiang
     * @Description 生成去掉了"-"的随机字符串,因为使用了UUID，所以可以保证生成的字符串不重复
     * @Date 2023/12/3 10:09
     * @Param void
     * @return java.lang.String
     **/
    public static String generateUUID(){
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * @Author Zhou Xiang
     * @Description 对某字符串进行MD5加密
     * @Date 2023/12/3 10:16
     * @param key
     * @return java.lang.String
     **/
    public static String MD5(String key){
        //这个isBlank可以判断key为null/""/"\n"等
        if(StringUtils.isBlank(key)){
            return null;
        }

        return DigestUtils.md5DigestAsHex(key.getBytes());
    }

    /**
     * @Author Zhou Xiang
     * @Description 获取业务对象的json字符串
     * @Date 2023/12/12 22:32
     * @param code 响应的代码
     * @param msg 响应的信息
     * @param map 响应的其他信息封装在该map中
     * @return java.lang.String 生成的json字符串
     **/
    public static <V> String getJSONString(int code, String msg, Map<String, V> map){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", code);
        jsonObject.put("msg", msg);
        if(map != null){
            jsonObject.putAll(map);
        }

        return jsonObject.toJSONString();
    }

    //getJSONString的重载形式，没有msg和map
    public static String getJSONString(int code){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", code);
        return jsonObject.toJSONString();
    }

    //getJSONString的重载形式，没有map
    public static String getJSONString(int code, String msg){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", code);
        jsonObject.put("msg", msg);
        return jsonObject.toJSONString();
    }


//    public static void main(String[] args) {
//        String a = "/js/a.css";
//        boolean matches = a.matches("(^/(.)*(.css)$)" );
//        System.out.println(matches);
//    }
}
