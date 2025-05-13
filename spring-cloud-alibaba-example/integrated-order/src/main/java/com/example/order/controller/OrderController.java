package com.example.order.controller;

import com.example.common.BusinessException;
import com.example.common.Result;
import com.example.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * ClassName:OrderController
 * Package:com.example.order.controller
 * Description:
 *
 * @Date:2025/3/27 17:29
 * @Author:qs@1.com
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/create")
    public Result<?> createOrder(@RequestParam("userId") String userId,
                                 @RequestParam("commodityCode") String commodityCode,
                                 @RequestParam("count") Integer count) {
        Result<?> res = null;
        try {
            res = orderService.createOrder(userId, commodityCode, count);
        }
        catch (BusinessException e) {
            return Result.failed(e.getMessage());
        }
        return res;
    }

}