package com.example.gateway.config;

import com.example.gateway.filter.CheckAuthFilter;
import com.example.gateway.filter.CheckAuthGatewayFilterFactory;
import com.example.gateway.filter.CheckIpFilter;
import com.example.gateway.predicate.CheckAuthRoutePredicateFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyGateWayConfigutation {
    /**
     * 自定义断言 - 后缀要以RoutePredicateFactory
     * @return
     */
    @Bean
    public CheckAuthRoutePredicateFactory checkAuthRoutePredicateFactory(){
        return new CheckAuthRoutePredicateFactory();
    }

    /**
     * 自定义过滤器 - 后缀要以GatewayFilterFactory
     * @return
     */
    @Bean
    public CheckAuthGatewayFilterFactory checkAuthGatewayFilterFactory(){
        return new CheckAuthGatewayFilterFactory();
    }

    /**
     * 自定义全局过滤器
     * @return
     */
    //@Bean
    //public CheckAuthFilter checkAuthFilter(){
//        return new CheckAuthFilter();
//    }

    @Bean
    public CheckIpFilter checkIpFilter(){
        return new CheckIpFilter();
    }
}
