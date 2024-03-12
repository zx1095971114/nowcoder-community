package com.nowcoder.community.controller;

import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.controller.vo.ScreenFolloweeVo;
import com.nowcoder.community.controller.vo.ScreenFollowerVo;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.joint.FollowService;
import com.nowcoder.community.service.joint.UserService;
import com.nowcoder.community.service.vo.FolloweeUserVo;
import com.nowcoder.community.service.vo.FollowerVo;
import com.nowcoder.community.util.CommonUtils;
import com.nowcoder.community.util.Constants;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

import static com.nowcoder.community.util.Constants.ENTITY_USER;

/**
 * @author : Zhou Xiang
 * @date : 2024/2/1  22:04
 * @description :
 **/
@Controller
public class FollowController {
    @Autowired
    private FollowService followService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @RequestMapping(path = "follow", method = RequestMethod.POST)
    @ResponseBody
    @LoginRequired
    public String follow(Model model, int entityType, int entityId){
        User host = hostHolder.getUser();
        //不能关注自己
        if(entityType == ENTITY_USER && entityId == host.getId()){
            return CommonUtils.getJSONString(201, "不能关注自己");
        }

        followService.follow(host.getId(), entityType, entityId);

        return CommonUtils.getJSONString(200, "关注成功！");
    }

    @RequestMapping(path = "unfollow", method = RequestMethod.POST)
    @ResponseBody
    @LoginRequired
    public String unfollow(Model model, int entityType, int entityId){
        User host = hostHolder.getUser();
        followService.unfollow(host.getId(), entityType, entityId);

        return CommonUtils.getJSONString(200, "取关成功！");
    }

    @RequestMapping(path = "followees/{userId}", method = RequestMethod.GET)
    public String getFollowees(@PathVariable("userId") int userId, Page page, Model model){
        page.setLimit(5);
        page.setPath("/followees/" + userId);
        page.setRecordsCount((int) followService.getFolloweeCount(userId, ENTITY_USER));

        User user = userService.findUserById(userId);
        model.addAttribute("user", user);

        User host = hostHolder.getUser();
        List<ScreenFolloweeVo> followees = new ArrayList<>();
        List<FolloweeUserVo> followeeUsers = followService.getFolloweeUsers(userId, page.getOffset(), page.getLimit());
        for (FolloweeUserVo followeeUser : followeeUsers) {
            ScreenFolloweeVo followee = new ScreenFolloweeVo();
            followee.setUser(followeeUser.getUser());
            followee.setFollowTime(followeeUser.getFollowTime());
            boolean followed = false;
            if(host != null){
                followed = followService.isFollowerOfFollowee(host.getId(), ENTITY_USER, followeeUser.getUser().getId());
            }
            followee.setFollowed(followed);

            followees.add(followee);
        }
        model.addAttribute("followees", followees);

        return "site/followee";
    }

    @RequestMapping(path = "followers/{userId}", method = RequestMethod.GET)
    public String getFollowers(@PathVariable("userId") int userId, Page page, Model model){
        page.setLimit(5);
        page.setPath("/followers/" + userId);
        page.setRecordsCount((int) followService.getFollowerCount(ENTITY_USER, userId));

        User user = userService.findUserById(userId);
        model.addAttribute("user", user);

        User host = hostHolder.getUser();
        List<ScreenFollowerVo> followers = new ArrayList<>();
        List<FollowerVo> followerUsers = followService.getFollowers(userId, page.getOffset(), page.getLimit());
        for (FollowerVo followerVo : followerUsers) {
            ScreenFollowerVo follower = new ScreenFollowerVo();
            follower.setUser(followerVo.getUser());
            follower.setFollowTime(followerVo.getFollowTime());
            boolean followed = false;
            if(host != null){
                followed = followService.isFollowerOfFollowee(host.getId(), ENTITY_USER, followerVo.getUser().getId());
            }
            follower.setFollowed(followed);

            followers.add(follower);
        }
        model.addAttribute("followers", followers);

        return "site/follower";
    }
}
