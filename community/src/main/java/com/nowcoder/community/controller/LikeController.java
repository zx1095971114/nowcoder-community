package com.nowcoder.community.controller;

import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.joint.LikeService;
import com.nowcoder.community.util.CommonUtils;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : Zhou Xiang
 * @date : 2023/12/23  16:03
 * @description :点赞相关的Controller
 **/
@Controller
public class LikeController {
    @Autowired
    private LikeService likeService;
    @Autowired
    private HostHolder hostHolder;

    //点赞或者取消赞使用异步请求，用户体验会好一点，实际中也不会出现点赞一下就刷新页面的情况
    @RequestMapping(path = "like", method = RequestMethod.POST)
    @ResponseBody
    @LoginRequired
    public String like(int entityType, int entityId, int entityUserId, int postId){
        User host = hostHolder.getUser();

        likeService.like(host.getId(), entityType, entityId, entityUserId, postId);

        //封装返回的信息
        Map<String, Object> map = new HashMap<>();
        //点赞数量
        map.put("likeCount", likeService.getLikeCount(entityType, entityId));
        //用户的点赞状态
        map.put("hostLikeStatus", likeService.getLikeStatus(host.getId(), entityType, entityId));

        return CommonUtils.getJSONString(200, null, map);
    }

}
