package com.example.sentinel.feign;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * ClassName:OrderService
 * Package:com.example.sentinel.feign
 * Description:
 * fallback：指定的类型需要实现当前接口且需要是个bean
 * fallbackFactory：指定的类型需要实现FallbackFactory且需要是个bean，泛型要传入当前接口类型
 * fallback和fallbackFactory同时指定的话，优先使用fallback（看下SentinelInvocationHandler的源码就知道了）
 * feign整合sentinel核心就是 SentinelFeign.Builder 和 SentinelInvocationHandler
 *
 * @Date:2022/7/27 12:57
 * @Author:qs@1.com
 */
@FeignClient(value = "order-service", path = "/order",
         fallback = FeignFallBackOrderService.class
        // fallbackFactory = FeignFallBackOrderServiceFactory.class
        )
public interface OrderService {
    @RequestMapping("/findOrderByUserId/{id}")
    String findOrderByUserId(@PathVariable Integer id);
}
