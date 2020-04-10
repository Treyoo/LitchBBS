package com.litchi.bbs.service;

import com.litchi.bbs.entity.User;
import com.litchi.bbs.util.JedisAdapter;
import com.litchi.bbs.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.BitOP;
import redis.clients.jedis.Jedis;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 数据统计
 *
 * @author cuiwj
 * @date 2020/4/10
 */
@Service
public class StatisticService {
    @Autowired
    private JedisAdapter jedisAdapter;

    public void addUV(String ip, Date date) {
        String redisKey = RedisKeyUtil.getUVKey(new SimpleDateFormat("yyyyMMdd").format(date));
        try (Jedis jedis = jedisAdapter.getJedis()) {
            jedis.pfadd(redisKey, ip);
        }
    }

    public long getUV(Date date) {
        return this.getUV(date, date);
    }

    public long getUV(Date begin, Date end) {
        if (begin == null || end == null || begin.after(end)) {
            throw new IllegalArgumentException("开始时间必须早于或等于结束时间！");
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(begin);
        List<String> redisKeys = new ArrayList<>();
        while (!calendar.getTime().after(end)) {
            redisKeys.add(RedisKeyUtil.getUVKey(sdf.format(calendar.getTime())));//收集时间区间内每一天的UV key
            calendar.add(Calendar.DATE, 1);
        }
        try (Jedis jedis = jedisAdapter.getJedis()) {
            return jedis.pfcount(redisKeys.toArray(new String[0]));
        }
    }

    public void addDAU(int userId, Date date) {
        String redisKey = RedisKeyUtil.getDAUKey(new SimpleDateFormat("yyyyMMdd").format(date));
        try (Jedis jedis = jedisAdapter.getJedis()) {
            jedis.setbit(redisKey, userId, true);
        }
    }

    public long getDAU(Date date) {
        return this.getDAU(date, date);
    }

    public long getDAU(Date begin, Date end) {
        if (begin == null || end == null || begin.after(end)) {
            throw new IllegalArgumentException("开始时间必须早于或等于结束时间！");
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(begin);
        List<String> srcKeys = new ArrayList<>();
        String destKey = RedisKeyUtil.getDAUKey(sdf.format(begin), sdf.format(end));
        while (!calendar.getTime().after(end)) {
            srcKeys.add(RedisKeyUtil.getDAUKey(sdf.format(calendar.getTime())));//收集时间区间内每一天的DAU key
            calendar.add(Calendar.DATE, 1);
        }
        try (Jedis jedis = jedisAdapter.getJedis()) {
            if (srcKeys.size() == 1) {
                return jedis.bitcount(srcKeys.get(0));
            } else {
                jedis.bitop(BitOP.OR, destKey, srcKeys.toArray(new String[0]));
                return jedis.bitcount(destKey);
            }
        }
    }
}
