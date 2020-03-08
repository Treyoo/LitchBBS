package com.litchi.bbs.dao;

import com.litchi.bbs.entity.DiscussPost;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

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
    public void testSelectDiscussPost(){
        List<DiscussPost> list = discussPostDAO.selectDiscussPosts(0,1,3);
        Assert.assertNotNull(list);
        for (DiscussPost discussPost:list){
            System.out.println(discussPost.getTitle());
        }
    }
}
