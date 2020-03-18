package com.litchi.bbs.entity;

import java.util.Date;

/**
 * author:CuiWJ
 * date:2018/12/6
 */
public class LoginToken {
    private int id;
    private int userId;
    private String token;
    private Date expired;
    private int status;//0有效，1失效

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getExpired() {
        return expired;
    }

    public void setExpired(Date expired) {
        this.expired = expired;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
