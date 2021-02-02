package com.gaga.gateway.vo;

import java.io.Serializable;

/**
 * 用户信息实体类
 * @Author fuGaga
 * @Date 2021/1/6 10:17
 * @Version 1.0
 */
public class UserVo implements Serializable {

    private static final long serialVersionUID = 7681363498656774936L;

    private String username;

    private String password;

    private String userToken;

    public String getUsername() {
        return username;
    }

    public UserVo(String username, String userToken) {
        this.username = username;
        this.userToken = userToken;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    @Override
    public String toString() {
        return "UserVo{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", userToken='" + userToken + '\'' +
                '}';
    }
}
