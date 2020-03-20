package com.litchi.bbs.service;

import com.litchi.bbs.util.JedisAdapter;
import com.litchi.bbs.util.RedisKeyUtil;
import com.litchi.bbs.util.constant.LikeStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

/**
 * @author CuiWJ
 * Created on 2018/12/10
 */
@Service
public class LikeService implements LikeStatus {
    @Autowired
    JedisAdapter jedisAdapter;

    /**
     * 用户对实体点赞
     *
     * @param userId
     * @param entityType
     * @param entityId
     * @return 实体的被赞数量
     */
    public long like(int userId, int entityType, int entityId) {
        String likeKey = RedisKeyUtil.getBizLikeKey(entityType, entityId);
        String dislikeKey = RedisKeyUtil.getBizDislikeKey(entityType, entityId);
        boolean liked = jedisAdapter.sismember(likeKey, String.valueOf(userId));
        try (Jedis jedis = jedisAdapter.getJedis();
             Transaction tx = jedis.multi()) {//事务管理
            //从dislike集合移除（如果有）
            tx.srem(dislikeKey, String.valueOf(userId));
            //向like集合添加（赞）/移除（取消赞）
            if (liked) { //已经赞过再点击赞就是取消赞
                tx.srem(likeKey, String.valueOf(userId));
            } else {
                tx.sadd(likeKey, String.valueOf(userId));
            }
            tx.exec();
        }
        return jedisAdapter.scard(likeKey);
    }

    /**
     * 用户对实体点踩
     *
     * @param userId
     * @param entityType
     * @param entityId
     * @return 实体的被赞数量
     */
    public long dislike(int userId, int entityType, int entityId) {
        String likeKey = RedisKeyUtil.getBizLikeKey(entityType, entityId);
        String dislikeKey = RedisKeyUtil.getBizDislikeKey(entityType, entityId);
        boolean disliked = jedisAdapter.sismember(dislikeKey, String.valueOf(userId));
        try (Jedis jedis = jedisAdapter.getJedis();
             Transaction tx = jedis.multi()) {
            //从like集合移除（如果有）
            tx.srem(likeKey, String.valueOf(userId));
            //向dislike集合添加（踩）/移除（取消踩）
            //已经踩过再点击踩就是取消踩
            if (disliked) {
                tx.srem(dislikeKey, String.valueOf(userId));
            } else {
                tx.sadd(dislikeKey, String.valueOf(userId));
            }
        }
        return jedisAdapter.scard(likeKey);
    }

    /**
     * 获取一个实体被赞数量
     *
     * @param entityType
     * @param entityId
     * @return
     */
    public long getLikeCount(int entityType, int entityId) {
        return jedisAdapter.scard(RedisKeyUtil.getBizLikeKey(entityType, entityId));
    }

    /**
     * 获取一个用户对一个实体是点赞还是点踩
     *
     * @param userId
     * @param entityType
     * @param entityId
     * @return 点赞返回1，点踩返回-1，无返回0
     */
    public int getStatus(int userId, int entityType, int entityId) {
        if (jedisAdapter.sismember(RedisKeyUtil.getBizLikeKey(entityType, entityId),
                String.valueOf(userId))) {
            return LIKE;
        }
        if (jedisAdapter.sismember(RedisKeyUtil.getBizDislikeKey(entityType, entityId),
                String.valueOf(userId))) {
            return DISLIKE;
        }
        return NONE;
    }
}
