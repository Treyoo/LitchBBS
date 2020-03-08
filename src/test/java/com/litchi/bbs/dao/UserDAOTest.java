package com.litchi.bbs.dao;

import com.litchi.bbs.entity.User;
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
    public void test() {
        User user = new User();
        String username = "TestMyBatis";
        user.setUsername(username);
        userDAO.addUser(user);
        Assert.assertNotNull(userDAO.selectByName(username));

    }
}