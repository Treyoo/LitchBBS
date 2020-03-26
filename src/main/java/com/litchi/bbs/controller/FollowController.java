package com.litchi.bbs.controller;

import com.litchi.bbs.dao.CommentDAO;
import com.litchi.bbs.entity.EntityType;
import com.litchi.bbs.entity.HostHolder;
import com.litchi.bbs.entity.Page;
import com.litchi.bbs.entity.User;
import com.litchi.bbs.service.DiscussPostService;
import com.litchi.bbs.service.FollowService;
import com.litchi.bbs.service.UserService;
import com.litchi.bbs.util.LitchiUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @author CuiWJ
 * Created on 2018/12/12
 */
@Controller
public class FollowController {
    private static final Logger logger = LoggerFactory.getLogger(FollowController.class);
    @Autowired
    HostHolder hostHolder;
    @Autowired
    FollowService followService;
    //    @Autowired
//    EventProducer eventProducer;
    @Autowired
    DiscussPostService discussPostService;
    @Autowired
    UserService userService;
    @Autowired
    CommentDAO commentDAO;

    @RequestMapping(path = {"/followUser"}, method = RequestMethod.POST)
    @ResponseBody
    public String followUser(@RequestParam("userId") int userId) {
        boolean res = followService.follow(hostHolder.get().getId(), EntityType.USER, userId);
//            eventProducer.fireEvent(new Event(EventType.FOLLOW)
//                    .setActorId(hostHolder.get().getId())
//                    .setEntityType(EntityType.USER)
//                    .setEntityId(userId)
//                    .setEntityOwnerId(userId));
        //给前端返回被关注用户的最新关注人数
        return LitchiUtil.getJSONString(res ? 0 : 1,
                String.valueOf(followService.getFollowerCount(EntityType.USER, userId)));
    }

    @RequestMapping(path = {"/unFollowUser"}, method = RequestMethod.POST)
    @ResponseBody
    public String unfollowUser(@RequestParam("userId") int userId) {
        boolean res = followService.unfollow(hostHolder.get().getId(), EntityType.USER, userId);
        //给前端返回被关注用户的最新关注人数
        return LitchiUtil.getJSONString(res ? 0 : 1,
                String.valueOf(followService.getFollowerCount(EntityType.USER, userId)));
    }

    @RequestMapping(path = {"/followDiscuss"}, method = RequestMethod.POST)
    @ResponseBody
    public String followDiscuss(@RequestParam("postId") int postId) {
        if (discussPostService.selectByid(postId) == null) {
            return LitchiUtil.getJSONString(1, "帖子不存在！");
        }
        boolean res = followService.follow(hostHolder.get().getId(), EntityType.DISCUSS_POST, postId);
//            eventProducer.fireEvent(new Event(EventType.FOLLOW)
//                    .setActorId(hostHolder.get().getId())
//                    .setEntityType(EntityType.QUESTION)
//                    .setEntityId(postId)
//                    .setEntityOwnerId(discussPostService.getById(postId).getUserId()));
        //给前端返回关注数量和关注者等信息
        Map<String, Object> returnInfo = new HashMap<>();
        returnInfo.put("count", String.valueOf(followService.getFollowerCount(EntityType.DISCUSS_POST, postId)));
        returnInfo.put("id", hostHolder.get().getId());
        returnInfo.put("name", hostHolder.get().getUsername());
        returnInfo.put("headUrl", hostHolder.get().getHeaderUrl());
        return LitchiUtil.getJSONString(res ? 0 : 1, returnInfo);
    }

    @RequestMapping(path = {"/unFollowDiscuss"}, method = RequestMethod.POST)
    @ResponseBody
    public String unfollowDiscuss(@RequestParam("postId") int postId) {
        boolean res = followService.unfollow(hostHolder.get().getId(), EntityType.DISCUSS_POST, postId);
        //给前端返回关注数量和关注者等信息
        Map<String, Object> returnInfo = new HashMap<>();
        returnInfo.put("count", String.valueOf(followService.getFollowerCount(EntityType.DISCUSS_POST, postId)));
        returnInfo.put("id", hostHolder.get().getId());
        return LitchiUtil.getJSONString(res ? 0 : 1, returnInfo);
    }

    @RequestMapping(path = "/user/{id}/followers")
    public String getFollowers(@PathVariable("id") int userId, Model model, Page page) {
        page.setLimit(3);
        page.setRows((int) followService.getFollowerCount(EntityType.USER, userId));
        page.setPath("/user/"+userId+"/followers");
        User loginUser = hostHolder.get();
        List<Integer> followerIds = followService.getFollowers(EntityType.USER, userId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> vos = new ArrayList<>();
        for (int followerId : followerIds) {
            Map<String,Object> vo = new HashMap<>();
            vo.put("follower",userService.selectUserById(followerId));
            vo.put("followTime",followService.getFollowTime(EntityType.USER,userId,followerId));
            vo.put("isFollower", loginUser != null
                    && followService.isFollower(loginUser.getId(), EntityType.USER, followerId));
            vos.add(vo);
        }
        model.addAttribute("followers", vos);
        model.addAttribute("user",userService.selectUserById(userId));
        return "site/follower";
    }

    @RequestMapping(path = "/user/{id}/followees")
    public String getFollowees(@PathVariable("id") int userId, Model model) {
        List<Integer> followeeIds = followService.getFollowees(userId, EntityType.USER, 15);
        List<Map<String, Object>> vos = getFollowUsersInfo(hostHolder.get().getId(), followeeIds);
        model.addAttribute("followees", vos);
        model.addAttribute("followeeCount", followService.getFolloweeCount(userId, EntityType.USER));
        model.addAttribute("curUser", userService.selectUserById(userId));
        return "site/followee";
    }

    /**
     * 获取当前用户与一批用户的关注关系
     *
     * @param curUserId 当前用户id
     * @param userIds   一批用户id
     * @return 当前用户与每个用户关系的viewObject
     */
    private List<Map<String, Object>> getFollowUsersInfo(int curUserId, List<Integer> userIds) {
        List<Map<String, Object>> vos = new ArrayList<>();
        for (Integer userId : userIds) {
            User user = userService.selectUserById(userId);
            if (user == null) {
                continue;
            }
            Map<String, Object> vo = new HashMap<>();
            vo.put("user", user);
            vo.put("followerCount", followService.getFollowerCount(EntityType.USER, userId));
            vo.put("followeeCount", followService.getFolloweeCount(userId, EntityType.USER));
            vo.put("commentCount", commentDAO.getCommentCountByUser(userId));
            vo.put("followed", followService.isFollower(curUserId, EntityType.USER, userId));
            vos.add(vo);
        }
        return vos;
    }
}