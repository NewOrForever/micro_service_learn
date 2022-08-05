package com.example.sentinel.config;


import com.alibaba.csp.sentinel.adapter.servlet.CommonFilter;
import com.alibaba.csp.sentinel.adapter.servlet.callback.WebCallbackManager;
import com.alibaba.csp.sentinel.annotation.aspectj.SentinelResourceAspect;
import com.example.sentinel.sentinel.MyUrlBlockHandler;
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
     * https://github.com/alibaba/Sentinel/issues/1213
     * 通过引入CommonFilter并配置WEB_CONTEXT_UNIFY属性为false来解决这个issue
     * 但会引入更多的问题：
     * 1. 比如 BlockException 异常处理的问题
     *  - 自定义UrlBlockHandler 设置进CommonFilter没法解决，拦截不到BlockException
     *  - 对应@SentinelResource指定的资源必须在@SentinelResource注解中指定blockHandler处理BlockException
     */
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
//        WebCallbackManager.setUrlBlockHandler(new MyUrlBlockHandler());
//
//        //解决授权规则不生效的问题
//        //com.alibaba.csp.sentinel.adapter.servlet.callback.RequestOriginParser
//        //WebCallbackManager.setRequestOriginParser(new MyRequestOriginParser());
//
//        return registration;
//    }
}
