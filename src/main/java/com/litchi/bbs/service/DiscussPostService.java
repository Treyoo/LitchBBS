package com.litchi.bbs.service;

import com.litchi.bbs.dao.DiscussPostDAO;
import com.litchi.bbs.entity.DiscussPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author cuiwj
 * @date 2020/3/8
 */
@Service
public class DiscussPostService {
    @Autowired
    private DiscussPostDAO discussPostDAO;

    public List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit) {
        return discussPostDAO.selectDiscussPosts(userId, offset, limit);
    }

    public int getDiscussRows(int userId) {
        return discussPostDAO.getDiscussPostRows(userId);
    }
}
