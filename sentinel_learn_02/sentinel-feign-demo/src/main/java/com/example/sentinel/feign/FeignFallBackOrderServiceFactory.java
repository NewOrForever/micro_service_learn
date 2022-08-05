package com.example.sentinel.feign;

import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * ClassName:FeignFallBackOrderServiceFactory
 * Package:com.example.sentinel.feign
 * Description:
 *
 * @Date:2022/7/27 15:05
 * @Author:qs@1.com
 */
@Component
public class FeignFallBackOrderServiceFactory implements FallbackFactory<OrderService> {

    @Override
    public OrderService create(Throwable cause) {
        return new OrderService() {
            @Override
            public String findOrderByUserId(Integer id) {
                return "=========> BlockException -------fallbackFactory----> 服务限流->降级";
            }
        };
    }
}
