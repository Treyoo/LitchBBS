package com.litchi.bbs.service;

import com.litchi.bbs.BbsApplication;
import com.litchi.bbs.entity.User;
import com.litchi.bbs.util.LitchiUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author cuiwj
 * @date 2020/3/10
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = BbsApplication.class)
@Transactional(transactionManager = "transactionManager")
@Rollback
public class UserServiceTests {
    @Autowired
    private UserService userService;

    @Test
    public void testRegister() {
        User user = new User();
        String username = LitchiUtil.genRandomString().substring(0, 3);
        user.setUsername(username);
        user.setEmail(username + "@mail.com");
        user.setPassword("123");
        userService.register(user);
    }

    @Test
    public void testUpdateUser() {
        final int userId = 150;
        final String headerUrl = "https://w.wallhaven.cc/full/ym/wallhaven-ymwwzd.jpg";
        User user = new User();
        user.setId(userId);
        user.setUsername("老板加辣");
        user.setPassword("123");
        user.setEmail(user.getUsername() + "@test.com");
        user.setHeaderUrl(headerUrl);
        user.setType(2);
        user.setStatus(1);
        userService.updateUser(user);
        user = userService.selectUserById(userId);
        Assert.assertEquals(headerUrl, user.getHeaderUrl());
    }
}
