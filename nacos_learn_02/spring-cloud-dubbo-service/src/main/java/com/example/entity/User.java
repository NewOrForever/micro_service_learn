package com.example.entity;

import java.io.Serializable;

/**
 * ClassName:User
 * Package:com.example.entity
 * Description:
 *
 * @Date:2022/6/21 13:19
 * @Author:qs@1.com
 */
public class User implements Serializable {

    private static final long serialVersionUID = -2248190721476487645L;

    private Integer id;
    private String username;

    public User() {
    }

    public User(Integer id, String username) {
        this.id = id;
        this.username = username;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
