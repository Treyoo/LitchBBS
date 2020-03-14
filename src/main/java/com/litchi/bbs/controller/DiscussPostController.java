package com.litchi.bbs.controller;

import com.litchi.bbs.entity.DiscussPost;
import com.litchi.bbs.entity.HostHolder;
import com.litchi.bbs.entity.User;
import com.litchi.bbs.service.DiscussPostService;
import com.litchi.bbs.service.UserService;
import com.litchi.bbs.util.LitchiUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author cuiwj
 * @date 2020/3/13
 */
@Controller
@RequestMapping("/discuss")
public class DiscussPostController {
    private static final Logger logger = LoggerFactory.getLogger(DiscussPostController.class);
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private UserService userService;

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(@RequestParam("title") String title,
                                 @RequestParam("content") String content) {
        try {
            DiscussPost discussPost = new DiscussPost();
            discussPost.setTitle(title);
            discussPost.setContent(content);
            discussPost.setCreatedDate(new Date());
            discussPost.setCommentCount(0);
            if (hostHolder.get() == null) {
                return LitchiUtil.getJSONString(999);
            } else {
                discussPost.setUserId(hostHolder.get().getId());
            }
            if (discussPostService.addDiscussPost(discussPost) > 0) {
//                eventProducer.fireEvent(new Event(EventType.QUESTION).setActorId(discussPost.getUserId())
//                        .setEntityType(EntityType.QUESTION).setEntityId(discussPost.getId())
//                        .setEntityOwnerId(discussPost.getUserId()).setExt("title", title)
//                        .setExt("content", content));
                logger.debug("新增discuss ID:" + discussPost.getId());
                return LitchiUtil.getJSONString(0, "发布帖子成功");
            }
        } catch (Exception e) {
            logger.error("添加帖子出现异常" + e.getMessage());
        }
        return LitchiUtil.getJSONString(1, "发布帖子失败");
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String detail(@PathVariable("id") int id, Model model) {
        try {
            DiscussPost post = discussPostService.selectByid(id);
            model.addAttribute("post", post);
            model.addAttribute("user", userService.selectUserById(post.getUserId()));
            //获取follower
            /*
            if (hostHolder.get() != null) {
                model.addAttribute("followed",
                        followService.isFollower(hostHolder.get().getId(), EntityType.QUESTION, id));
            } else {
                model.addAttribute("followed", false);
            }
            model.addAttribute("followUsers",
                    getUserInfoByIds(followService.getFollowers(EntityType.QUESTION, id, 10)));*/

            //获取评论
            /*
            List<Comment> comments = commentService.selectByEntity(EntityType.QUESTION, id, 0, 10);
            List<ViewObject> vos = new ArrayList<>();
            for (Comment comment : comments) {
                User user = userService.selectById(comment.getUserId());
                ViewObject vo = new ViewObject();
                vo.put("comment", comment);
                vo.put("user", user);
                User curUser = hostHolder.get();
                if (curUser == null) {
                    vo.put("liked", 0);
                } else {
                    vo.put("liked", likeService.getStatus(curUser.getId(), EntityType.COMMENT, comment.getId()));
                }
                vo.put("likeCount", likeService.getLikeCount(EntityType.COMMENT, comment.getId()));
                vos.add(vo);
            }
            model.addAttribute("comments", vos);*/

        } catch (Exception e) {
            logger.error("获取问题详情失败" + e.getMessage());
            e.printStackTrace();
        }
        return "site/discuss-detail";
    }

    /**
     * 获取所有follower的用户信息
     *
     * @param ids 所有follower的id
     * @return User集合
     */
    private Set<User> getUserInfoByIds(List<Integer> ids) {
        Set<User> followers = new HashSet<>();
        for (Integer id : ids) {
            User user = userService.selectUserById(id);
            if (user != null) {
                followers.add(user);
            }
        }
        return followers;
    }
}
