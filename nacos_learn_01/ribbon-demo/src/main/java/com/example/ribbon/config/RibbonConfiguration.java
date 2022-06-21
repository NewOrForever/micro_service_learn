package com.example.ribbon.config;

import com.alibaba.cloud.nacos.ribbon.NacosRule;
import com.netflix.loadbalancer.IRule;
import org.springframework.cloud.client.ConditionalOnBlockingDiscoveryEnabled;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 可以参考一下RibbonClientConfiguration这个配置类
 */
//@Configuration
public class RibbonConfiguration {
    @Bean
    public IRule ribbonRule() {
        // 指定使用Nacos提供的负载均衡策略（优先调用同一集群的实例，基于随机权重）
        // mall-user  v1--- mall-order v1
        //mall-user  v2--- mall-order v2
        return new NacosRule();
    }
}
