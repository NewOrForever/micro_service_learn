package com.example.service;

import com.example.entity.User;

import java.util.ArrayList;
import java.util.List;

/**
 * ClassName:UserServiceMock
 * Package:com.example.service
 * Description:
 *
 * @Date:2022/7/28 14:16
 * @Author:qs@1.com
 */
public class UserServiceDubboMock implements UserService{
    @Override
    public List<User> list() {
        List<User> list = new ArrayList<>();
        list.add(new User(0, "=========> mock"));
        return list;
    }

    @Override
    public User getById(Integer id) {
        return null;
    }
}
