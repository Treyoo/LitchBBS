package com.litchi.bbs.service;

import com.litchi.bbs.entity.EntityType;
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
     * @param entityUserId 实体拥有者id
     * @return 实体的被赞数量
     */
    public long like(int userId, int entityType, int entityId, int entityUserId) {
        String likeKey = RedisKeyUtil.getBizLikeKey(entityType, entityId);
        String dislikeKey = RedisKeyUtil.getBizDislikeKey(entityType, entityId);
        try (Jedis jedis = jedisAdapter.getJedis();
             Transaction tx = jedis.multi()) {//事务管理
            switch (this.getLikeStatus(userId, entityType, entityId)) {
                case DISLIKE://从dislike集合移除
                    tx.srem(dislikeKey, String.valueOf(userId));
                    //统计相应用户收到全部赞踩的数量
                    //TODO: decr可能出现负数
                    tx.decr(RedisKeyUtil.getEntityDislikeCountKey(EntityType.USER, entityUserId));
                    tx.incr(RedisKeyUtil.getEntityLikeCountKey(EntityType.USER, entityUserId));
                    break;
                case LIKE://从like集合移除（取消赞）
                    tx.srem(likeKey, String.valueOf(userId));
                    tx.decr(RedisKeyUtil.getEntityLikeCountKey(EntityType.USER, entityUserId));
                    break;
                case NONE:
                    tx.sadd(likeKey, String.valueOf(userId));
                    tx.incr(RedisKeyUtil.getEntityLikeCountKey(EntityType.USER, entityUserId));
                    break;
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
     * @param entityUserId 实体拥有者id
     * @return 实体的被赞数量
     */
    public long dislike(int userId, int entityType, int entityId, int entityUserId) {
        String likeKey = RedisKeyUtil.getBizLikeKey(entityType, entityId);
        String dislikeKey = RedisKeyUtil.getBizDislikeKey(entityType, entityId);
        try (Jedis jedis = jedisAdapter.getJedis();
             Transaction tx = jedis.multi()) {
            switch (this.getLikeStatus(userId, entityType, entityId)) {
                case DISLIKE://从dislike集合移除
                    tx.srem(dislikeKey, String.valueOf(userId));
                    //统计相应用户收到全部踩的数量
                    tx.decr(RedisKeyUtil.getEntityDislikeCountKey(EntityType.USER, entityUserId));
                    break;
                case LIKE://从like集合移除（取消赞）
                    tx.srem(likeKey, String.valueOf(userId));
                    //统计相应用户收到全部赞的数量
                    tx.decr(RedisKeyUtil.getEntityLikeCountKey(EntityType.USER, entityUserId));
                    tx.incr(RedisKeyUtil.getEntityDislikeCountKey(EntityType.USER, entityUserId));
                    break;
                case NONE:
                    tx.sadd(likeKey, String.valueOf(userId));
                    tx.incr(RedisKeyUtil.getEntityDislikeCountKey(EntityType.USER, entityUserId));
                    break;
            }
            tx.exec();
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
    public int getLikeStatus(int userId, int entityType, int entityId) {
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

    /**
     * @param userId
     * @return 用户收到全部赞的数量（帖子+评论）
     */
    public String getUserTotalLikedCount(int userId){
        return jedisAdapter.get(RedisKeyUtil.getEntityLikeCountKey(EntityType.USER,userId));
    }

    /**
     * @param userId
     * @return 用户收到全部踩的数量（帖子+评论）
     */
    public String getUserTotalDislikedCount(int userId){
        return jedisAdapter.get(RedisKeyUtil.getEntityDislikeCountKey(EntityType.USER,userId));
    }
}
