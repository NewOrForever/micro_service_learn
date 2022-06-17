package com.example.consumer.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

/**
 * ClassName:UserEntity
 * Package:com.example.consumer.entity
 * Description:
 *
 * @Date:2022/6/16 15:20
 * @Author:qs@1.com
 */
@TableName("t_user")
public class UserEntity implements Serializable {
    private static final long serialVersionUID = 9091775706459611832L;
    /**
     *
     */
    @TableId
    private Integer id;
    /**
     *
     */
    private String username;
    /**
     *
     */
    private Integer age;

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

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}
