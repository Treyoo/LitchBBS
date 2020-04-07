package com.litchi.bbs.controller;

import com.litchi.bbs.entity.HostHolder;
import com.litchi.bbs.entity.Message;
import com.litchi.bbs.entity.Page;
import com.litchi.bbs.entity.User;
import com.litchi.bbs.service.MessageService;
import com.litchi.bbs.service.UserService;
import com.litchi.bbs.util.LitchiUtil;
import com.litchi.bbs.util.constant.EventTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import java.util.*;

/**
 * author:CuiWJ
 * date:2018/12/9
 */
@Controller
@RequestMapping("/msg")
public class MessageController implements EventTopic {
    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    MessageService messageService;
    @Autowired
    UserService userService;
    @Autowired
    HostHolder hostHolder;

    @RequestMapping(value = "/letter", method = RequestMethod.POST)
    @ResponseBody
    public String addLetter(@RequestParam("toName") String toName,
                            @RequestParam("content") String content) {
        if (hostHolder.get() == null) {
            return LitchiUtil.getJSONString(999, "请先登录");
        }
        User targetUser = userService.selectUserByName(toName);
        if (targetUser == null) {
            return LitchiUtil.getJSONString(1, "用户不存在");
        }
        Message message = new Message();
        int fromId = hostHolder.get().getId();
        int toId = targetUser.getId();
        message.setFromId(fromId);
        message.setToId(toId);
        message.setContent(content);
        message.setCreateTime(new Date());
        message.setConversationId(fromId < toId ? fromId + "_" + toId : toId + "_" + fromId);
        messageService.addMessage(message);
        return LitchiUtil.getJSONString(0);
    }

    @RequestMapping(path = "/letter/detail/{conversationId}", method = RequestMethod.GET)
    public String conversationDetail(@PathVariable("conversationId") String conversationId,
                                     Model model, Page page) {
        try {
            int localUserId = hostHolder.get().getId();
            page.setPath("/msg/letter/detail/" + conversationId);
            page.setRows(messageService.getConversationTotalLetterCount(localUserId, conversationId));
            page.setLimit(3);
            List<Message> conversation = messageService.getConversationDetail(conversationId,
                    page.getOffset(), page.getLimit());
            List<Map<String, Object>> vos = new ArrayList<>();
            for (Message msg : conversation) {
                Map<String, Object> vo = new HashMap<>();
                vo.put("user", userService.selectUserById(msg.getFromId()));
                vo.put("message", msg);
                vos.add(vo);
            }
            model.addAttribute("messages", vos);
            model.addAttribute("target", getLetterTarget(conversationId));
        } catch (Exception e) {
            logger.error("获取会话详情失败" + e.getMessage());
            e.printStackTrace();
        }
        return "site/letter-detail";
    }

    private User getLetterTarget(String conversationId) {
        String[] ids = conversationId.split("_");
        int id0 = Integer.parseInt(ids[0]);
        int id1 = Integer.parseInt(ids[1]);
        if (hostHolder.get().getId() == id0) {
            return userService.selectUserById(id1);
        } else {
            return userService.selectUserById(id0);
        }
    }

    @RequestMapping(path = "/letters", method = RequestMethod.GET)
    public String conversationList(Model model, Page page) {
        int localUserId = hostHolder.get().getId();
        page.setPath("/msg/letters");
        page.setRows(messageService.getConversationCount(localUserId));
        page.setLimit(3);
        List<Message> conversationList = messageService.getConversationList(
                localUserId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> vos = new ArrayList<>();
        for (Message msg : conversationList) {
            Map<String, Object> vo = new HashMap<>();
            vo.put("message", msg);
            vo.put("unread", messageService.getConversationUnReadLetterCount(localUserId, msg.getConversationId()));
            vo.put("total", messageService.getConversationTotalLetterCount(localUserId, msg.getConversationId()));
            int targetId = msg.getFromId() == localUserId ? msg.getToId() : msg.getFromId();
            User targetUser = userService.selectUserById(targetId);
            vo.put("user", targetUser);
            vos.add(vo);
        }
        model.addAttribute("totalUnreadLetterCount", messageService.getUnReadLetterCount(localUserId));
        model.addAttribute("totalUnreadNoticeCount", messageService.getUnReadNoticeCount(localUserId));
        model.addAttribute("conversations", vos);
        return "site/letter";
    }

    @RequestMapping(path = "/notices", method = RequestMethod.GET)
    public String noticeList(Model model) {
        model.addAttribute("commentNotice", this.parseNoticeVo(TOPIC_COMMENT));//获取最新一条评论通知
        model.addAttribute("likeNotice", this.parseNoticeVo(TOPIC_LIKE));//获取最新一条点赞通知
        model.addAttribute("followNotice", this.parseNoticeVo(TOPIC_FOLLOW));//获取最新一条关注通知
        User loginUser = hostHolder.get();
        model.addAttribute("totalUnreadLetterCount", messageService.getUnReadLetterCount(loginUser.getId()));
        model.addAttribute("totalUnreadNoticeCount", messageService.getUnReadNoticeCount(loginUser.getId()));
        return "site/notice";
    }

    private Map<String, Object> parseNoticeVo(String topic) {
        User loginUser = hostHolder.get();
        Map<String, Object> noticeVo = null;
        Message message = messageService.getLatestNotice(loginUser.getId(), topic);
        if (null != message) {
            noticeVo = new HashMap<>();
            message.setContent(HtmlUtils.htmlUnescape(message.getContent()));
            noticeVo.put("message", message);
            Map<String, Integer> data = LitchiUtil.parseObject(message.getContent(), HashMap.class);
            noticeVo.put("actorUser", userService.selectUserById(data.get("userId")));
            noticeVo.put("entityType", data.get("entityType"));
            noticeVo.put("entityId", data.get("entityId"));
            noticeVo.put("postId", data.get("postId"));//当实体是comment时方便链接跳转
            noticeVo.put("unReadCount", messageService.getTopicUnReadNoticeCount(loginUser.getId(), topic));
            noticeVo.put("totalCount", messageService.getTopicTotalNoticeCount(loginUser.getId(), topic));
        }
        return noticeVo;
    }

}
