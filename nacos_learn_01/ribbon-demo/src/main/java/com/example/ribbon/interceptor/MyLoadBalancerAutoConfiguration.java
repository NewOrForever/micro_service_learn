package com.example.ribbon.interceptor;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerInterceptor;
import org.springframework.cloud.client.loadbalancer.LoadBalancerRequestFactory;
import org.springframework.cloud.client.loadbalancer.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ClassName:MyLoadBalancerAutoConfiguration
 * Package:com.example.ribbon.interceptor
 * Description:
 *
 * @Date:2022/6/17 13:50
 * @Author:qs@1.com
 */
@Configuration(proxyBeanMethods = false)
public class MyLoadBalancerAutoConfiguration {

    @MyLoadBalanced
    @Autowired(required = false)
    private List<RestTemplate> restTemplates = Collections.emptyList();

    @Bean
    public MyLoadBalancerInterceptor myLoadBalancerInterceptor(LoadBalancerClient loadBalancerClient,
                                                               LoadBalancerRequestFactory loadBalancerRequestFactory) {
        return new MyLoadBalancerInterceptor(loadBalancerClient, loadBalancerRequestFactory);
    }

    /**
     * spring 扩展点 - 在所有的非懒加载单例bean都加载完成之后会去执行
     * @see  SmartInitializingSingleton
     * @param myLoadBalancerInterceptor
     * @return
     */
    @Bean
    public SmartInitializingSingleton myLoadBalancerAfterSingletonInitial(final MyLoadBalancerInterceptor myLoadBalancerInterceptor) {
        return new SmartInitializingSingleton() {
            @Override
            public void afterSingletonsInstantiated() {
                for (RestTemplate restTemplate : MyLoadBalancerAutoConfiguration.this.restTemplates) {
                    List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>(restTemplate.getInterceptors());
                    interceptors.add(myLoadBalancerInterceptor);
                    restTemplate.setInterceptors(interceptors);
                }
            }
        };
    }


//    @Bean
//    public RestTemplateCustomizer myRestTemplateCustomizer(
//            final MyLoadBalancerInterceptor myLoadBalancerInterceptor) {
//        return restTemplate -> {
//            List<ClientHttpRequestInterceptor> list = new ArrayList<>(
//                    restTemplate.getInterceptors());
//            list.add(myLoadBalancerInterceptor);
//            restTemplate.setInterceptors(list);
//        };
//    }

}
