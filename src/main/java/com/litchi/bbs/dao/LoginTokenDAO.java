package com.litchi.bbs.dao;

import com.litchi.bbs.entity.LoginToken;
import org.apache.ibatis.annotations.*;

/**
 * author:CuiWJ
 * date:2018/12/6
 */
@Mapper
public interface LoginTokenDAO {
    String TABLE_NAME = " login_token ";
    String INSERT_FIELDS = " user_id, expired, status, token ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    @Insert({"insert into", TABLE_NAME, "(", INSERT_FIELDS, ") values (#{userId},#{expired},#{status},#{token})"})
    int addToken(LoginToken loginToken);

    @Select({"select", SELECT_FIELDS, "from", TABLE_NAME, "where token=#{token}"})
    LoginToken selectByToken(String token);

    @Update({"update", TABLE_NAME, "set status=#{status} where token=#{token}"})
    void updateStatus(@Param("token") String token, @Param("status") int status);
}
