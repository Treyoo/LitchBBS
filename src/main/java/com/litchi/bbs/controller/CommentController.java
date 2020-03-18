package com.litchi.bbs.controller;

import com.litchi.bbs.entity.Comment;
import com.litchi.bbs.entity.HostHolder;
import com.litchi.bbs.service.CommentService;
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
    HostHolder hostHolder;
    @Autowired
    CommentService commentService;
//    @Autowired
//    EventProducer eventProducer;

    @RequestMapping(path = "/add/{postId}", method = RequestMethod.POST)
    public String addComment(@PathVariable("postId") int postId,
                             @RequestParam("entityType") int entityType,
                             @RequestParam("entityId") int entityId,
                             @RequestParam("content") String content,
                             @RequestParam(value = "targetId", defaultValue = "0") int targetId) {
        try {
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
        } catch (Exception e) {
            logger.error("添加评论失败" + e.getMessage());
        }
        return "redirect:/discuss/" + postId;
    }
}
