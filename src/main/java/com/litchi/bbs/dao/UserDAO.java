package com.litchi.bbs.dao;

import com.litchi.bbs.entity.User;
import org.apache.ibatis.annotations.*;

/**
 * @author cuiwj
 * @date 2020/3/6
 */
@Mapper
public interface UserDAO {
    String TABLE_NAME = " user ";
    String INSERT_FIELDS = " username,password,salt,header_url ";
    String SELECT_FIELDS = " id," + INSERT_FIELDS;

    @Insert({"insert into", TABLE_NAME, "(", INSERT_FIELDS, ") values (#{username},#{password},#{salt},#{headerUrl})"})
    int addUser(User user);

    @Select({"select", SELECT_FIELDS, "from", TABLE_NAME, "where id=#{id}"})
    User selectById(int id);

    @Select({"select", SELECT_FIELDS, "from", TABLE_NAME, "where username=#{username}"})
    User selectByName(String username);

    @Update({"update", TABLE_NAME, "set password=#{password} where id=#{id}"})
    void updatePassword(User user);

    @Delete({"delete from", TABLE_NAME, "where id=#{id}"})
    void deleteById(int id);
}
