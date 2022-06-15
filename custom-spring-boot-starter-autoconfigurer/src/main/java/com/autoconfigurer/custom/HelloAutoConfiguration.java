package com.autoconfigurer.custom;

import cn.hutool.core.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ClassName:HelloAutoConfiguration
 * Package:com.autoconfigurer.custom
 * Description:
 *
 * @Date:2022/6/9 16:16
 * @Author:qs@1.com
 */
@Configuration(proxyBeanMethods = false)
// HelloProperties会注册为bean
// 这是和配置文件绑定的javabean
@EnableConfigurationProperties(HelloProperties.class)
// 配置文件要有改配置否则改AutoConfiguration排除
@ConditionalOnProperty(value = "custom.hello.name")
// 要有StrUtil类
@ConditionalOnClass(StrUtil.class)
public class HelloAutoConfiguration {
    @Autowired
    private HelloProperties helloProperties;

    @Bean
    public IndexController indexController() {
        return new IndexController(helloProperties);
    }
}
