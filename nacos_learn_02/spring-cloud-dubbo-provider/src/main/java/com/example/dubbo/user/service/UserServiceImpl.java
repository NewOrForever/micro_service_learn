package com.example.dubbo.user.service;

import com.example.dubbo.user.mapper.UserMapper;
import com.example.entity.User;
import com.example.service.UserService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ClassName:UserServiceImpl
 * Package:com.example.dubbo.service.impl
 * Description:
 *
 * @Date:2022/6/21 13:23
 * @Author:qs@1.com
 */
@DubboService
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public List<User> list() {
        return userMapper.list();
    }

    @Override
    public User getById(Integer id) {
        return userMapper.getById(id);
    }
}
