package com.example.consumer.controller;

import com.example.common.utils.R;
import com.example.consumer.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * ClassName:UserController
 * Package:com.example.consumer.controller
 * Description:
 *
 * @Date:2022/6/16 15:11
 * @Author:qs@1.com
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/findOrderByUserId/{user_id}")
    public R findOrderByUserId(@PathVariable("user_id") Integer userId) {
        return restTemplate.getForObject(String.format("http://mall-order/order/findOrderByUserId/%s", userId), R.class);
    }



}
