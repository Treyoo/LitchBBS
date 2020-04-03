package com.litchi.bbs.util;

import com.litchi.bbs.BbsApplication;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import redis.clients.jedis.BitOP;
import redis.clients.jedis.Jedis;

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
    public void test() {
        jedisAdapter.set("testJedis", "测试Jedis是否正常工作");
        Assert.assertEquals(jedisAdapter.get("testJedis"), "测试Jedis是否正常工作");
        jedisAdapter.del("testJedis");
        Assert.assertNull(jedisAdapter.get("testJedis"));
    }

    //测试HyperLogLog
    @Test
    public void testHyperLogLog() {
        String redisKey1 = "test:HyperLogLog:1";
        final int BOUND = 100000;
        try (Jedis jedis = jedisAdapter.getJedis()) {
            jedis.del(redisKey1);
            for (int i = 1; i <= BOUND; i++) {
                jedis.pfadd(redisKey1, String.valueOf(i));
            }
            for (int i = 1; i <= BOUND; i++) {
                int random = (int) (Math.random() * BOUND + 1);
                jedis.pfadd(redisKey1, String.valueOf(random));
            }
            long count = jedis.pfcount(redisKey1);//统计20万个重复数据的独立总数
            System.out.println("实际独立个数=" + BOUND + ",HyperLogLog统计独立个数=" + count);
        }
    }

    //测试HyperLogLog的合并操作
    @Test
    public void testHyperLogLogUnion() {
        String redisKey1 = "test:HyperLogLog:union:1";
        String redisKey2 = "test:HyperLogLog:union:2";
        String redisKey3 = "test:HyperLogLog:union:3";
        String unionKey = "test:HyperLogLog:union";
        try (Jedis jedis = jedisAdapter.getJedis()) {
            jedis.del(redisKey1, redisKey2, redisKey3, unionKey);
            for (int i = 1; i <= 10000; i++) {
                jedis.pfadd(redisKey1, String.valueOf(i));
            }
            for (int i = 5001; i <= 15000; i++) {
                jedis.pfadd(redisKey2, String.valueOf(i));
            }
            for (int i = 10001; i <= 20000; i++) {
                jedis.pfadd(redisKey3, String.valueOf(i));
            }
            jedis.pfmerge(unionKey, redisKey1, redisKey2, redisKey3);
            long count = jedis.pfcount(unionKey);
            System.out.println("实际独立个数=20000,HyperLogLog统计独立个数=" + count);
        }
    }

    //测试BitMap，BitMap实际是redis对字符串的位操作
    @Test
    public void testBitMap() {
        String redisKey = "test:BitMap:1";
        try (Jedis jedis = jedisAdapter.getJedis()) {
            jedis.del(redisKey);
            jedis.setbit(redisKey, 1, true);
            jedis.setbit(redisKey, 3, true);
            jedis.setbit(redisKey, 5, false);
            Assert.assertFalse(jedis.getbit(redisKey, 0));
            Assert.assertTrue(jedis.getbit(redisKey, 1));
            Assert.assertTrue(jedis.getbit(redisKey, 3));
            Assert.assertFalse(jedis.getbit(redisKey, 5));
            //统计BitMap中1（true）的个数
            long count = jedis.bitcount(redisKey);
            Assert.assertEquals(2, count);
        }
    }

    //测试BitMap或运算
    @Test
    public void testBitMapOROperation() {
        String redisKey1 = "test:BitMap:1";
        String redisKey2 = "test:BitMap:2";
        String redisKey3 = "test:BitMap:3";
        String redisKeyOR = "test:BitMap:OR";
        try (Jedis jedis = jedisAdapter.getJedis()) {
            jedis.del(redisKey1, redisKey2, redisKey3, redisKeyOR);
            jedis.setbit(redisKey1, 0, true);
            jedis.setbit(redisKey1, 1, false);
            jedis.setbit(redisKey2, 0, false);
            jedis.setbit(redisKey2, 1, false);
            jedis.setbit(redisKey2, 0, false);
            jedis.setbit(redisKey3, 1, false);
            jedis.bitop(BitOP.OR, redisKeyOR, redisKey1, redisKey2, redisKey3);
            Assert.assertTrue(jedis.getbit(redisKeyOR, 0));
            Assert.assertFalse(jedis.getbit(redisKeyOR, 1));
        }
    }
}
