package com.litchi.bbs.controller;

import com.litchi.bbs.entity.HostHolder;
import com.litchi.bbs.service.LikeService;
import com.litchi.bbs.service.UserService;
import com.litchi.bbs.util.LitchiUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author cuiwj
 * @date 2020/3/15
 */
@Controller
@RequestMapping("/user")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    @Value("${bbs.path.upload}")
    private String uploadPath;

    @Value("${bbs.path.domain}")
    private String domain;

    @Autowired
    HostHolder hostHolder;
    @Autowired
    UserService userService;
    @Autowired
    LikeService likeService;

    @RequestMapping("/setting")
    public String getSettingPage() {
        logger.debug("当前用户:" + String.valueOf(hostHolder.get()));
        return "site/setting";
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImg, Model model) {
        if (headerImg == null) {
            model.addAttribute("msg", "请选择图片");
            return "redirect:/user/setting";
        }
        String filename = headerImg.getOriginalFilename();
        String suffix = filename == null ? null : filename.substring(filename.lastIndexOf('.'));
        if (StringUtils.isBlank(suffix)) {
            model.addAttribute("msg", "文件格式错误");
            return "redirect:/user/setting";
        }
        //Gen random file name
        filename = LitchiUtil.genRandomString() + suffix;
        // saving destination
        File file = new File(uploadPath + '/' + filename);
        try {
            headerImg.transferTo(file);
        } catch (IOException e) {
            logger.error("上传文件失败," + e.getMessage());
            throw new RuntimeException("上传文件失败,服务器出现异常!", e);
        }
        // 更新当前用户的头像的路径(web访问路径)
        // http://localhost:8080/user/header/xxx.png
        String headerUrl = domain + "/user/header/" + filename;
        userService.updateHeaderUrl(hostHolder.get().getId(), headerUrl);
        return "redirect:/index";
    }

    @RequestMapping(path = "/header/{fileName}", method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response) {
        // 服务器存放路径
        fileName = uploadPath + "/" + fileName;
        // 文件后缀
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        // 响应图片
        response.setContentType("image/" + suffix);
        try (
                FileInputStream fis = new FileInputStream(fileName);
                OutputStream os = response.getOutputStream();
        ) {
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fis.read(buffer)) != -1) {
                os.write(buffer, 0, b);
            }
        } catch (IOException e) {
            logger.error("读取头像失败: " + e.getMessage());
        }
    }

    @RequestMapping(value = "/profile/{userId}",method=RequestMethod.GET)
    public String getProfile(Model model, @PathVariable("userId") int userId){
        model.addAttribute("user",userService.selectUserById(userId));
        model.addAttribute("totalLikedCount",likeService.getUserTotalLikedCount(userId));
        return "site/profile";
    }
}
