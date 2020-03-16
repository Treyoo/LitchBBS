package com.litchi.bbs.controller;

import com.litchi.bbs.entity.Comment;
import com.litchi.bbs.entity.EntityType;
import com.litchi.bbs.entity.HostHolder;
import com.litchi.bbs.service.CommentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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

    @RequestMapping(path = "/add", method = RequestMethod.POST)
    public String addComment(@RequestParam("postId") int postId,
                             @RequestParam("content") String content) {
        try {
            Comment comment = new Comment();
            comment.setContent(content);
            comment.setUserId(hostHolder.get().getId());
            comment.setCreatedTime(new Date());
            comment.setEntityType(EntityType.DISCUSS_POST);
            comment.setEntityId(postId);
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
