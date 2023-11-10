package com.tuling.tulingmallgateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Configuration
public class RibbonConfig {

    @Autowired
    private LoadBalancerClient loadBalancer;

    /**
     * 此处不能直接通过@LoadBalanced配置RestTemplate去获取公钥，思考为什么？
     * 1. @LoadBalanced 注解的 RestTemplate Bean 会在 LoadBalancerAutoConfiguration 中属性注入
     * 2. RestTemplate 加@LoadBalanced 是为了 添加 LoadBalancerInterceptor 通过服务名调用微服务，但是
     * 添加的生命周期是在所有的单例 bean 都创建完成后
     * 3. 但是我的 public key 是在 bean 初始化的时候就会去使用 RestTemplate 调用微服务获取，很明显
     * 此时拦截器还没有添加进 RestTemplate，无法调用微服务
     * @see org.springframework.cloud.client.loadbalancer.LoadBalancerAutoConfiguration
     * @see  org.springframework.cloud.client.loadbalancer.LoadBalancerAutoConfiguration#loadBalancedRestTemplateInitializerDeprecated
     * @see  org.springframework.cloud.client.loadbalancer.LoadBalancerAutoConfiguration.LoadBalancerInterceptorConfig#restTemplateCustomizer
     * @see org.springframework.beans.factory.support.DefaultListableBeanFactory#preInstantiateSingletons()
     */

    @Bean
    public RestTemplate restTemplate(){
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setInterceptors(
                Collections.singletonList(
                        new LoadBalancerInterceptor(loadBalancer)));

        return restTemplate;
    }

}
