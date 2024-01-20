package com.example.feign.demo;

import com.example.common.utils.R;
import com.example.feign.entity.OrderEntity;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * ClassName:RemoteService
 * Package:com.example.feign.demo
 * Description:
 *
 * @Date:2022/6/20 12:57
 * @Author:qs@1.com
 */
public interface RemoteService {

    @Headers({"Content-Type:application/json", "Accept:application/json"})
    @RequestLine("GET /order/findOrderByUserId/{user_id}")
    R findOrderByUserId(@Param("user_id") Integer userId);

    @Headers({"Content-Type:application/json", "Accept:application/json"})
    @RequestLine("POST /order/save")
    R save(@RequestBody OrderEntity order);
}
