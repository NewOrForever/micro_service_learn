package com.example.demo;

import com.example.demo.json.deserializer.OAuth2AuthenticationDeserializer;
import com.example.demo.json.mixin.MyOAuth2AccessTokenJacksonMixin;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.security.jackson2.CoreJackson2Module;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.redis.StandardStringSerializationStrategy;

import java.io.IOException;

/**
 * ClassName:MyJacksonSerializer
 * Package:com.example.authserver
 * Description:
 *
 * @Date:2024/1/23 17:11
 * @Author:qs@1.com
 */
public class MyJacksonSerializationStrategy extends StandardStringSerializationStrategy {
    /**
     * @see Jackson2JsonRedisSerializer
     */
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final GenericJackson2JsonRedisSerializer jsonRedisSerializer = new GenericJackson2JsonRedisSerializer(objectMapper);

    static {
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addDeserializer(OAuth2Authentication.class, new OAuth2AuthenticationDeserializer());
        objectMapper.registerModule(simpleModule);

        objectMapper.registerModule(new CoreJackson2Module());
        objectMapper.addMixIn(OAuth2AccessToken.class, MyOAuth2AccessTokenJacksonMixin.class);
    }


    @Override
    protected <T> T deserializeInternal(byte[] bytes, Class<T> clazz) {
        return jsonRedisSerializer.deserialize(bytes, clazz);
    }

    @Override
    protected byte[] serializeInternal(Object object) {
        return jsonRedisSerializer.serialize(object);
    }

}
