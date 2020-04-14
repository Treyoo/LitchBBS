package com.litchi.bbs.service;

import com.alibaba.fastjson.TypeReference;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.litchi.bbs.dao.DiscussPostDAO;
import com.litchi.bbs.entity.DiscussPost;
import com.litchi.bbs.util.JedisAdapter;
import com.litchi.bbs.util.LitchiUtil;
import com.litchi.bbs.util.RedisKeyUtil;
import com.litchi.bbs.util.constant.DiscussPostConst;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author cuiwj
 * @date 2020/3/8
 */
@Service
public class DiscussPostService implements DiscussPostConst {
    private static final Logger logger = LoggerFactory.getLogger(DiscussPostService.class);
    @Autowired
    private DiscussPostDAO discussPostDAO;
    @Autowired
    private SensitiveService sensitiveService;
    @Autowired
    private JedisAdapter jedisAdapter;
    @Value("${caffeine.posts.cache.max-size}")
    private int caffeineCacheMaxSize;
    @Value("${caffeine.posts.cache.expire-seconds}")
    private long caffeineCacheExpireSeconds;
    @Value("${redis.posts.cache.expire-seconds}")
    private int redisCacheExpireSeconds;

    private LoadingCache<String, List<DiscussPost>> postsCache;
    private LoadingCache<Integer, Integer> postsRowsCache;

    @PostConstruct
    private void init() {
        //初始化帖子Caffeine缓存
        this.postsCache = Caffeine.newBuilder()
                .maximumSize(caffeineCacheMaxSize)
                .expireAfterWrite(caffeineCacheExpireSeconds, TimeUnit.SECONDS)
                .build(new CacheLoader<String, List<DiscussPost>>() {
                    @Nullable
                    @Override
                    public List<DiscussPost> load(@NonNull String key) throws Exception {
                        if (StringUtils.isBlank(key)) {
                            throw new IllegalArgumentException("key不能为空");
                        }
                        String[] params = key.split(":");
                        if (params.length != 2) {
                            throw new IllegalArgumentException("key格式错误");
                        }
                        int offset = Integer.parseInt(params[0]);
                        int limit = Integer.parseInt(params[1]);
                        return getPostsFromRedisCache(offset, limit);
                    }
                });
        //初始化帖子总数Caffeine缓存
        this.postsRowsCache = Caffeine.newBuilder()
                .maximumSize(caffeineCacheMaxSize)
                .expireAfterWrite(caffeineCacheExpireSeconds, TimeUnit.SECONDS)
                .build(this::getPostRowsFromRedisCache);
    }

    public List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit, int orderMode) {
        if (userId == 0 && orderMode == ORDER_BY_SCORE) {//访问首页时传入userId是0，仅缓存首页最热帖子列表
            return postsCache.get(offset + ":" + limit);
        }
        logger.debug("Load DiscussPost from DB.");
        return discussPostDAO.selectDiscussPosts(userId, offset, limit, orderMode);
    }

    public List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit) {
        return this.selectDiscussPosts(userId, offset, limit, ORDER_BY_CREATE_TIME);
    }

    public int getDiscussRows(int userId) {
        if (userId == 0) {
            return postsRowsCache.get(userId);
        }
        logger.debug("Load DiscussPost rows from DB.");
        return discussPostDAO.getDiscussPostRows(userId);
    }

    /**
     * 新增问题，并过滤html标签和敏感词
     *
     * @param discussPost
     * @return 新增成功返回问题id, 失败返回0
     */
    public int addDiscussPost(DiscussPost discussPost) {
        //过滤html标签
        discussPost.setTitle(HtmlUtils.htmlEscape(discussPost.getTitle()));
        discussPost.setContent(HtmlUtils.htmlEscape(discussPost.getContent()));

        //过滤敏感词
        discussPost.setTitle(sensitiveService.filter(discussPost.getTitle()));
        discussPost.setContent(sensitiveService.filter(discussPost.getContent()));

        return discussPostDAO.addDiscussPost(discussPost) > 0 ? discussPost.getId() : 0;
    }

    public DiscussPost selectById(int id) {
        return discussPostDAO.selectById(id);
    }

    public void updateDiscussType(int postId, int type) {
        discussPostDAO.updateDiscussType(postId, type);
    }

    public void updateDiscussStatus(int postId, int status) {
        discussPostDAO.updateDiscussStatus(postId, status);
    }

    public void updateDiscussScore(int postId, double score) {
        discussPostDAO.updateDiscussScore(postId, score);
    }

    public int getDiscussType(int postId) {
        return discussPostDAO.selectDiscussType(postId);
    }

    public int getDiscussStatus(int postId) {
        return discussPostDAO.selectDiscussStatus(postId);
    }

    /**
     * 从redis缓存获取帖子列表,若没有则自动初始化redis缓存
     */
    private List<DiscussPost> getPostsFromRedisCache(int offset, int limit) {
        logger.debug("Load DiscussPosts from redis.");
        String redisKey = RedisKeyUtil.getPostsKey(offset, limit);
        String result = jedisAdapter.get(redisKey);
        if (result == null) {//初始化redis缓存
            //TODO 实现限制缓存数量
            logger.debug("Load DiscussPost list from DB.");
            List<DiscussPost> posts = discussPostDAO.selectDiscussPosts(0, offset, limit, ORDER_BY_SCORE);
            jedisAdapter.setex(redisKey, LitchiUtil.toJSONString(posts), redisCacheExpireSeconds);
            return posts;
        }
        return LitchiUtil.parseObject(result, new TypeReference<List<DiscussPost>>() {
        });
    }

    /**
     * 从redis缓存获取帖子总数,若没有则自动初始化redis缓存
     */
    private int getPostRowsFromRedisCache(int userId) {
        logger.debug("Load DiscussPost rows from redis.");
        String redisKey = RedisKeyUtil.getPostRowsKey(userId);
        String result = jedisAdapter.get(redisKey);
        if (result == null) {//初始化redis缓存
            logger.debug("Load DiscussPost rows from DB.");
            int rows = discussPostDAO.getDiscussPostRows(userId);
            jedisAdapter.setex(redisKey, String.valueOf(rows), redisCacheExpireSeconds);
            return rows;
        }
        return Integer.parseInt(result);
    }
}
