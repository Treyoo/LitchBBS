package com.litchi.bbs.dao;

import com.litchi.bbs.entity.Message;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @author cuiwj
 * @date 2020/3/18
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Rollback
public class MessageDAOTests {
    @Autowired
    private MessageDAO messageDAO;

    @Test
    public void testGetConversationList() {
        List<Message> conversationList = messageDAO.getConversationList(111, 0, 10);
        Assert.assertFalse(conversationList.isEmpty());
    }

    @Test
    public void testGetConversationCount() {
        int count = messageDAO.getConversationCount(111);
        Assert.assertTrue(count > 0);
        System.out.println("Conversation count=" + count);
    }

    @Test
    public void testGetLatestNotice(){
        int userId=149;
        String topic = "comment";
        Message message = messageDAO.getLatestNotice(userId,topic);
        Assert.assertNotNull(message);
        Assert.assertEquals(topic,message.getConversationId());
    }
}
