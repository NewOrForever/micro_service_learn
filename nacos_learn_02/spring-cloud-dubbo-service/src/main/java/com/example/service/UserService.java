package com.example.service;

import com.example.entity.User;

import java.util.List;

/**
 * ClassName:UserService
 * Package:com.example.service
 * Description:
 *
 * @Date:2022/6/21 13:18
 * @Author:qs@1.com
 */
public interface UserService {

    List<User> list();

    User getById(Integer id);

}
