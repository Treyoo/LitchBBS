package com.litchi.bbs.controller;

import com.litchi.bbs.annotation.MyAnnotation;
import com.litchi.bbs.entity.DiscussPost;
import com.litchi.bbs.entity.Page;
import com.litchi.bbs.entity.User;
import com.litchi.bbs.service.DiscussPostService;
import com.litchi.bbs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author cuiwj
 * @date 2020/3/8
 */
@Controller
public class HomeController {
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private UserService userService;

    @RequestMapping(path = {"/", "/index"})
    @MyAnnotation
    public String index(Model model, Page page) {
        page.setRows(discussPostService.getDiscussRows(0));
        page.setPath("/index");
        List<DiscussPost> list = discussPostService.selectDiscussPosts(0, page.getOffset(), page.getLimit());
        if (null != list) {
            List<Map<String, Object>> discussPosts = new ArrayList<>();
            for (DiscussPost discussPost : list) {
                User user = userService.selectUserById(discussPost.getUserId());
                Map<String, Object> map = new HashMap<>(4);
                map.put("post", discussPost);
                map.put("user", user);
                discussPosts.add(map);
            }
            model.addAttribute("discussPosts", discussPosts);
        }
        return "/index";
    }
}
