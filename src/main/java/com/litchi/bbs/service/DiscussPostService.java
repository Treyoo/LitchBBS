package com.litchi.bbs.service;

import com.litchi.bbs.dao.DiscussPostDAO;
import com.litchi.bbs.entity.DiscussPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * @author cuiwj
 * @date 2020/3/8
 */
@Service
public class DiscussPostService {
    @Autowired
    private DiscussPostDAO discussPostDAO;
    @Autowired
    private SensitiveService sensitiveService;

    public List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit) {
        return discussPostDAO.selectDiscussPosts(userId, offset, limit);
    }

    public int getDiscussRows(int userId) {
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

    public DiscussPost selectById(int id){
        return discussPostDAO.selectById(id);
    }
}
