package com.example.demo;

import com.example.demo.json.deserializer.OAuth2AuthenticationDeserializer;
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
public class Auth2AuthenticationApp {
    public static void main(String[] args) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();

        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addDeserializer(OAuth2Authentication.class, new OAuth2AuthenticationDeserializer());
        objectMapper.registerModule(simpleModule);

        String authentation = "{\"authorities\":[],\"details\":null,\"authenticated\":true,\"userAuthentication\":null,\"clientOnly\":true,\"principal\":\"world\",\"oauth2Request\":{\"clientId\":\"world\",\"scope\":[\"all\"],\"requestParameters\":{\"grant_type\":\"client_credentials\",\"client_id\":\"world\",\"scope\":\"all\"},\"resourceIds\":[],\"authorities\":[],\"approved\":true,\"refresh\":false,\"redirectUri\":null,\"responseTypes\":[],\"extensions\":{},\"grantType\":\"client_credentials\",\"refreshTokenRequest\":null},\"credentials\":\"\",\"name\":\"world\"}";
        OAuth2Authentication oAuth2Authentication = objectMapper.readValue(authentation, OAuth2Authentication.class);
        String s = objectMapper.writeValueAsString(oAuth2Authentication);
        System.out.println(s);
    }
}
