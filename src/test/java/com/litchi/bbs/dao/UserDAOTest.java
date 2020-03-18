package com.litchi.bbs.dao;

import com.litchi.bbs.entity.User;
import com.litchi.bbs.util.LitchiUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

/**
 * @author cuiwj
 * @date 2020/3/6
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserDAOTest {
    @Autowired
    UserDAO userDAO;

    @Test
    public void testAddUser() {
        User user = new User();
        String username = "TestMyBatis+" + LitchiUtil.genRandomString().substring(0, 2);
        user.setUsername(username);
        userDAO.addUser(user);
        Assert.assertNotEquals(0, user.getId());//测试id自动回填
        Assert.assertNotNull(userDAO.selectByName(username));
    }
}