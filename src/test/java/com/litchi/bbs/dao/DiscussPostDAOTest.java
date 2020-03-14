package com.litchi.bbs.dao;

import com.litchi.bbs.entity.DiscussPost;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;


/**
 * @author cuiwj
 * @date 2020/3/8
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class DiscussPostDAOTest {
    @Autowired
    private DiscussPostDAO discussPostDAO;

    @Test
    public void testSelectDiscussPost() {
        List<DiscussPost> list = discussPostDAO.selectDiscussPosts(0, 1, 3);
        Assert.assertNotNull(list);
        for (DiscussPost discussPost : list) {
            System.out.println(discussPost.getTitle());
        }
    }

    @Test
    public void testAddDiscussPost() {
        DiscussPost discussPost = new DiscussPost();
        discussPost.setTitle("Test add discuss post");
        discussPost.setContent("test!");
        discussPost.setCreatedDate(new Date());
        discussPost.setScore(88.0);
        discussPost.setType(1);
        discussPost.setType(2);
        discussPost.setUserId(100);
        discussPostDAO.addDiscussPost(discussPost);
        Assert.assertNotEquals(0, discussPost.getId());
    }

    @Test
    public void testUpdateCommentCount() {
        discussPostDAO.updateCommentCount(282, 861);
        Assert.assertEquals(861,
                discussPostDAO.selectById(282).getCommentCount());
    }

    @Test
    public void testSelectLatestDiscussPost(){
        List<DiscussPost> discussPosts = discussPostDAO.selectLatestDiscussPosts(100,0,20);
        Assert.assertFalse(discussPosts.isEmpty());
    }
}
