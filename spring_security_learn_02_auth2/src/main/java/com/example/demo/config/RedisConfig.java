package com.example.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.StandardStringSerializationStrategy;

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

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    @Bean
    public TokenStore tokenStore() {
        RedisTokenStore redisTokenStore = new RedisTokenStore(redisConnectionFactory);
        // 设置序列化方式 - 自定义序列化反序列化方式
        // redisTokenStore.setSerializationStrategy(new MySerializationStrategy());
        return redisTokenStore;
    }

//    public static class MySerializationStrategy extends StandardStringSerializationStrategy {
//        private static final StringRedisSerializer STRING_SERIALIZER = new StringRedisSerializer();
//        private static final Jackson2JsonRedisSerializer<Object> JSON_SERIALIZER = new Jackson2JsonRedisSerializer<>(Object.class);
//
//        @Override
//        @SuppressWarnings("unchecked")
//        protected <T> T deserializeInternal(byte[] bytes, Class<T> clazz) {
//            return (T) JSON_SERIALIZER.deserialize(bytes);
//        }
//
//        @Override
//        protected byte[] serializeInternal(Object object) {
//            return STRING_SERIALIZER.serialize((String) object);
//        }
//    }
}
