package com.example.demo.testMixin;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * ClassName:OAuth2AuthenticationSerializer
 * Package:com.example.authserver
 *
 * @Date:2024/1/23 17:13
 * @Author:qs@1.com
 */

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(using = MyUserDeserializer.class)
@JsonSerialize(using = MyUserSerializer.class)
public abstract class MyUserMixin {

}
