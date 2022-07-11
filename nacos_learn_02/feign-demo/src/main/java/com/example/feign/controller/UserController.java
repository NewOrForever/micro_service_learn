package com.example.feign.controller;

import com.example.common.utils.R;
import com.example.feign.service.OrderService;
import feign.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ClassName:UserController
 * Package:com.example.feign.controller
 * Description: spring cloud 整合feign
 *
 * @Date:2022/6/20 11:20
 * @Author:qs@1.com
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private OrderService orderService;

    @RequestMapping("/findOrderByUserId/{userId}")
    public R findOrderByUserId(@PathVariable Integer userId) {
        // System.out.println(Logger.Level.valueOf("FULL"));
        return orderService.findOrderByUserId(userId);
    }

//    @RequestMapping("/findOrderByUserIdWithFeign/{userId}")
//    public R findOrderByUserIdWithFeignDefault(@PathVariable Integer userId) {
//        return orderService.findOrderByUserIdWithFeignDefault(userId);
//    }
}
