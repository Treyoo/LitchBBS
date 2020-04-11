package com.litchi.bbs.service;

import com.litchi.bbs.BbsApplication;
import com.litchi.bbs.entity.DiscussPost;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.Random;

/**
 * @author cuiwj
 * @date 2020/4/10
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = BbsApplication.class)
public class CaffeineTests {
    private static final Logger logger = LoggerFactory.getLogger(CaffeineTests.class);
    @Autowired
    private DiscussPostService postService;

    @Test
    public void initDataForTest() {//构造5万条帖子数据用于测试
        final int COUNT = 50000;
        Random random = new Random();
        int minUserId = 153;
        int maxUserId = 158;
        for (int i = 1; i <= COUNT; i++) {
            DiscussPost post = new DiscussPost();
            post.setUserId(random.nextInt(maxUserId) % (maxUserId - minUserId + 1) + minUserId);
            post.setTitle("将进酒");
            post.setContent("\n" +
                    "君不见黄河之水天上来，奔流到海不复回。\n" +
                    "君不见高堂明镜悲白发，朝如青丝暮成雪。\n" +
                    "人生得意须尽欢，莫使金樽空对月。\n" +
                    "天生我材必有用，千金散尽还复来。\n" +
                    "烹羊宰牛且为乐，会须一饮三百杯。\n" +
                    "岑夫子，丹丘生，将进酒，君莫停。\n" +
                    "与君歌一曲，请君为我侧耳听。\n" +
                    "钟鼓馔玉不足贵，但愿长醉不愿醒。\n" +
                    "古来圣贤皆寂寞，惟有饮者留其名。\n" +
                    "陈王昔时宴平乐，斗酒十千恣欢谑。\n" +
                    "主人何为言少钱，径须沽取对君酌。\n" +
                    "五花马，千金裘，呼儿将出换美酒，与尔同销万古愁。");
            post.setCreateTime(new Date());
            post.setScore(Math.random() * 2000);
            postService.addDiscussPost(post);
            if (i % 1000 == 0) {
                logger.info(String.format("已生成%d/%d条帖子数据.", i, COUNT));
            }
        }
    }

    @Test
    public void testCache() {
        System.out.println(postService.selectDiscussPosts(0, 0, 10));
        System.out.println(postService.selectDiscussPosts(0, 0, 10));
        System.out.println(postService.selectDiscussPosts(0, 0, 10));
        System.out.println(postService.selectDiscussPosts(0, 0, 10));
    }
}
