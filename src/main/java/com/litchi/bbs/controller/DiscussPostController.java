package com.litchi.bbs.controller;

import com.litchi.bbs.async.Event;
import com.litchi.bbs.async.EventProducer;
import com.litchi.bbs.entity.*;
import com.litchi.bbs.service.CommentService;
import com.litchi.bbs.service.DiscussPostService;
import com.litchi.bbs.service.LikeService;
import com.litchi.bbs.service.UserService;
import com.litchi.bbs.util.JedisAdapter;
import com.litchi.bbs.util.LitchiUtil;
import com.litchi.bbs.util.RedisKeyUtil;
import com.litchi.bbs.util.constant.DiscussPostConst;
import com.litchi.bbs.util.constant.EventTopic;
import com.litchi.bbs.util.constant.LikeStatus;
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
public class DiscussPostController implements LikeStatus, DiscussPostConst {
    private static final Logger logger = LoggerFactory.getLogger(DiscussPostController.class);
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private UserService userService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private LikeService likeService;
    @Autowired
    private EventProducer eventProducer;
    @Autowired
    private JedisAdapter jedisAdapter;

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(@RequestParam("title") String title,
                                 @RequestParam("content") String content) {
        DiscussPost discussPost = new DiscussPost();
        discussPost.setTitle(title);
        discussPost.setContent(content);
        discussPost.setCreateTime(new Date());
        discussPost.setCommentCount(0);
        discussPost.setUserId(hostHolder.get().getId());
        if (discussPostService.addDiscussPost(discussPost) > 0) {
            eventProducer.fireEvent(new Event(EventTopic.TOPIC_PUBLISH_DISCUSS)
                    .setActorId(discussPost.getUserId())
                    .setEntityType(EntityType.DISCUSS_POST)
                    .setEntityId(discussPost.getId())
                    .setEntityOwnerId(discussPost.getUserId())
                    .setExt("title", title)
                    .setExt("content", content));
            //记录该帖子id用于后续计算分数(新增要计算，得到默认分数)
            String redisKey = RedisKeyUtil.getPostNeedCalScoreKey();
            jedisAdapter.sadd(redisKey, String.valueOf(discussPost.getId()));
            logger.debug("新增discuss ID:" + discussPost.getId());
            return LitchiUtil.getJSONString(0, "发布帖子成功");
        }
        return LitchiUtil.getJSONString(1, "发布帖子失败");
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String detail(@PathVariable("id") int id, Model model, Page page) {
        User loginUser = hostHolder.get();
        DiscussPost post = discussPostService.selectById(id);
        // 评论分页信息
        page.setLimit(5);
        page.setPath("/discuss/" + id);
        page.setRows(post.getCommentCount());
        logger.debug(String.format("共分%d页", page.getTotal()));

        model.addAttribute("post", post);
        model.addAttribute("user", userService.selectUserById(post.getUserId()));
        if (loginUser == null) {
            model.addAttribute("likeStatus", NONE);
        } else {
            model.addAttribute("likeStatus", likeService.getLikeStatus(loginUser.getId(),
                    EntityType.DISCUSS_POST, id));
        }
        model.addAttribute("likeCount", likeService.getLikeCount(EntityType.DISCUSS_POST, id));
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
            if (loginUser == null) {
                vo.put("likeStatus", NONE);
            } else {
                vo.put("likeStatus", likeService.getLikeStatus(loginUser.getId(), EntityType.COMMENT, comment.getId()));
            }
            vo.put("likeCount", likeService.getLikeCount(EntityType.COMMENT, comment.getId()));

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
                    replyVo.put("likeCount", likeService.getLikeCount(EntityType.COMMENT, reply.getId()));
                    replyVo.put("likeStatus", loginUser == null ? NONE : likeService.getLikeStatus
                            (loginUser.getId(), EntityType.COMMENT, reply.getId()));
                    replyVos.add(replyVo);
                }
            }
            vo.put("replies", replyVos);
            vo.put("replyCount", commentService.getCommentCountByEntity(EntityType.COMMENT, comment.getId()));
            vos.add(vo);
        }
        model.addAttribute("comments", vos);

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

    @DeleteMapping(path = "/delete")
    @ResponseBody
    public String delete(@RequestParam("id") int id) {
        DiscussPost discussPost = discussPostService.selectById(id);
        if (discussPost.getStatus() == STATUS_DELETED) {
            discussPostService.updateDiscussStatus(id, STATUS_NORMAL);
            eventProducer.fireEvent(new Event(EventTopic.TOPIC_PUBLISH_DISCUSS)
                    .setActorId(discussPost.getUserId())
                    .setEntityType(EntityType.DISCUSS_POST)
                    .setEntityId(discussPost.getId())
                    .setEntityOwnerId(discussPost.getUserId())
                    .setExt("title", discussPost.getTitle())
                    .setExt("content", discussPost.getContent()));
            return LitchiUtil.getJSONString(0, "撤销删除成功");
        } else {
            discussPostService.updateDiscussStatus(id, STATUS_DELETED);
            eventProducer.fireEvent(new Event(EventTopic.TOPIC_DELETE_DISCUSS)
                    .setActorId(discussPost.getUserId())
                    .setEntityType(EntityType.DISCUSS_POST)
                    .setEntityId(discussPost.getId())
                    .setEntityOwnerId(discussPost.getUserId()));
            return LitchiUtil.getJSONString(0, "删除成功");
        }
    }

    @PostMapping(path = "/top")
    @ResponseBody
    public String top(@RequestParam("id") int id) {
        int type = discussPostService.getDiscussType(id);
        String msg = null;
        if (type == TYPE_NORMAL) {
            discussPostService.updateDiscussType(id, TYPE_TOP);
            msg = "置顶成功";
        } else if (type == TYPE_TOP) {
            discussPostService.updateDiscussType(id, TYPE_NORMAL);
            msg = "取消置顶成功";
        } else {
            throw new RuntimeException("数据异常，未识别帖子类型：%d" + type);
        }
        return LitchiUtil.getJSONString(0, msg);
    }

    @PostMapping(path = "/highlight")
    @ResponseBody
    public String highlight(@RequestParam("id") int id) {
        int status = discussPostService.getDiscussStatus(id);
        if (status == STATUS_NORMAL) {
            discussPostService.updateDiscussStatus(id, STATUS_HIGHLIGHT);
            //记录该帖子id用于后续计算分数
            jedisAdapter.sadd(RedisKeyUtil.getPostNeedCalScoreKey(),String.valueOf(id));
            return LitchiUtil.getJSONString(0, "加精成功");
        } else if (status == STATUS_HIGHLIGHT) {
            discussPostService.updateDiscussStatus(id, STATUS_NORMAL);
            jedisAdapter.sadd(RedisKeyUtil.getPostNeedCalScoreKey(),String.valueOf(id));
            return LitchiUtil.getJSONString(0, "取消加精成功");
        } else {
            return LitchiUtil.getJSONString(1, "加精失败");
        }
    }
}
