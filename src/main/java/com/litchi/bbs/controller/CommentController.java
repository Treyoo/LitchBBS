package com.litchi.bbs.controller;

import com.litchi.bbs.entity.Comment;
import com.litchi.bbs.entity.EntityType;
import com.litchi.bbs.entity.HostHolder;
import com.litchi.bbs.service.CommentService;
import com.litchi.bbs.util.JedisAdapter;
import com.litchi.bbs.util.RedisKeyUtil;
import org.checkerframework.checker.units.qual.A;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

/**
 * author:CuiWJ
 * date:2018/12/8
 */
@Controller
@RequestMapping("/comment")
public class CommentController {
    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);

    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private CommentService commentService;
//    @Autowired
//    EventProducer eventProducer;
    @Autowired
    private JedisAdapter jedisAdapter;

    @RequestMapping(path = "/add/{postId}", method = RequestMethod.POST)
    public String addComment(@PathVariable("postId") int postId,
                             @RequestParam("entityType") int entityType,
                             @RequestParam("entityId") int entityId,
                             @RequestParam("content") String content,
                             @RequestParam(value = "targetId", defaultValue = "0") int targetId) {
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setUserId(hostHolder.get().getId());
        comment.setCreateTime(new Date());
        comment.setEntityType(entityType);
        comment.setEntityId(entityId);
        comment.setTargetId(targetId);
        comment.setStatus(0);
        commentService.addComment(comment);
        //发出事件
//            eventProducer.fireEvent(new Event(EventType.COMMENT)
//                    .setActorId(hostHolder.get().getId())
//                    .setEntityType(EntityType.QUESTION)
//                    .setEntityId(postId)
//                    .setEntityOwnerId(discussPostService.getById(postId).getUserId())
//            );
        if (entityType == EntityType.DISCUSS_POST) {
            //记录该帖子id用于后续计算帖子分数
            jedisAdapter.sadd(RedisKeyUtil.getPostNeedCalScoreKey(), String.valueOf(entityId));
        }
        return "redirect:/discuss/" + postId;
    }
}
