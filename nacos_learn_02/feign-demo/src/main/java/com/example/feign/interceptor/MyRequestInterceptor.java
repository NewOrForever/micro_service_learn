package com.example.feign.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.cloud.client.loadbalancer.Request;

import java.util.UUID;

/**
 * ClassName:MyRequestInterceptor
 * Package:com.example.feign.interceptor
 * Description: 自定义一个拦截器，在每次http请求之前给header中添加Authorization的值
 *
 * @Date:2022/6/20 15:33
 * @Author:qs@1.com
 */
public class MyRequestInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        String token = UUID.randomUUID().toString();
        template.header("Authorization", token);
    }
}
