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
                                         @Param("limit") int limit);
    int getDiscussPostRows(@Param("userId") int userId);

    int addDiscussPost(DiscussPost discussPost);

    DiscussPost selectById(int id);

    List<DiscussPost> selectLatestDiscussPosts(@Param("userId") int userId, @Param("offset") int offset,
                                         @Param("limit") int limit);

    void updateCommentCount(@Param("id") int id, @Param("commentCount") int commentCount);
}
