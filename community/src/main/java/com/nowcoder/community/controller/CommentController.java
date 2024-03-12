package com.nowcoder.community.controller;

import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.service.joint.CommentService;
import com.nowcoder.community.service.joint.DiscussService;
import com.nowcoder.community.service.joint.UserService;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : Zhou Xiang
 * @date : 2023/12/21  11:44
 * @description : 与评论有关的controller
 **/
@Controller
@RequestMapping("comment")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private DiscussService discussService;

    @LoginRequired
    @RequestMapping(path = "add_comment/{discussPostId}", method = RequestMethod.POST)
    public String addComment(Comment comment, @PathVariable("discussPostId")int discussPostId){
        commentService.addComment(hostHolder.getUser().getId(), comment.getEntityType(),
                comment.getEntityId(), comment.getTargetId(), comment.getContent(), discussPostId);
        return "redirect:/discussPost/detail/" + discussPostId;
    }

    //查用户的所有评论
    @RequestMapping(path = "userComments/{userId}", method = RequestMethod.GET)
    public String getUserComments(@PathVariable("userId") int userId, Page page, Model model){
        int count = commentService.findComment2PostCountByUserId(userId);
        page.setRecordsCount(count);
        page.setPath("/comment/userComments/" + userId);

        model.addAttribute("user", userService.findUserById(userId));
        model.addAttribute("commentCount", count);

        List<Comment> comments = commentService.findComments2PostByUserId(userId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> commentsVos = new ArrayList<>();
        for (Comment comment : comments) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("comment", comment);
            map.put("discussPostTitle", discussService.findDiscussPostById(comment.getEntityId()).getTitle());
            commentsVos.add(map);
        }
        model.addAttribute("comments", commentsVos);
        return "site/my-reply";
    }
}
