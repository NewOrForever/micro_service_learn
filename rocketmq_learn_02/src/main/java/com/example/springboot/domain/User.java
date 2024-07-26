package com.example.springboot.domain;

import lombok.Data;

/**
 * ClassName:User
 * Package:com.example.springboot.domain
 * Description:
 *
 * @Date:2024/7/23 16:20
 * @Author:qs@1.com
 */
@Data
public class User {
    private String name;
    private Integer age;
    private String address;

    public User() {
    }

    public User(String name, Integer age, String address) {
        this.name = name;
        this.age = age;
        this.address = address;
    }
}
