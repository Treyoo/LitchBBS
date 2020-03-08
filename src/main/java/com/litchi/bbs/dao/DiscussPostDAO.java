package com.litchi.bbs.dao;

import com.litchi.bbs.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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
}
