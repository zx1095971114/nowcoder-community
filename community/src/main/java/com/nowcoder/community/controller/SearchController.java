package com.nowcoder.community.controller;

import com.nowcoder.community.controller.vo.DiscussPostVo;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.service.joint.ElasticsearchService;
import com.nowcoder.community.service.joint.LikeService;
import com.nowcoder.community.service.joint.UserService;
import com.nowcoder.community.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : Zhou Xiang
 * @date : 2024/2/11  21:08
 * @description :有关搜索的Controller
 **/
@Controller
public class SearchController {
    @Autowired
    private ElasticsearchService elasticsearchService;
    @Autowired
    private UserService userService;
    @Autowired
    private LikeService likeService;

    @RequestMapping(path = "search", method = RequestMethod.GET)
    public String getSearch(Model model, Page page, String keyword){
        List<DiscussPost> discussPostList = elasticsearchService.searchDiscussPost(keyword, page.getCurrentPage(), page.getLimit());
        page.setPath("/search?keyword=" + keyword);
        page.setRecordsCount(discussPostList.size());

        List<DiscussPostVo> posts = new ArrayList<>();
        for (DiscussPost discussPost : discussPostList) {
            DiscussPostVo discussPostVo = new DiscussPostVo();
            discussPostVo.setDiscussPost(discussPost);
            discussPostVo.setUser(userService.findUserById(discussPost.getUserId()));
            discussPostVo.setLikeCount((int) likeService.getLikeCount(Constants.ENTITY_DISCUSS_POST, discussPost.getId()));

            posts.add(discussPostVo);
        }

        model.addAttribute("keyword", keyword);
        model.addAttribute("posts", posts);

        return "site/search";
    }
}
