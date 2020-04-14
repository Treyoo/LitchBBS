package com.litchi.bbs.controller;

import com.litchi.bbs.annotation.MyAnnotation;
import com.litchi.bbs.entity.DiscussPost;
import com.litchi.bbs.entity.EntityType;
import com.litchi.bbs.entity.Page;
import com.litchi.bbs.entity.User;
import com.litchi.bbs.service.DiscussPostService;
import com.litchi.bbs.service.LikeService;
import com.litchi.bbs.service.UserService;
import com.litchi.bbs.util.constant.DiscussPostConst;
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

/**
 * @author cuiwj
 * @date 2020/3/8
 */
@Controller
public class HomeController implements DiscussPostConst {
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private UserService userService;
    @Autowired
    private LikeService likeService;

    @RequestMapping(path = {"/", "/index"})
    @MyAnnotation
    public String index(Model model, Page page, @RequestParam(name = "orderMode",
            defaultValue = "" + ORDER_BY_CREATE_TIME) int orderMode) {
        page.setRows(discussPostService.getDiscussRows(0));
        page.setPath("/index?orderMode=" + orderMode);
        List<DiscussPost> list = discussPostService
                .selectDiscussPosts(0, page.getOffset(), page.getLimit(), orderMode);
        if (null != list) {
            List<Map<String, Object>> discussPosts = new ArrayList<>();
            for (DiscussPost discussPost : list) {
                User user = userService.selectUserById(discussPost.getUserId());
                Map<String, Object> map = new HashMap<>(4);
                map.put("post", discussPost);
                map.put("user", user);
                map.put("likeCount", likeService.getLikeCount(EntityType.DISCUSS_POST, discussPost.getId()));
                discussPosts.add(map);
            }
            model.addAttribute("discussPosts", discussPosts);
        }
        model.addAttribute("orderMode", orderMode);
        return "/index";
    }

    @RequestMapping(path = "/error", method = RequestMethod.GET)
    public String get500ErrorPage() {
        return "error/500";
    }

    @RequestMapping(path = "/denied", method = RequestMethod.GET)
    public String get403ErrorPage() {
        return "error/403";
    }
}
