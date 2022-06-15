package com.example.common.controller;

import com.example.common.entity.OrderEntity;
import com.example.common.service.OrderService;
import com.example.common.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    @RequestMapping("/findOrderByUserId/{userId}")
    public R findOrderByUserId(@PathVariable Integer userId) {
        List<OrderEntity> orderList = orderService.listByUserId(userId);
        return R.ok().put("orderList", orderList);
    }
}
