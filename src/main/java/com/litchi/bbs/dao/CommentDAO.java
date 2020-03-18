package com.litchi.bbs.dao;

import com.litchi.bbs.entity.Comment;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * author:CuiWJ
 * date:2018/12/8
 */
@Mapper
public interface CommentDAO {
    String TABLE_NAME = " comment ";
    String INSERT_FIELDS = " entity_type,entity_id,user_id,content,create_time,status,target_id ";
    String SELECT_FIELDS = " id," + INSERT_FIELDS;

    @Insert({"insert into", TABLE_NAME, "(", INSERT_FIELDS,
            ") values(#{entityType},#{entityId},#{userId},#{content},#{createTime},#{status},#{targetId})"})
    int addComment(Comment comment);

    @Select({"select", SELECT_FIELDS, "from", TABLE_NAME, "where entity_type=#{entityType} and ",
            "entity_id=#{entityId} and status=0 order by create_time desc limit #{offset},#{limit}"})
    List<Comment> selectByEntity(@Param("entityType") int entityType,
                                 @Param("entityId") int entityId,
                                 @Param("offset") int offset,
                                 @Param("limit") int limit);

    @Select({"select count(id) ", "from", TABLE_NAME, "where entity_type=#{entityType} and ",
            "entity_id=#{entityId} and status=0"})
    int getCommentCountByEntity(@Param("entityType") int entityType,
                                @Param("entityId") int entityId);

    @Update({"update", TABLE_NAME, "set status=#{status} where id=#{id}"})
    boolean updateStatus(@Param("id") int id, @Param("status") int status);

    @Select({"select", SELECT_FIELDS, "from", TABLE_NAME, "where id=#{id}"})
    Comment selectById(@Param("id") int id);

    @Select({"select count(id) from",TABLE_NAME,"where user_id=#{userId}"})
    int getCommentCountByUser(int userId);
}
