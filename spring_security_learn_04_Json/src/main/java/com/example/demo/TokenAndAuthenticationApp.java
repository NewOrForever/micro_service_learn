package com.example.demo;

import com.example.demo.json.deserializer.OAuth2AuthenticationDeserializer;
import com.example.demo.json.mixin.MyOAuth2AccessTokenJacksonMixin;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.security.jackson2.CoreJackson2Module;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessTokenJackson2Deserializer;
import org.springframework.security.oauth2.common.OAuth2AccessTokenJackson2Serializer;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

/**
 * ClassName:App
 * Package:com.example.authserver
 * Description:
 *
 * @Date:2024/1/24 15:03
 * @Author:qs@1.com
 */
public class TokenAndAuthenticationApp {
    public static void main(String[] args) throws JsonProcessingException {
//        executeWithoutCoreJacksonModule();
        executeWithCoreJacksonModule();
    }

    private static void executeWithCoreJacksonModule() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addDeserializer(OAuth2Authentication.class, new OAuth2AuthenticationDeserializer());
        objectMapper.registerModule(simpleModule);
        /**
         * @see CoreJackson2Module#setupModule(Module.SetupContext)
         * @see SecurityJackson2Modules#enableDefaultTyping(ObjectMapper)
         * @see SecurityJackson2Modules#createWhitelistedDefaultTyping() 相当于将所有非final的 序列化反序列化类 都添加一个 @JsonTypeInfo 注解
         * 所以 {@link OAuth2AccessToken} 的序列化反序列化类都需要自己去重写 -> 需要添加对 serializeWithType 和 deserializeWithType 的实现
         * 感觉这个 {@link SecurityJackson2Modules} 添加了之后，会导致所有的序列化反序列化类都要实现 serializeWithType 和 deserializeWithType，如果
         * 已经存在的序列化反序列化类没有实现这两个方法，那么就会报错，所以还得自己去重写并 mixin 进去
         * 比如 {@link com.fasterxml.jackson.databind.deser.std.MapDeserializer}，objectMapper.readValue() json 反序列化为 Map 的时候，就会报错
         *
         * 我直接拿 {@link CoreJackson2Module} 中的 mixin 自己添加到 {@link ObjectMapper} 也不行啊，里面的 mixin 不是 public 的，只能自己去重写
         *
         * {@link SecurityJackson2Modules} 添加了之后，序列化成 json 的时候，如果属性不是基础类型，那么这个对象属性就会添加一个 @class 属性
         * -> 反序列化的时候就要考虑下这个 @class 属性
         */
        objectMapper.registerModule(new CoreJackson2Module());
        objectMapper.addMixIn(OAuth2AccessToken.class, MyOAuth2AccessTokenJacksonMixin.class);

        doSerAndDeser(objectMapper);
    }

    private static void executeWithoutCoreJacksonModule() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        SimpleModule simpleModule = new SimpleModule();
        /**
         * 也可以添加一个自定义的 mixin 类的方式
         */
        simpleModule.addDeserializer(OAuth2Authentication.class, new OAuth2AuthenticationDeserializer());
        objectMapper.registerModule(simpleModule);

        /**
         * 序列化反序列化 OAuth2AccessToken
         */
        doSerAndDeser(objectMapper);
    }

    private static void doSerAndDeser(ObjectMapper objectMapper) throws JsonProcessingException {
        /**
         * 序列化反序列化 OAuth2AccessToken
         */
        String token = "{\"access_token\":\"464838a8-4b23-4087-aa08-e7f2485e90eb\",\"token_type\":\"bearer\",\"refresh_token\":\"c5963f15-224c-4b37-9937-5208021be131\",\"expires_in\":43199,\"scope\":\"all\"}";
        byte[] bytes = token.getBytes(StandardCharsets.UTF_8);
        OAuth2AccessToken oAuth2AccessToken = objectMapper.readValue(token, OAuth2AccessToken.class);
        String s = objectMapper.writeValueAsString(oAuth2AccessToken);
        System.out.println(s);

        OAuth2AccessToken accesstoken = null;
        try {
            accesstoken = objectMapper.readValue(bytes, OAuth2AccessToken.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        /**
         * 序列化反序列化 OAuth2Authentication
         */
        String authentation = "{\"authorities\":[],\"details\":null,\"authenticated\":true,\"userAuthentication\":null,\"clientOnly\":true,\"principal\":\"world\",\"oauth2Request\":{\"clientId\":\"world\",\"scope\":[\"all\"],\"requestParameters\":{\"grant_type\":\"client_credentials\",\"client_id\":\"world\",\"scope\":\"all\"},\"resourceIds\":[],\"authorities\":[],\"approved\":true,\"refresh\":false,\"redirectUri\":null,\"responseTypes\":[],\"extensions\":{},\"grantType\":\"client_credentials\",\"refreshTokenRequest\":null},\"credentials\":\"\",\"name\":\"world\"}";
        OAuth2Authentication oAuth2Authentication = objectMapper.readValue(authentation, OAuth2Authentication.class);
        String at = objectMapper.writeValueAsString(oAuth2Authentication);
        System.out.println(at);
    }

}
