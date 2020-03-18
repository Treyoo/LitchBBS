package com.litchi.bbs.service;

import com.litchi.bbs.dao.CommentDAO;
import com.litchi.bbs.dao.DiscussPostDAO;
import com.litchi.bbs.entity.Comment;
import com.litchi.bbs.entity.DiscussPost;
import com.litchi.bbs.entity.EntityType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * author:CuiWJ
 * date:2018/12/8
 */
@Service
public class CommentService {
    @Autowired
    CommentDAO commentDAO;
    @Autowired
    SensitiveService sensitiveService;
    @Autowired
    DiscussPostDAO discussPostDAO;

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public int addComment(Comment comment) {
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveService.filter(comment.getContent()));
        if (comment.getEntityType() == EntityType.DISCUSS_POST) {
            //相应帖评论数增加1
            DiscussPost post = discussPostDAO.selectById(comment.getEntityId());
            discussPostDAO.updateCommentCount(post.getId(), post.getCommentCount() + 1);
        }
        return commentDAO.addComment(comment) > 0 ? comment.getId() : 0;
    }

    @Transactional
    public boolean deleteComment(Comment comment) {
        if (comment.getEntityType() == EntityType.DISCUSS_POST) {
            //相应帖评论数增减1
            DiscussPost post = discussPostDAO.selectById(comment.getEntityId());
            discussPostDAO.updateCommentCount(post.getId(), post.getCommentCount() - 1);
        }
        return commentDAO.updateStatus(comment.getId(), 1);
    }

    public List<Comment> selectByEntity(int entityType, int entityId, int offset, int limit) {
        return commentDAO.selectByEntity(entityType, entityId, offset, limit);
    }

    public int getCommentCountByEntity(int entityType, int entityId) {
        return commentDAO.getCommentCountByEntity(entityType, entityId);
    }

    public Comment getCommentById(int id) {
        return commentDAO.selectById(id);
    }

    public int getCommentCountByUser(int userId) {
        return commentDAO.getCommentCountByUser(userId);
    }
}
