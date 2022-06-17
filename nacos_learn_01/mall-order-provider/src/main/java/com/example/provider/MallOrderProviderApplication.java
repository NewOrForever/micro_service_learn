package com.example.provider;

import com.example.common.xss.XssFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

import javax.servlet.Filter;

@SpringBootApplication
public class MallOrderProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallOrderProviderApplication.class, args);
    }

    /**
     * 配置过滤器
     */
    @Bean
    public FilterRegistrationBean xssFilter () {
        FilterRegistrationBean<Filter> filterFilterRegistrationBean = new FilterRegistrationBean<>();
        filterFilterRegistrationBean.setFilter(new XssFilter());
        filterFilterRegistrationBean.setName("xssFilter");
        filterFilterRegistrationBean.addUrlPatterns("/*");
        return filterFilterRegistrationBean;
    }
}
