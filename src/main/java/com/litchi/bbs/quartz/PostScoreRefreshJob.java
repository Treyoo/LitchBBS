package com.litchi.bbs.quartz;

import com.litchi.bbs.entity.DiscussPost;
import com.litchi.bbs.entity.EntityType;
import com.litchi.bbs.service.DiscussPostService;
import com.litchi.bbs.service.ElasticsearchService;
import com.litchi.bbs.service.LikeService;
import com.litchi.bbs.util.JedisAdapter;
import com.litchi.bbs.util.RedisKeyUtil;
import com.litchi.bbs.util.constant.DiscussPostConst;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author cuiwj
 * @date 2020/4/13
 */
public class PostScoreRefreshJob implements Job, DiscussPostConst {
    private static final Logger logger = LoggerFactory.getLogger(PostScoreRefreshJob.class);
    @Autowired
    private JedisAdapter jedisAdapter;
    @Autowired
    private DiscussPostService postService;
    @Autowired
    private LikeService likeService;
    @Autowired
    private ElasticsearchService elasticsearchService;

    private static final Date epoch;//网站纪元常量，用于计算帖子分数

    static {
        try {
            epoch = new SimpleDateFormat("yyyy-MM-dd").parse("2020-01-01");
        } catch (ParseException e) {
            throw new RuntimeException("初始化网站纪元失败", e);
        }
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        String redisKey = RedisKeyUtil.getPostNeedCalScoreKey();
        long postCount = jedisAdapter.scard(redisKey);
        if (postCount == 0) {
            logger.info("[任务取消]没有需要计算分数的帖子");
            return;
        }
        logger.info(String.format("[任务开始]正在刷新帖子分数，共有%d篇帖子需要刷新分数", postCount));
        while (jedisAdapter.scard(redisKey) > 0) {
            this.refreshScore(Integer.parseInt(jedisAdapter.spop(redisKey)));
        }
    }

    private void refreshScore(int postId) {
        //计算帖子分数公式：log(精华分 + 评论数*10 + 点赞数*2 + 收藏数*2) + (发布时间 - 网站纪元)
        DiscussPost post = postService.selectById(postId);
        if (post == null) {
            logger.error("[无法计算帖子分数]该帖子不存在，id=" + postId);
            return;
        }
        //是否加精
        boolean highlight = post.getStatus() == STATUS_HIGHLIGHT;
        //评论数量
        int commentCount = post.getCommentCount();
        //点赞数量
        long likeCount = likeService.getLikeCount(EntityType.DISCUSS_POST, postId);
        //计算权重
        double w = (highlight ? 75 : 0) + commentCount * 10 + likeCount * 2/*+followCount*2*/;
        //计算分数,要避免w<1时log算出负数
        double score = Math.log10(Math.max(w, 1))
                + (post.getCreateTime().getTime() - epoch.getTime()) / (1000.0 * 3600 * 24);
        postService.updateDiscussScore(postId, score);
    }
}
