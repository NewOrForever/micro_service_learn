package com.example.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

/**
 * ClassName:RedisConfig
 * Package:com.example.demo.config
 * Description:
 *
 * @Date:2023/10/23 13:26
 * @Author:qs@1.com
 */
@Configuration
public class RedisConfig {


//    @Autowired
//    private RedisConnectionFactory redisConnectionFactory;

    /**
     * 令牌存储策略
     * jwt 也引入后，这里的配置可以不需要了
     * 也可以使用 @Primary 注解在 JwtTokenStore，让 jwt 优先注入
     * 也可以使用 @Qualifier("jwtTokenStore") 注解在 AuthorizationServerConfig 的 tokenStore 属性上
     */

//    @Bean
//    public TokenStore tokenStore() {
//        return new RedisTokenStore(redisConnectionFactory);
//    }

}
