package com.litchi.bbs.controller;

import com.litchi.bbs.entity.*;
import com.litchi.bbs.service.CommentService;
import com.litchi.bbs.service.DiscussPostService;
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
    @Autowired
    private CommentService commentService;

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(@RequestParam("title") String title,
                                 @RequestParam("content") String content) {
        try {
            DiscussPost discussPost = new DiscussPost();
            discussPost.setTitle(title);
            discussPost.setContent(content);
            discussPost.setCreateTime(new Date());
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
    public String detail(@PathVariable("id") int id, Model model, Page page) {
        try {
            DiscussPost post = discussPostService.selectByid(id);
            // 评论分页信息
            page.setLimit(5);
            page.setPath("/discuss/" + id);
            page.setRows(post.getCommentCount());
            logger.debug(String.format("共分%d页",page.getTotal()));

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
            List<Comment> comments = commentService.selectByEntity(EntityType.DISCUSS_POST,
                    id, page.getOffset(), page.getLimit());
            List<Map<String, Object>> vos = new ArrayList<>();
            for (Comment comment : comments) {
                User user = userService.selectUserById(comment.getUserId());
                Map<String, Object> vo = new HashMap<>();
                vo.put("comment", comment);
                vo.put("user", user);
                /*User loginUser = hostHolder.get();
                if (loginUser == null) {
                    vo.put("liked", 0);
                } else {
                    vo.put("liked", likeService.getStatus(loginUser.getId(), EntityType.COMMENT, comment.getId()));
                }
                vo.put("likeCount", likeService.getLikeCount(EntityType.COMMENT, comment.getId()));
                 */
                // 获取回复(评论的评论)
                List<Comment> replies = commentService.selectByEntity(EntityType.COMMENT,
                        comment.getId(), 0, Integer.MAX_VALUE);
                List<Map<String, Object>> replyVos = null;
                if (replies != null) {
                    replyVos = new ArrayList<>();
                    for (Comment reply : replies) {
                        User target = userService.selectUserById(reply.getTargetId());
                        Map<String, Object> replyVo = new HashMap<>();
                        replyVo.put("reply", reply);
                        replyVo.put("user", userService.selectUserById(reply.getUserId()));
                        replyVo.put("target", target);
                        replyVos.add(replyVo);
                    }
                }
                vo.put("replies", replyVos);
                vo.put("replyCount", commentService.getCommentCountByEntity(EntityType.COMMENT, comment.getId()));
                vos.add(vo);
            }
            model.addAttribute("comments", vos);

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
