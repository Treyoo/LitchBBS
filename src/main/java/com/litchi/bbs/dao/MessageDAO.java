package com.litchi.bbs.dao;

import com.litchi.bbs.entity.Message;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * author:CuiWJ
 * date:2018/12/8
 */
@Mapper
public interface MessageDAO {
    String TABLE_NAME = " message ";
    String INSERT_FIELDS = " from_id,to_id,content,create_time,status,conversation_id ";
    String SELECT_FIELDS = " id," + INSERT_FIELDS;

    @Insert({"insert into", TABLE_NAME, "(", INSERT_FIELDS,
            ") values (#{fromId},#{toId},#{content},#{createTime},#{status},#{conversationId})"})
    int addMessage(Message message);

    List<Message> getConversationDetail(@Param("conversationId") String conversationId,
                                        @Param("offset") int offset,
                                        @Param("limit") int limit);

    /**
     * 获取私信数量
     *
     * @param userId         当前用户id
     * @param conversationId 会话id,为null时代表全部会话
     * @param status         哪种状态的私信，为-1时代表全部(未读+已读)
     * @return 私信数量
     */
    int getLetterCount(@Param("userId") int userId,
                       @Param("conversationId") String conversationId,
                       @Param("status") int status);

    /**
     * 设置指定conversation的所有未读消息变为已读状态
     *
     * @param conversationId
     * @param userId
     * @return
     */
    @Update({"update", TABLE_NAME, "set has_read=1 where conversation_id=#{conversationId}",
            "and has_read=0 and to_id=#{userId}"})
    boolean updateRead(@Param("conversationId") String conversationId,
                       @Param("userId") int userId);

    /**
     * 获取指定用户站内信会话列表
     *
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    List<Message> getConversationList(@Param("userId") int userId,
                                      @Param("offset") int offset,
                                      @Param("limit") int limit);

    /**
     * 获取会话数量
     *
     * @param userId 当前用户
     * @return 会话数量
     */
    int getConversationCount(@Param("userId") int userId);

    Message getLatestNotice(int userId, String topic);

    int getNoticeCount(int userId, String topic, int status);
}
