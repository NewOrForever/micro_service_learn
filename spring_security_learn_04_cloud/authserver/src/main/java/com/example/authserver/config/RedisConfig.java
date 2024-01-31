package com.example.authserver.config;

import com.alibaba.fastjson2.JSON;
import com.example.authserver.MyJacksonSerializationStrategy;
import com.example.authserver.MyOAuth2AccessTokenJacksonMixin;
import com.example.authserver.OAuth2AuthenticationDeserializer;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

/**
 * @author Fox
 */
@Configuration
public class RedisConfig {

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    @Bean
    public TokenStore tokenStore() {
        // access_token
        RedisTokenStore redisTokenStore = new RedisTokenStore(redisConnectionFactory);
        // 设置序列化方式 string
        redisTokenStore.setSerializationStrategy(new MyJacksonSerializationStrategy());
        return redisTokenStore;
    }
}
