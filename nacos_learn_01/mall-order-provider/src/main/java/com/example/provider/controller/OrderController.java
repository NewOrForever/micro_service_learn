package com.example.provider.controller;

import com.example.provider.entity.OrderEntity;
import com.example.provider.service.OrderService;
import com.example.common.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * ClassName:OrderController
 * Package:com.example.common.controller
 * Description:
 *
 * @Date:2022/6/15 17:26
 * @Author:qs@1.com
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 使用@RequestHeader来接收feign-demo中RequestIntercetor在http请求之前设置进header的值
     * 这其实是权限控制的一种方式，这里可以建个拦截器来进行权限认证
     * @param userId
     * @param token
     * @return
     */
    @RequestMapping("/findOrderByUserId/{userId}")
    public R findOrderByUserId(@PathVariable Integer userId, @RequestHeader("Authorization") String token) {

        // 测试feign-demo的超时
//        try {
//            TimeUnit.SECONDS.sleep(6);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        System.out.println(token);
        List<OrderEntity> orderList = orderService.listByUserId(userId);
        return R.ok().put("orderList", orderList);
    }


    @PostMapping("/save")
    public R findOrderByUserId(@RequestBody OrderEntity order) {
        boolean res = orderService.save(order);
        return R.ok();
    }


}
