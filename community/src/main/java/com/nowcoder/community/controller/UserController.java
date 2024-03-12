package com.nowcoder.community.controller;

import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.controller.vo.UserInformationVo;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.joint.FollowService;
import com.nowcoder.community.service.joint.LikeService;
import com.nowcoder.community.service.joint.UploadService;
import com.nowcoder.community.service.joint.UserService;
import com.nowcoder.community.service.vo.UploadVo;
import com.nowcoder.community.util.CommonUtils;
import com.nowcoder.community.util.Constants;
import com.nowcoder.community.util.HostHolder;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

/**
 * @author : Zhou Xiang
 * @date : 2023/12/7  22:02
 * @description :与用户相关的界面
 **/
@Controller
@RequestMapping(path = "user")
public class UserController {
    private Logger logger = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private HostHolder hostHolder;
    @Value("${community.path.upload}")
    private String UPLOAD_PATH;
    @Value("${community.path.domain}")
    private String DOMAIN;
    @Value("${server.servlet.context-path}")
    private String CONTEXT_PATH;
    @Value("${qiniu.bucket.address}")
    private String HEADER_URL_PREFIX;
    @Autowired
    private UserService userService;
    @Autowired
    private LikeService likeService;
    @Autowired
    private FollowService followService;
    @Autowired
    private UploadService uploadService;

    @RequestMapping(path = "setting", method = RequestMethod.GET)
    @LoginRequired
    public String getSettingPage(Model model){
        UploadVo uploadVo = uploadService.upload2Qiniu();
        model.addAttribute("uploadAddress", uploadVo.getUploadAddress());
        model.addAttribute("uploadToken", uploadVo.getUploadToken());
        model.addAttribute("filename", uploadVo.getFilename());
        return "site/setting";
    }

    @RequestMapping(path = "saveHeader", method = RequestMethod.POST)
    @ResponseBody
    public String saveHeader(String filename){
        if(StringUtils.isBlank(filename)){
            logger.error("头像文件还未上传");
            return CommonUtils.getJSONString(400, "头像文件还未上传！");
        }

        String url = HEADER_URL_PREFIX + filename;
        userService.changeHeaderUrl(url, hostHolder.getUser().getId());
        return CommonUtils.getJSONString(200, "头像保存成功！");
    }


    @Deprecated
//    @RequestMapping(path = "uploadHeader", method = RequestMethod.POST)
    @LoginRequired
    public String uploadHeader(Model model, MultipartFile head){
        //处理文件上传异常
        if(head == null){
            model.addAttribute("headMsg", "没有选择图片");
            return "site/setting";
        }

        String type = head.getContentType();
        String suffix = type.substring(type.lastIndexOf("/") + 1);
        if(!suffix.equals("jpeg") && !suffix.equals("png") && !suffix.equals("gif")){
            model.addAttribute("headMsg", "错误的文件类型");
            return "site/setting";
        }

        //存储文件
        String fileName = CommonUtils.generateUUID() + "." + suffix;
        String filePath= UPLOAD_PATH + "/" + fileName;
        File file = new File(filePath);
        try {
            head.transferTo(file);
        } catch (IOException e) {
            logger.error("存储文件失败" + e.getMessage());
            throw new RuntimeException("上传文件失败", e);
        }

        //改用户的头像路径
        //http://localhost:8080/community/user/header/{filename}
        String url = DOMAIN + CONTEXT_PATH + "/user/header/" + fileName;
        userService.changeHeaderUrl(url, hostHolder.getUser().getId());
        return "redirect:/user/setting";
    }

    @Deprecated
//    @RequestMapping(path = "header/{filename}", method = RequestMethod.GET)
    public void getHeader(@PathVariable("filename") String filename, HttpServletResponse response){
        //获取图片在服务器的路径
        String filePath = UPLOAD_PATH + "/" + filename;

        response.setContentType("image/" + filename.substring(filename.lastIndexOf(".") + 1));
        try {
            InputStream input = new FileInputStream(new File(filePath));
            IOUtils.copy(input, response.getOutputStream());
        } catch (FileNotFoundException e) {
            logger.error("头像图片获取失败: " + e.getMessage());
            throw new RuntimeException(e);
        } catch (IOException e) {
            logger.error("返回图片失败： " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @RequestMapping(path = "resetPassword", method = RequestMethod.POST)
    @LoginRequired
    public String resetPasswordAfterLogin(Model model, String newPassword, String oldPassword, HttpSession session){
        //这次，我将在js中实现判断密码不为空，且密码不能小于8位
        //判断旧密码是不是正确
        User user = hostHolder.getUser();
        oldPassword = CommonUtils.MD5(oldPassword + user.getSalt());
        if(!oldPassword.equals(user.getPassword())){
            model.addAttribute("oldPasswordMsg", "旧密码不正确");
            return "site/setting";
        }

        //改密码
        newPassword = CommonUtils.MD5(newPassword + user.getSalt());
        userService.resetPasswordById(newPassword, user.getId());
        model.addAttribute("message", "修改密码成功");
        model.addAttribute("href", "/logout");
        return "site/operate-result";
    }

    @RequestMapping(path = "profile/{userId}", method = RequestMethod.GET)
    public String getProfile(@PathVariable("userId") int userId, Model model){
        User host = hostHolder.getUser();
        boolean followed = false;
        if(host != null){
            followed = followService.isFollowerOfFollowee(hostHolder.getUser().getId(), Constants.ENTITY_USER, userId);
        }

        UserInformationVo userInformationVo = new UserInformationVo();
        userInformationVo.setUser(userService.findUserById(userId));
        userInformationVo.setLikeCount(likeService.getUserLikeCount(userId));
        userInformationVo.setFolloweeCount(followService.getFolloweeCount(userId, Constants.ENTITY_USER));
        userInformationVo.setFollowerCount(followService.getFollowerCount(Constants.ENTITY_USER, userId));
        model.addAttribute("userInformationVo", userInformationVo);
        model.addAttribute("followed", followed);
        return "site/profile";
    }
}
