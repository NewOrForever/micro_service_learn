package com.example.sentinel.config;


import com.alibaba.cloud.sentinel.annotation.SentinelRestTemplate;
import com.alibaba.csp.sentinel.annotation.aspectj.SentinelResourceAspect;
import com.example.sentinel.sentinel.RestSentinelExceptionUtil;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * ClassName:RestConfiguration
 * Package:com.example.sentinel.config
 * Description: RestTemplate整合sentinel
 *
 * @Date:2022/7/20 9:24
 * @Author:qs@1.com
 */
@Configuration
public class RestConfiguration {

    @Bean
    @LoadBalanced
    @SentinelRestTemplate(blockHandlerClass = RestSentinelExceptionUtil.class, blockHandler = "handlerBlockException",
            fallbackClass = RestSentinelExceptionUtil.class, fallback = "fallback")
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
