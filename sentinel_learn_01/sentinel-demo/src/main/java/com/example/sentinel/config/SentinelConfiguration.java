package com.example.sentinel.config;


import com.alibaba.csp.sentinel.annotation.aspectj.SentinelResourceAspect;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
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
public class SentinelConfiguration {
    /**
     * 这个切面要配置，不然控制台会显示客户端失联的
     * 使用spring cloud 的那个包后这个就不需要了
     * @return
     */
    @Bean
    public SentinelResourceAspect sentinelResourceAspect() {
        return new SentinelResourceAspect();
    }

//    @Bean
//    public FilterRegistrationBean sentinelFilterRegistration() {
//        FilterRegistrationBean registration = new FilterRegistrationBean();
//        registration.setFilter(new CommonFilter());
//        registration.addUrlPatterns("/*");
//        // 入口资源关闭聚合  解决流控链路不生效的问题
//        registration.addInitParameter(CommonFilter.WEB_CONTEXT_UNIFY, "false");
//        registration.setName("sentinelFilter");
//        registration.setOrder(1);
//
//        //CommonFilter的BlockException自定义处理逻辑
////        WebCallbackManager.setUrlBlockHandler(new MyUrlBlockHandler());
//
//        //解决授权规则不生效的问题
//        //com.alibaba.csp.sentinel.adapter.servlet.callback.RequestOriginParser
//        //WebCallbackManager.setRequestOriginParser(new MyRequestOriginParser());
//
//        return registration;
//    }
}
