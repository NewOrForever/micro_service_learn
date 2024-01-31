package com.example.authserver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
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
//        SimpleModule simpleModule = new SimpleModule();
//        simpleModule.addDeserializer(OAuth2Authentication.class, new OAuth2AuthenticationDeserializer());
//        objectMapper.registerModule(simpleModule);
        /**
         * 序列化类没写，最终的json 串中每个对象都会有一个 @class 属性，反序列化处理的时候需要注意
         *
         * @see CoreJackson2Module 先不加看看效果，加了之后会有好多 typeId 的问题
         */
        // objectMapper.registerModule(new CoreJackson2Module());

        objectMapper.addMixIn(OAuth2AccessToken.class, MyOAuth2AccessTokenJacksonMixin.class);
        objectMapper.addMixIn(OAuth2Authentication.class, MyOAuth2AuthenticationJacksonMixin.class);
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
