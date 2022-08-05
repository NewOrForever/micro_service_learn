package com.example.sentinel.feign;

import org.springframework.stereotype.Component;

/**
 * ClassName:FeignFallBackOrderService
 * Package:com.example.sentinel.feign
 * Description:
 *
 * @Date:2022/7/27 14:39
 * @Author:qs@1.com
 */
@Component
public class FeignFallBackOrderService implements OrderService{
    @Override
    public String findOrderByUserId(Integer id) {
        return "===============> BlockException ----> 服务限流->降级";
    }
}
