package com.litchi.bbs.service;

import com.litchi.bbs.dao.UserDAO;
import com.litchi.bbs.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author cuiwj
 * @date 2020/3/8
 */
@Service
public class UserService {
    @Autowired
    private UserDAO userDAO;

    public User selectUserById(int userId) {
        return userDAO.selectById(userId);
    }
}
