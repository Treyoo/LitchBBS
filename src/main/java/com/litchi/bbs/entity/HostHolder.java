package com.litchi.bbs.entity;

import org.springframework.stereotype.Component;

/**
 * author:CuiWJ
 * date:2018/12/7
 */
@Component
public class HostHolder {
    private ThreadLocal<User> users = new ThreadLocal<>();

    public void set(User user) {
        users.set(user);
    }

    public User get() {
        return users.get();
    }

    public void clear(){
        users.remove();
    }
}
