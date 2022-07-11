package com.example.feign.config;

import com.example.feign.interceptor.MyRequestInterceptor;
import feign.Contract;
import feign.Logger;
import feign.Request;
import feign.auth.BasicAuthRequestInterceptor;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ClassName:FeignConfig
 * Package:com.example.feign.config
 * Description:
 *
 * @Date:2022/6/20 14:08
 * @Author:qs@1.com
 */
// 这个注解用的话就是全局配置了，注释掉去yml配置文件中对响应微服务进行局部配置
//@Configuration
public class FeignConfig {

    /**
     * 配置全局的日志
     * yml中需要配置
     * logging:
     *   level:
     *     com.example.feign.service(这是接口包路径): debug
     *
     * 日志级别
     * 通过源码可以看到日志等级有 4 种，分别是：
     * NONE：不输出日志。
     * BASIC：只输出请求方法的 URL 和响应的状态码以及接口执行的时间。
     * HEADERS：将 BASIC 信息和请求头信息输出。
     * FULL：输出完整的请求信息。
     *
     * @return
     */
    @Bean
    public Logger.Level feignLoggLevel() {
        return Logger.Level.FULL;
    }

    /**
     * 使用feign原生的注解配置 -> OrderService中的springmvc注解就无效了
     * 和spring cloud整合后默认使用的是SpringMvcContract -> 可以使用SpringMvc的注解
     * @return
     */
//    @Bean
//    public Contract feignContract() {
//        return new Contract.Default();
//    }

    /**
     * Basic认证
     * 就是apply方法中统一添加一个header，账户和密码会进行base64加密
     * 进源码看就知道了
     * 注意：RequestInterceptor这个接口是用于发起http请求之前的一些操作啊
     * @return
     */
//    @Bean
//    public BasicAuthRequestInterceptor basicAuthRequestInterceptor() {
//        return new BasicAuthRequestInterceptor("sq", "123456");
//    }

    /**
     * 自定义拦截器
     * @return
     */
//    @Bean
//    public MyRequestInterceptor myRequestInterceptor() {
//        return new MyRequestInterceptor();
//    }

    /**
     * 超时时间的全局配置
     * mall-order的controller进行sleep一下报错Read timed out
     * @return
     */
    @Bean
    public Request.Options options() {
        return new Request.Options(5000, 5000);
    }

    @Bean
    public Encoder encoder() {
        return new JacksonEncoder();
    }

    @Bean
    public Decoder decoder() {
        return new JacksonDecoder();
    }

}
