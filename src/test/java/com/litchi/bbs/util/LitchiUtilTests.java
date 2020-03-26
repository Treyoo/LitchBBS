package com.litchi.bbs.util;

import com.litchi.bbs.BbsApplication;
import com.litchi.bbs.entity.LoginToken;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

/**
 * @author cuiwj
 * @date 2020/3/26
 */
@RunWith(SpringRunner.class)
public class LitchiUtilTests {
    @Test
    public void testToJSONString(){
        LoginToken token = new LoginToken();
        token.setUserId(1);
        token.setToken(LitchiUtil.genRandomString());
        token.setStatus(0);
        Date date = new Date();
        date.setTime(date.getTime() + 1000 * 3600 * 24 * 7);//设置24*7小时有效期
        token.setExpired(date);
        String JSONStr = LitchiUtil.toJSONString(token);
        System.out.println("序列化结果："+JSONStr);
        LoginToken token1 = LitchiUtil.parseObject(JSONStr,LoginToken.class);
        System.out.println("反序列化结果："+token1);
        Assert.assertEquals(1,token1.getUserId());
    }
}
