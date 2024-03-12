package com.nowcoder.community.controller;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Message;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.service.joint.DiscussService;
import com.nowcoder.community.service.joint.LikeService;
import com.nowcoder.community.service.joint.MessageService;
import com.nowcoder.community.service.joint.UserService;
import com.nowcoder.community.util.Constants;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {
    @Autowired
    private DiscussService discussService;
    @Autowired
    private UserService userService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private LikeService likeService;

    @RequestMapping(path = "/", method = RequestMethod.GET)
    public String getRoot(){
        return "forward:/index";
    }

    @RequestMapping(path = "/index", method = RequestMethod.GET)
    public String getIndexPage(Model model, Page page,
                               @RequestParam(name = "orderMode", defaultValue = "0") int orderMode){
        page.setRecordsCount(discussService.findDiscussPostCount());
        page.setPath("/index?orderMode=" + orderMode);
        List<DiscussPost> discussPostList = discussService.findDiscussPost(page.getOffset(), page.getLimit(), orderMode);
        //将每一个帖子的发帖用户和帖子组合起来，放到一个map中，实际相当于创建了一个有DiscussPost和User属性的类
        List<Map<String, Object>> userAndDiscussList = new ArrayList<>();
        for(DiscussPost discussPost : discussPostList){
            Map<String, Object> userAndPost = new HashMap<>();
            userAndPost.put("discussPost", discussPost);
            userAndPost.put("user", userService.findUserById(discussPost.getUserId()));

            userAndPost.put("likeCount", likeService.getLikeCount(Constants.ENTITY_DISCUSS_POST, discussPost.getId()));
            userAndDiscussList.add(userAndPost);
        }
        model.addAttribute("userAndDiscussList", userAndDiscussList);
        model.addAttribute("page", page);
        model.addAttribute("orderMode", orderMode);

        return "index";
    }

    //返回500的错误页面
    @RequestMapping(path = "error", method = RequestMethod.GET)
    public String get500(){
        return "error/500";
    }
}