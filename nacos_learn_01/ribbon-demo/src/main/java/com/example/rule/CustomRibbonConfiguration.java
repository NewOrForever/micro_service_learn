package com.example.rule;

import com.alibaba.cloud.nacos.ribbon.NacosRule;
import com.netflix.loadbalancer.IRule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomRibbonConfiguration {
    @Bean
    public IRule ribbonRule() {
        return new NacosRule();
    }

}
