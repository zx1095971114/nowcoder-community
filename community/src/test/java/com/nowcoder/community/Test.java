package com.nowcoder.community;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

import java.util.Map;

/**
 * @author : Zhou Xiang
 * @date : 2023/12/25  10:52
 * @description :
 **/
public class Test {
    public static void main(String[] args) {
        JSONObject parse = JSONObject.parse("{\"entityType\":2,\"entityId\":92,\"postId\":234,\"userId\":114}");
//        Map<String, Object> map = (Map<String, Object>) parse;
//        int a = 1;
        int entityType = (int) parse.get("entityType");
        int a = 2;
    }
}
