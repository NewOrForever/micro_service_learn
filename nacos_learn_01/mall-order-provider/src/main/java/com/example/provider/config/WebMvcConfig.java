package com.example.provider.config;

import com.example.provider.interceptor.MyHandlerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * ClassName:WebMvcConfig
 * Package:com.example.provider
 * Description:
 *
 * @Date:2022/6/20 16:27
 * @Author:qs@1.com
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Bean
    public MyHandlerInterceptor myHandlerInterceptor() {
        return  new MyHandlerInterceptor();
    }

    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(myHandlerInterceptor());
    }
}
