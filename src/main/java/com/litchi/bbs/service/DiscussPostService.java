package com.litchi.bbs.service;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.litchi.bbs.dao.DiscussPostDAO;
import com.litchi.bbs.entity.DiscussPost;
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
public class DiscussPostService {
    private static final Logger logger = LoggerFactory.getLogger(DiscussPostService.class);
    @Autowired
    private DiscussPostDAO discussPostDAO;
    @Autowired
    private SensitiveService sensitiveService;
    @Value("${caffeine.posts.cache.max-size}")
    private int cacheMaxSize;
    @Value("${caffeine.posts.cache.expire-seconds}")
    private long cacheExpireSeconds;

    private LoadingCache<String, List<DiscussPost>> postsCache;
    private LoadingCache<Integer, Integer> postsRowsCache;

    @PostConstruct
    private void init() {
        //初始化帖子Caffeine缓存
        this.postsCache = Caffeine.newBuilder()
                .maximumSize(cacheMaxSize)
                .expireAfterWrite(cacheExpireSeconds, TimeUnit.SECONDS)
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
                        //TODO 从二级缓存redis读数据
                        logger.info("Load DiscussPost from DB.");
                        return discussPostDAO.selectDiscussPosts(0, offset, limit);
                    }
                });
        //初始化帖子总数Caffeine缓存
        this.postsRowsCache = Caffeine.newBuilder()
                .maximumSize(cacheMaxSize)
                .expireAfterWrite(cacheExpireSeconds, TimeUnit.SECONDS)
                .build(key -> {
                    logger.info("Load DiscussPost rows from DB.");
                    return discussPostDAO.getDiscussPostRows(key);
                });
    }

    public List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit) {
        if (userId == 0) {//访问首页时传入userId是0
            return postsCache.get(offset + ":" + limit);
        }
        logger.info("Load DiscussPost from DB.");
        return discussPostDAO.selectDiscussPosts(userId, offset, limit);
    }

    public int getDiscussRows(int userId) {
        if (userId == 0) {
            return postsRowsCache.get(userId);
        }
        logger.info("Load DiscussPost rows from DB.");
        return discussPostDAO.getDiscussPostRows(userId);
    }

    public List<DiscussPost> selectLatestDiscussPosts(int userId, int offset, int limit) {
        return discussPostDAO.selectLatestDiscussPosts(userId, offset, limit);
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

    public int getDiscussType(int postId) {
        return discussPostDAO.selectDiscussType(postId);
    }

    public int getDiscussStatus(int postId) {
        return discussPostDAO.selectDiscussStatus(postId);
    }
}
