package com.litchi.bbs.dao;

import com.litchi.bbs.entity.DiscussPost;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author cuiwj
 * @date 2020/3/8
 */
@Mapper
public interface DiscussPostDAO {
    List<DiscussPost> selectDiscussPosts(@Param("userId") int userId,
                                         @Param("offset") int offset,
                                         @Param("limit") int limit,
                                         @Param("orderMode") int orderMode);

    int getDiscussPostRows(@Param("userId") int userId);

    int addDiscussPost(DiscussPost discussPost);

    DiscussPost selectById(int id);

    void updateCommentCount(@Param("id") int id, @Param("commentCount") int commentCount);

    void updateDiscussType(@Param("id") int id, @Param("type") int type);

    void updateDiscussStatus(@Param("id") int id, @Param("status") int status);

    void updateDiscussScore(@Param("id") int id, @Param("score") double score);

    int selectDiscussType(@Param("id") int id);

    int selectDiscussStatus(@Param("id") int id);
}
