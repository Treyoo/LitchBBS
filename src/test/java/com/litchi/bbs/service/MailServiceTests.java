package com.litchi.bbs.service;

import com.litchi.bbs.BbsApplication;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * @author cuiwj
 * @date 2020/3/9
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = BbsApplication.class)
public class MailServiceTests {
    @Autowired
    private MailService mailService;
    @Autowired
    private TemplateEngine templateEngine;

    @Test
    public void testSendMail() {
        Assert.assertTrue(mailService.send("413753723@qq.com", "Test测试邮件", "测试JavaMail"));
    }
    @Test
    public void testSendHTMLMail(){
        Context context = new Context();
        context.setVariable("username","暴力蛤蟆");
        String content = templateEngine.process("mail/template",context);
        Assert.assertTrue(mailService.send("413753723@qq.com", "Test测试邮件", content));
    }
}
