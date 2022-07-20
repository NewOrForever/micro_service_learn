package com.example.sentinel.config;

import com.alibaba.csp.sentinel.annotation.aspectj.SentinelResourceAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ClassName:SentinelAspectConfiguration
 * Package:com.example.sentinel.config
 * Description: sentinel引入切面的一个配置类
 *
 * @Date:2022/7/20 9:24
 * @Author:qs@1.com
 */
@Configuration
public class SentinelAspectConfiguration {
    @Bean
    public SentinelResourceAspect sentinelResourceAspect() {
        return new SentinelResourceAspect();
    }
}
