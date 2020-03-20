package com.litchi.bbs.util;

import com.litchi.bbs.BbsApplication;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * author:CuiWJ
 * date:2018/12/10
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = BbsApplication.class)
public class JedisAdapterTest {
    @Autowired
    private JedisAdapter jedisAdapter;

    @Test
    public void test(){
        jedisAdapter.set("testJedis","测试Jedis是否正常工作");
        Assert.assertEquals(jedisAdapter.get("testJedis"),"测试Jedis是否正常工作");
        jedisAdapter.del("testJedis");
        Assert.assertNull(jedisAdapter.get("testJedis"));
    }
}
