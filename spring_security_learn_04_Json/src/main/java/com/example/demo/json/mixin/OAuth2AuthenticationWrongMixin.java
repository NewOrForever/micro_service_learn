package com.example.demo.json.mixin;

import com.fasterxml.jackson.annotation.*;
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

/**
 * creatorVisibility = JsonAutoDetect.Visibility.ANY表示检测所有构造函数
 * <b>这个Mixin 是个<span style="color:red">错误示范</span>，这里只是为了写法学习</b>
 * 像这种没有无参构造函数的类，如果要使用@JsonCreator注解，那么请注意：
 * 1. 目标类的有参构造参数如果是一个对象，且内嵌着对象，那么试一下不行的话就自己写序列化反序列化类吧
 */
@Deprecated
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE, creatorVisibility = JsonAutoDetect.Visibility.ANY)
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class OAuth2AuthenticationWrongMixin {

    /**
     * @JsonCreator 注解表示这个构造函数是用于从JSON创建对象。
     * @JsonProperty 注解用于指定JSON属性名和Java参数名之间的映射。例如，JSON中的"oauth2Request"属性将被映射到Java参数 storedRequest。
     */
    @JsonCreator
    public OAuth2AuthenticationWrongMixin(@JsonProperty("oauth2Request") OAuth2Request storedRequest,
                                          @JsonProperty("userAuthentication") Authentication userAuthentication) {
    }

}
