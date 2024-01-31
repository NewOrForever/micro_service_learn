package com.example.demo;

import com.alibaba.fastjson2.JSONObject;
import com.example.demo.json.mixin.OAuth2AuthenticationWrongMixin;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.security.jackson2.CoreJackson2Module;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import java.util.Map;

/**
 * ClassName:App
 * Package:com.example.authserver
 * Description:
 *
 * @Date:2024/1/24 15:03
 * @Author:qs@1.com
 */
public class UseMixinJsonCreatorApp {
    public static void main(String[] args) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();


        SimpleModule simpleModule = new SimpleModule();
        simpleModule.setMixInAnnotation(OAuth2Authentication.class, OAuth2AuthenticationWrongMixin.class);
        objectMapper.registerModule(simpleModule);
//        objectMapper.registerModule(new CoreJackson2Module());

        String authentation = "{\"@class\":\"org.springframework.security.oauth2.provider.OAuth2Authentication\",\"authorities\":[],\"details\":null,\"authenticated\":true,\"userAuthentication\":null,\"clientOnly\":true,\"principal\":\"world\",\"oauth2Request\":{\"clientId\":\"world\",\"scope\":[\"all\"],\"requestParameters\":{\"grant_type\":\"client_credentials\",\"client_id\":\"world\",\"scope\":\"all\"},\"resourceIds\":[],\"authorities\":[],\"approved\":true,\"refresh\":false,\"redirectUri\":null,\"responseTypes\":[],\"extensions\":{},\"grantType\":\"client_credentials\",\"refreshTokenRequest\":null},\"credentials\":\"\",\"name\":\"world\"}";

        Map<String, Object> map = objectMapper.readValue(authentation, new TypeReference<Map<String, Object>>() {
        });



        OAuth2Authentication oAuth2Authentication = objectMapper.readValue(authentation, OAuth2Authentication.class);

        String s = objectMapper.writeValueAsString(oAuth2Authentication);
        System.out.println(s);

    }
}
