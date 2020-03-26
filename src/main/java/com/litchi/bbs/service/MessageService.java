package com.litchi.bbs.service;

import com.litchi.bbs.dao.MessageDAO;
import com.litchi.bbs.entity.HostHolder;
import com.litchi.bbs.entity.Message;
import com.litchi.bbs.util.LitchiUtil;
import com.litchi.bbs.util.constant.MessageStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * author:CuiWJ
 * date:2018/12/9
 */
@Service
public class MessageService implements MessageStatus {
    @Autowired
    MessageDAO messageDAO;
    @Autowired
    SensitiveService sensitiveService;
    @Autowired
    HostHolder hostHolder;

    public int addMessage(Message message) {
        if (message.getFromId() != LitchiUtil.SYSUSER_ID) {
            message.setContent(HtmlUtils.htmlEscape(message.getContent()));
            message.setContent(sensitiveService.filter(message.getContent()));
        }
        return messageDAO.addMessage(message) > 0 ? message.getId() : 0;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED,propagation = Propagation.REQUIRED)
    public List<Message> getConversationDetail(String conversationId, int offset, int limit) {
//        messageDAO.updateRead(conversationId, hostHolder.get().getId());
        return messageDAO.getConversationDetail(conversationId, offset, limit);
    }

    /**
     * @param userId
     * @param conversationId
     * @return 单个会话未读私信数量
     */
    public int getConversationUnReadLetterCount(int userId, String conversationId) {
        return messageDAO.getLetterCount(userId,conversationId,UNREAD);
    }

    /**
     * @param userId
     * @param conversationId
     * @return 单个会话全部私信数量
     */
    public int getConversationTotalLetterCount(int userId, String conversationId) {
        return messageDAO.getLetterCount(userId,conversationId,-1);
    }

    /**
     * @param userId
     * @return 用户全部未读私信数量
     */
    public int getUnReadLetterCount(int userId){
        return messageDAO.getLetterCount(userId,null,UNREAD);
    }

    public boolean updateRead(String conversationId, int userId, int hasRead) {
        return messageDAO.updateRead(conversationId, userId);
    }

    public List<Message> getConversationList(int userId, int offset, int limit) {
        return messageDAO.getConversationList(userId, offset, limit);
    }

    public int getConversationCount(int userId) {
        return messageDAO.getConversationCount(userId);
    }
}
