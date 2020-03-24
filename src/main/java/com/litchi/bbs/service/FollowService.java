package com.litchi.bbs.service;

import com.litchi.bbs.util.JedisAdapter;
import com.litchi.bbs.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import javax.validation.constraints.NotNull;
import java.util.*;

/**
 * @author CuiWJ
 * Created on 2018/12/12
 */
@Service
public class FollowService {
    @Autowired
    private JedisAdapter jedisAdapter;

    /**
     * 用户关注实体
     *
     * @param userId     用户的id
     * @param entityType 被关注实体的类型
     * @param entityId   被关注实体的id
     * @return 成功true，失败false
     */
    public boolean follow(int userId, int entityType, int entityId) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        Date date = new Date();
        try (Jedis jedis = jedisAdapter.getJedis();
             Transaction tx = jedis.multi()) {
            //用户followee增加实体id
            tx.zadd(followeeKey, date.getTime(), String.valueOf(entityId));
            //实体follower增加用户id
            tx.zadd(followerKey, date.getTime(), String.valueOf(userId));
            List<Object> res = tx.exec();
            return res.size() == 2 && (Long) res.get(0) == 1 && (Long) res.get(1) == 1;
        }
    }

    /**
     * 用户对实体取消关注
     *
     * @param userId     用户的id
     * @param entityType 被关注实体的类型
     * @param enityId    被关注实体的id
     * @return 成功true，失败false
     */
    public boolean unfollow(int userId, int entityType, int enityId) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, enityId);
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        try(Jedis jedis = jedisAdapter.getJedis();
            Transaction tx = jedis.multi()){
            //用户followee移除实体id
            tx.zrem(followeeKey, String.valueOf(enityId));
            //实体follower移除用户id
            tx.zrem(followerKey, String.valueOf(userId));
            List<Object> res = tx.exec();
            return res.size() == 2 && (Long) res.get(0) == 1 && (Long) res.get(1) == 1;
        }
    }

    /**
     * 获取实体的follower数量
     *
     * @param entityType
     * @param entityId
     * @return
     */
    public long getFollowerCount(int entityType, int entityId) {
        return jedisAdapter.zcard(RedisKeyUtil.getFollowerKey(entityType, entityId));
    }

    /**
     * 获取用户指定类型的followee数量
     *
     * @param userId
     * @param entityType
     * @return
     */
    public long getFolloweeCount(int userId, int entityType) {
        return jedisAdapter.zcard(RedisKeyUtil.getFolloweeKey(userId, entityType));
    }

    /**
     * 判断用户是否是实体的follower
     *
     * @param userId
     * @param entityType
     * @param entityId
     * @return 是返回true, 不是返回false
     */
    public boolean isFollower(int userId, int entityType, int entityId) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return jedisAdapter.zscore(followerKey, String.valueOf(userId)) != null;
    }

    /**
     * 获取实体的follower
     *
     * @param entityType
     * @param entityId
     * @param offset     偏移量
     * @param count      数量
     * @return 实体的follower的id集合
     */
    public List<Integer> getFollowers(int entityType, int entityId, int offset, int count) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        Set<String> res = jedisAdapter.zrevrange(followerKey, offset, offset + count);
        return parseElements(res);
    }

    /**
     * 获取实体前count位follower的id（按关注时间倒序）
     *
     * @param entityType
     * @param entityId
     * @param count
     * @return 实体前count位follower的id集合
     */
    public List<Integer> getFollowers(int entityType, int entityId, int count) {
        return getFollowers(entityType, entityId, 0, count);
    }

    /**
     * 获取用户的follower
     *
     * @param userId
     * @param entityType
     * @param offset     偏移量
     * @param count      数量
     * @return 用户的followee的id集合
     */
    public List<Integer> getFollowees(int userId, int entityType, int offset, int count) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        Set<String> res = jedisAdapter.zrevrange(followeeKey, offset, offset + count);
        return parseElements(res);
    }

    /**
     * 获取用户前count位followee（按关注时间倒序）
     *
     * @param userId
     * @param entityType
     * @param count
     * @return 用户前count位followee的id集合
     */
    public List<Integer> getFollowees(int userId, int entityType, int count) {
        return getFollowees(userId, entityType, 0, count);
    }

    /**
     * 将集合元素类型转换为整型
     *
     * @param set
     * @return
     */
    private List<Integer> parseElements(@NotNull Set<String> set) {
        List<Integer> res = new ArrayList<>();
        for (String element : set) {
            res.add(Integer.parseInt(element));
        }
        return res;
    }
}
