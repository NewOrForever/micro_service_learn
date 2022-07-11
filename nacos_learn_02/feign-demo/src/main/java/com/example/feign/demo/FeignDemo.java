package com.example.feign.demo;

import com.example.common.utils.R;
import com.example.feign.entity.OrderEntity;
import feign.Feign;
import feign.Request;
import feign.Retryer;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import org.springframework.core.Ordered;

/**
 * ClassName:FeignDemo
 * Package:com.example.feign.demo
 * Description: feign单独使用测试
 *
 * @Date:2022/6/20 12:57
 * @Author:qs@1.com
 */
public class FeignDemo {
    public static void main(String[] args) {
        RemoteService service = Feign.builder()
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .options(new Request.Options(5000, 10000))
                .retryer(new Retryer.Default())
                .target(RemoteService.class, "http://localhost:8100");

        R result = service.findOrderByUserId(1);
        System.out.println(result);

//        OrderEntity order = new OrderEntity();
//        order.setUserId("2");
//        order.setAmount(18000);
//        order.setCount(2);
//        order.setCommodityCode("bj");
//        System.out.println(service.save(order));
    }
}
