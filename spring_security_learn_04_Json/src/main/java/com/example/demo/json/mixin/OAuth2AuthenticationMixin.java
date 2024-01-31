package com.example.demo.json.mixin;

import com.example.demo.json.deserializer.OAuth2AuthenticationDeserializer;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;

/**
 * ClassName:OAuth2AuthenticationSerializer
 * Package:com.example.authserver
 * Description: <span style="color:red">这个Mixin使用还是会报错的哦</span>
 *
 * @Date:2024/1/23 17:13
 * @Author:qs@1.com
 */

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(using = OAuth2AuthenticationDeserializer.class)
public abstract class OAuth2AuthenticationMixin {
}
