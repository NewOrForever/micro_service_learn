package com.example.feign.service;

import com.example.common.utils.R;
import com.example.feign.config.FeignConfig;
import feign.Param;
import feign.RequestLine;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * ClassName:OrderService
 * Package:com.example.feign.service
 * Description: spring cloud 整合feign
 *
 * @Date:2022/6/20 10:53
 * @Author:qs@1.com
 */
// 局部配置，推荐使用yml方式，下面这种方式，FeignConfig需要吧@Configuration注释掉否则就是全局配置了
// @FeignClient(value = "mall-order", path = "/order", configuration = {FeignConfig.class})
@FeignClient(value = "mall-order", path = "/order")
public interface OrderService {
    // 如果配置使用了feign原生注解的话，那么这个使用springmvc注解的地方就得注释掉了哦，不然启动就报错了
    // 反之，使用springmvc的话原生的那个注解的地方也得注释掉不然会报错
    // http://mall-order/order/findOrderByUserId/1
    @RequestMapping("/findOrderByUserId/{user_id}")
    R findOrderByUserId(@PathVariable Integer user_id);

//    @RequestLine("GET /findOrderByUserId/{userId}")
//    R findOrderByUserIdWithFeignDefault(@Param Integer userId);

}
