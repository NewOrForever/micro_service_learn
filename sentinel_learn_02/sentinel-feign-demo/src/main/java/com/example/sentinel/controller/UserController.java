package com.example.sentinel.controller;

import com.example.common.utils.R;
import com.example.sentinel.feign.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/sentinel/user")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    /**
     * 这里报错没问题的啊，编译器问题而已
     * spring会先根据类型找到多个，最后再会根据属性名作为beanname去匹配
     * 解决编译器报错的话：
     *  1. 可以使用@Resource
     *  2. 可以使用加上@Qulifier注解
     */
    @Autowired
    private OrderService orderService;

    @RequestMapping("/findUserInfoById/{id}")
    public R findOrderByUserId(@PathVariable Integer id) {
        String res = orderService.findOrderByUserId(id);
        return R.ok().put("result", res);
    }
}
