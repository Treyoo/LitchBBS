package com.litchi.bbs.service;

import com.litchi.bbs.BbsApplication;
import com.litchi.bbs.entity.User;
import com.litchi.bbs.util.LitchiUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author cuiwj
 * @date 2020/3/10
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = BbsApplication.class)
public class UserServiceTests {
    @Autowired
    private UserService userService;

    @Test
    public void testRegister(){
        User user = new User();
        String username = LitchiUtil.genRandomString().substring(0,3);
        user.setUsername(username);
        user.setEmail(username+"@mail.com");
        user.setPassword("123");
        userService.register(user);
    }
}
