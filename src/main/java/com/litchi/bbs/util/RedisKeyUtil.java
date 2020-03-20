package com.litchi.bbs.util;

/**
 * 本类用于规范存入redis的key
 *
 * @author CuiWJ
 * Created on 2018/12/10
 */
public class RedisKeyUtil {
    private static final String SPLIT = ":";
    private static final String BIZ_LIKE = "BIZ_LIKE";
    private static final String BIZ_DISLIKE = "BIZ_DISLIKE";
    private static final String BIZ_QUESTION = "BIZ_QUESTION";
    private static final String BIZ_COMMENT = "BIZ_COMMENT";
    private static final String EVENTS = "EVENTS";
    private static final String BIZ_FOLLOWER = "FOLLOWER";
    private static final String BIZ_FOLLOWEE = "FOLLOWEE";
    private static final String BIZ_TIMELINE = "TIMELINE";

    public static String getBizLikeKey(int entityType, int entityId) {
        return BIZ_LIKE + SPLIT + entityType + SPLIT + entityId;
    }

    public static String getBizDislikeKey(int entityType, int entityId) {
        return BIZ_DISLIKE + SPLIT + entityType + SPLIT + entityId;
    }

    public static String getEventQueueKey() {
        return EVENTS;
    }

    /**
     * 获取指定实体所有粉丝的redis key
     *
     * @param entityType
     * @param entityId
     * @return FOLLOWER:实体类型:实体id
     */
    public static String getFollowerKey(int entityType, int entityId) {
        return BIZ_FOLLOWER + SPLIT + String.valueOf(entityType) + SPLIT + String.valueOf(entityId);
    }

    /**
     * 获取指定用户对某一类实体的关注列表的redis key
     *
     * @param userId
     * @param entityType
     * @return FOLLOWEE:用户id:实体类型
     */
    public static String getFolloweeKey(int userId, int entityType) {
        return BIZ_FOLLOWEE + SPLIT + String.valueOf(userId) + SPLIT + String.valueOf(entityType);
    }

    public static String getTimelinetKey(int userId) {
        return BIZ_TIMELINE + SPLIT + String.valueOf(userId);
    }
}
