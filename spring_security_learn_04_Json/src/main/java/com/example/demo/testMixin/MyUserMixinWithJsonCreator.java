package com.example.demo.testMixin;

import com.fasterxml.jackson.annotation.*;

/**
 * ClassName:OAuth2AuthenticationSerializer
 * Package:com.example.authserver
 *
 * @Date:2024/1/23 17:13
 * @Author:qs@1.com
 */

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE, creatorVisibility = JsonAutoDetect.Visibility.ANY)
@JsonIgnoreProperties(ignoreUnknown = true)
abstract class MyUserMixinWithJsonCreator {

    /**
     * @JsonCreator 注解表示这个构造函数是用于从JSON创建对象。
     * @JsonProperty 注解用于指定JSON属性名和Java参数名之间的映射。例如，JSON中的"oauth2Request"属性将被映射到Java参数 storedRequest。
     */
    @JsonCreator
    public MyUserMixinWithJsonCreator(@JsonProperty("username") String username,
                                      @JsonProperty("realname") String realname) {
    }

}
