package com.litchi.bbs.service;

import com.litchi.bbs.BbsApplication;
import com.litchi.bbs.util.JedisAdapter;
import com.litchi.bbs.util.RedisKeyUtil;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author cuiwj
 * @date 2020/4/10
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = BbsApplication.class)
public class StatisticServiceTests {
    @Autowired
    private StatisticService statisticService;
    @Autowired
    private JedisAdapter jedisAdapter;
    private static Date now;
    private static Date tomorrow;
    private static String UVRedisKey1;
    private static String UVRedisKey2;
    private static String DAURedisKey1;
    private static String DAURedisKey2;
    private static String DAUDestRedisKey;

    @BeforeClass
    public static void setupClass() {
        now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        tomorrow = calendar.getTime();
        UVRedisKey1 = RedisKeyUtil.getUVKey(sdf.format(now));
        UVRedisKey2 = RedisKeyUtil.getUVKey(sdf.format(tomorrow));

        DAURedisKey1 = RedisKeyUtil.getDAUKey(sdf.format(now));
        DAURedisKey2 = RedisKeyUtil.getDAUKey(sdf.format(tomorrow));
        DAUDestRedisKey = RedisKeyUtil.getDAUKey(sdf.format(now), sdf.format(tomorrow));
    }

    @Before
    public void setup() {
        jedisAdapter.del(UVRedisKey1);
        jedisAdapter.del(UVRedisKey2);
        jedisAdapter.del(DAURedisKey1);
        jedisAdapter.del(DAURedisKey2);
        jedisAdapter.del(DAUDestRedisKey);
    }

    @After
    public void teardown() {
        jedisAdapter.del(UVRedisKey1);
        jedisAdapter.del(UVRedisKey2);
        jedisAdapter.del(DAURedisKey1);
        jedisAdapter.del(DAURedisKey2);
        jedisAdapter.del(DAUDestRedisKey);
    }


    @Test
    public void testGetSingleDateUV() {
        statisticService.addUV("1.2.3.4", now);
        statisticService.addUV("1.2.3.4", now);
        statisticService.addUV("1.2.3.5", now);
        long uv = statisticService.getUV(now);
        System.out.println("UV=" + uv);
        Assert.assertEquals(2, uv);
    }

    @Test
    public void testGetMultiDateUV() {
        statisticService.addUV("1.2.3.4", now);
        statisticService.addUV("1.2.3.5", now);
        statisticService.addUV("1.2.3.5", tomorrow);
        statisticService.addUV("1.2.3.6", tomorrow);
        long uv = statisticService.getUV(now, tomorrow);
        Assert.assertEquals(3, uv);
        System.out.println("UV=" + uv);
    }

    @Test
    public void testGetSingleDateDAU() {
        statisticService.addDAU(1, now);
        statisticService.addDAU(3, now);
        long dau = statisticService.getDAU(now);
        System.out.println("DAU=" + dau);
        Assert.assertEquals(2, dau);
    }

    @Test
    public void testGetMultiDateDAU() {
        statisticService.addDAU(1, now);
        statisticService.addDAU(2, now);
        statisticService.addDAU(1, tomorrow);
        statisticService.addDAU(3, tomorrow);
        long dau = statisticService.getDAU(now, tomorrow);
        System.out.println("DAU=" + dau);
        Assert.assertEquals(3, dau);
    }
}
