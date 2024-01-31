package com.example.demo;

import com.example.demo.json.deserializer.OAuth2AuthenticationDeserializer;
import com.example.demo.json.mixin.MyOAuth2AccessTokenJacksonMixin;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessTokenJackson2Deserializer;
import org.springframework.security.oauth2.common.OAuth2AccessTokenJackson2Serializer;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

/**
 * ClassName:App
 * Package:com.example.authserver
 * Description:
 *
 * @Date:2024/1/24 15:03
 * @Author:qs@1.com
 */
public class AccessTokenApp {
    public static void main(String[] args) throws JsonProcessingException {
        String token = "{\"access_token\":\"464838a8-4b23-4087-aa08-e7f2485e90eb\",\"token_type\":\"bearer\",\"refresh_token\":\"c5963f15-224c-4b37-9937-5208021be131\",\"expires_in\":43199,\"scope\":\"all\"}";
        ObjectMapper objectMapper = new ObjectMapper();

        /**
         * @see OAuth2AccessToken 类上有注解指定了序列化和反序列化的方式
         * @see OAuth2AccessTokenJackson2Serializer 序列化
         * @see OAuth2AccessTokenJackson2Deserializer 反序列化
         */
        OAuth2AccessToken oAuth2AccessToken = objectMapper.readValue(token, OAuth2AccessToken.class);
        String s = objectMapper.writeValueAsString(oAuth2AccessToken);
        System.out.println(s);
    }
}
