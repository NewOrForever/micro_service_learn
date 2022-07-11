package com.example.dubbo.user.controller;

import com.example.entity.User;
import com.example.service.UserService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * ClassName:UserController
 * Package:com.example.dubbo.user.controller
 * Description:
 *
 * @Date:2022/6/21 15:34
 * @Author:qs@1.com
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @DubboReference
    private UserService userService;

    @RequestMapping("/list")
    public List<User> list() {
        return userService.list();
    }
}
