package com.litchi.bbs.controller;

import com.litchi.bbs.entity.Comment;
import com.litchi.bbs.entity.EntityType;
import com.litchi.bbs.entity.HostHolder;
import com.litchi.bbs.service.CommentService;
import com.litchi.bbs.service.LikeService;
import com.litchi.bbs.util.LitchiUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * @author CuiWJ
 * Created on 2018/12/10
 */
@Controller
public class LikeController {
    private static final Logger logger = LoggerFactory.getLogger(LikeController.class);
    @Autowired
    LikeService likeService;
    @Autowired
    HostHolder hostHolder;
    //    @Autowired
//    EventProducer eventProducer;
    @Autowired
    CommentService commentService;

    @RequestMapping(path = "/like", method = RequestMethod.POST)
    @ResponseBody
    public String like(@RequestParam("entityType") int entityType,
                       @RequestParam("entityId") int entityId,
                       @RequestParam("entityUserId") int entityUserId) {
        long likeCount = likeService.like(hostHolder.get().getId(), entityType, entityId, entityUserId);
        if (EntityType.COMMENT == entityType) {
            Comment comment = commentService.getCommentById(entityId);
            /*eventProducer.fireEvent(new Event(EventType.LIKE)
                    .setActorId(hostHolder.get().getId())
                    .setEntityType(EntityType.COMMENT)
                    .setEntityId(entityId)
                    .setEntityOwnerId(comment.getUserId())
                    .setExt("questionId",String.valueOf(comment.getEntityId())));*/
        }
        Map<String, Object> res = new HashMap<>();
        res.put("likeCount", likeCount);
        res.put("likeStatus", likeService.getLikeStatus(hostHolder.get().getId(), entityType, entityId));
        return LitchiUtil.getJSONString(0, res);
    }

    @RequestMapping(path = "/dislike", method = RequestMethod.POST)
    @ResponseBody
    public String dislike(@RequestParam("entityType") int entityType,
                          @RequestParam("entityId") int entityId,
                          @RequestParam("entityUserId") int entityUserId) {

        long likeCount = likeService.dislike(hostHolder.get().getId(), EntityType.COMMENT, entityId, entityUserId);
        return LitchiUtil.getJSONString(0, String.valueOf(likeCount));

    }
}
