package com.example.consumer.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * ClassName:MyConfiguration
 * Package:com.example.consumer.config
 * Description:
 *
 * @Date:2022/6/16 15:36
 * @Author:qs@1.com
 */
@Configuration
public class MyConfiguration {
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
