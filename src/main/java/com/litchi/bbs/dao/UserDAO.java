package com.litchi.bbs.dao;

import com.litchi.bbs.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author cuiwj
 * @date 2020/3/6
 */
@Mapper
public interface UserDAO {
    String TABLE_NAME = " user ";
    String INSERT_FIELDS = " username,password,salt,email,header_url," +
            "type,status,activation_code,create_time ";
    String SELECT_FIELDS = " id," + INSERT_FIELDS;

    @Insert({"insert into", TABLE_NAME, "(", INSERT_FIELDS, ") values (#{username},#{password}," +
            "#{salt},#{email},#{headerUrl},#{type},#{status},#{activationCode},#{createTime})"})
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int addUser(User user);

    @Select({"select", SELECT_FIELDS, "from", TABLE_NAME, "where id=#{id}"})
    User selectById(int id);

    @Select({"select", SELECT_FIELDS, "from", TABLE_NAME, "where username=#{username}"})
    User selectByName(String username);

    @Select({"select", SELECT_FIELDS, "from", TABLE_NAME, "where email=#{email}"})
    User selectByEmail(String email);

    List<User> selectUsers(@Param("offset") int offset, @Param("limit") int limit);

    int getUserRows();

    void updateUser(User user);

    @Update({"update", TABLE_NAME, "set password=#{password} where id=#{id}"})
    void updatePassword(User user);

    @Update({"update", TABLE_NAME, "set status=#{status} where id=#{id}"})
    void updateStatus(User user);

    @Update({"update", TABLE_NAME, "set header_url=#{headerUrl} where id=#{userId}"})
    void updateHeaderUrl(int userId, String headerUrl);

    @Delete({"delete from", TABLE_NAME, "where id=#{id}"})
    void deleteById(int id);
}
