package com.nowcoder.community.controller;

import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.controller.vo.Comment2CommentVo;
import com.nowcoder.community.controller.vo.Comment2DiscussPostVo;
import com.nowcoder.community.controller.vo.UserDiscussPostVo;
import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.joint.CommentService;
import com.nowcoder.community.service.joint.DiscussService;
import com.nowcoder.community.service.joint.LikeService;
import com.nowcoder.community.service.joint.UserService;
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

/**
 * @author : Zhou Xiang
 * @date : 2023/12/13  15:07
 * @description : 与帖子相关的页面
 **/

@Controller
@RequestMapping("discussPost")
public class DiscussPostController implements Constants{
    @Autowired
    private DiscussService discussService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private UserService userService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private LikeService likeService;


    @RequestMapping(path = "publish", method = RequestMethod.POST)
    @ResponseBody
    @LoginRequired
    public String publishDiscussPost(String title, String content){
        User user = hostHolder.getUser();

        //先不处理请求失败的逻辑
        if(discussService.addDiscussPost(title, content, user.getId()) == 0){
            return CommonUtils.getJSONString(500, "发布失败");
        }
        return CommonUtils.getJSONString(200, "发布成功");
    }

    //localhost:8080/community/discussPost/detail/{id}
    @RequestMapping(path = "detail/{id}", method = RequestMethod.GET)
    public String getDiscussPostDetail(Model model, @PathVariable("id")int id, Page page){
        //先不考虑没查到的情况
        //帖子本身的内容
        DiscussPost discussPost = discussService.findDiscussPostById(id);
        User writer = userService.findUserById(discussPost.getUserId());
        model.addAttribute("discussPost", discussPost);
        model.addAttribute("writer", writer);
        model.addAttribute("likeCount", likeService.getLikeCount(ENTITY_DISCUSS_POST, id));
        //如果没登录，默认就显示赞，所以默认的likeStatus是LIKE
        model.addAttribute("likeStatus", getHostLikeStatus(ENTITY_DISCUSS_POST, id));

        //对帖子的评论
        //分页的设计
        page.setPath("/discussPost/detail/" + id);
        page.setRecordsCount(commentService.findCommentCountByEntityId(1, id));

        List<Comment> comments = commentService.findCommentsByEntityId(1, id, page.getOffset(), page.getLimit());
        //将评论转为评论视图
        List<Comment2DiscussPostVo> list = new ArrayList<>();
        for(Comment comment2discussPost: comments){
            Comment2DiscussPostVo comment2DiscussPostVo = new Comment2DiscussPostVo();
            //设置对帖子的回复
            comment2DiscussPostVo.setComment(comment2discussPost);

            //设置用户
            User user = userService.findUserById(comment2discussPost.getUserId());
            comment2DiscussPostVo.setUser(user);

            //设置回复的回复数
            int count = commentService.findCommentCountByEntityId(2, comment2discussPost.getId());
            comment2DiscussPostVo.setCommentsCount(count);

            //设置帖子回复
            List<Comment2CommentVo> replyComments = new ArrayList<>();
            //评论的评论暂不做分页展示
            List<Comment> comments2comment = commentService.findCommentsByEntityId(2, comment2discussPost.getId(), 0, count);
            for(Comment comment2comment: comments2comment){
                Comment2CommentVo comment2CommentVo = new Comment2CommentVo();
                comment2CommentVo.setComment(comment2comment);
                comment2CommentVo.setUser(userService.findUserById(comment2comment.getUserId()));
                comment2CommentVo.setTarget(comment2comment.getTargetId() == 0 ? null : userService.findUserById(comment2comment.getTargetId()));
                comment2CommentVo.setLikeCount(likeService.getLikeCount(ENTITY_COMMENT, comment2comment.getId()));
                comment2CommentVo.setLikeStatus(getHostLikeStatus(ENTITY_COMMENT, comment2comment.getId()));
                replyComments.add(comment2CommentVo);
            }
            comment2DiscussPostVo.setComments2comment(replyComments);

            //设置评论被点赞的数目
            comment2DiscussPostVo.setLikeCount(likeService.getLikeCount(ENTITY_COMMENT, comment2discussPost.getId()));

            //设置当前用户对该评论的点赞状态
            comment2DiscussPostVo.setLikeStatus(getHostLikeStatus(ENTITY_COMMENT, comment2discussPost.getId()));

            list.add(comment2DiscussPostVo);
        }

        model.addAttribute("comments2discuss", list);

        return "site/discuss-detail";
    }

    //获取当前用户对某实体的点赞情况，如果没登录，就默认为NOT_LIKE
    private String getHostLikeStatus(int entityType, int entityId){
        int likeStatus = NOT_LIKE;
        if(hostHolder != null && hostHolder.getUser() != null){
            likeStatus = likeService.getLikeStatus(hostHolder.getUser().getId(), entityType, entityId);
        }

        switch (likeStatus){
            case LIKE:
                return "已赞";
            case NOT_LIKE:
                return "赞";
            default:
                return "赞";
        }
    }

    @RequestMapping(path = "userPosts/{userId}", method = RequestMethod.GET)
    public String getUserDiscussPost(@PathVariable("userId") int userId, Model model, Page page){
        int count = discussService.findDiscussPostCountByUserId(userId);

        page.setPath("/discussPost/userPosts/" + userId);
        page.setRecordsCount(count);

        List<DiscussPost> discussPostList = discussService.findDiscussPostByUserId(userId, page.getOffset(), page.getLimit(), 0);

        List<UserDiscussPostVo> discussPosts = new ArrayList<>();
        for (DiscussPost discussPost : discussPostList) {
            UserDiscussPostVo userDiscussPostVo = new UserDiscussPostVo();
            userDiscussPostVo.setDiscussPost(discussPost);
            userDiscussPostVo.setLikeCount(likeService.getLikeCount(ENTITY_DISCUSS_POST, discussPost.getId()));
            discussPosts.add(userDiscussPostVo);
        }
        model.addAttribute("discussPosts", discussPosts);

        model.addAttribute("userDiscussPostCount", count);

        model.addAttribute("user", userService.findUserById(userId));

        return "site/my-post";
    }

    //置顶
    @RequestMapping(path = "top", method = RequestMethod.POST)
    @ResponseBody
    public String topDiscussPost(int id){
        discussService.setTop(id);

        return CommonUtils.getJSONString(200, "置顶成功！");
    }

    //取消置顶
    @RequestMapping(path = "untop", method = RequestMethod.POST)
    @ResponseBody
    public String untopDiscussPost(int id){
        discussService.setUntop(id);

        return CommonUtils.getJSONString(200, "取消置顶！");
    }

    //加精
    @RequestMapping(path = "refinement", method = RequestMethod.POST)
    @ResponseBody
    public String refinementDiscussPost(int id){
        discussService.setRefinement(id);

        return CommonUtils.getJSONString(200, "加精成功！");
    }

    //取消加精
    @RequestMapping(path = "unrefinement", method = RequestMethod.POST)
    @ResponseBody
    public String unrefinement(int id){
        discussService.setUnrefinement(id);

        return CommonUtils.getJSONString(200, "已取消加精！");
    }

    @RequestMapping(path = "delete", method = RequestMethod.POST)
    @ResponseBody
    public String delete(int id){
        discussService.setDeleted(id);

        return CommonUtils.getJSONString(200, "删帖成功！");
    }
}
