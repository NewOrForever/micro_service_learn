package com.example.demo.json.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.Map;

/**
 * ClassName:OAuth2AuthenticationSerializer
 * Package:com.example.authserver
 * Description:
 *
 * @Date:2024/1/30 16:00
 * @Author:qs@1.com
 */
public class OAuth2AuthenticationSerializer extends StdSerializer<OAuth2Authentication> {
    public OAuth2AuthenticationSerializer() {
        super(OAuth2Authentication.class);
    }

    @Override
    public void serialize(OAuth2Authentication value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        doSerialize(value, gen, null);
        gen.writeEndObject();
    }

    private static void doSerialize(OAuth2Authentication value, JsonGenerator gen, String typeIdName) throws IOException {
        /**
         * {
         * 	"authorities": [],
         * 	"details": null,
         * 	"authenticated": true,
         * 	"userAuthentication": null,
         * 	"clientOnly": true,
         * 	"principal": "world",
         * 	"oauth2Request": {
         * 		"clientId": "world",
         * 		"scope": [
         * 			"all"
         * 		],
         * 		"requestParameters": {
         * 			"grant_type": "client_credentials",
         * 			"client_id": "world",
         * 			"scope": "all"
         *                },
         * 		"resourceIds": [],
         * 		"authorities": [],
         * 		"approved": true,
         * 		"refresh": false,
         * 		"redirectUri": null,
         * 		"responseTypes": [],
         * 		"extensions": {},
         * 		"grantType": "client_credentials",
         * 		"refreshTokenRequest": null* 	},
         * 	"credentials": "",
         * 	"name": "world"
         * }
         */
        gen.writeBooleanField("authenticated", value.isAuthenticated());
        gen.writeBooleanField("clientOnly", value.isClientOnly());
        gen.writeStringField("principal", value.getPrincipal() == null ? "" : value.getPrincipal().toString());
        gen.writeStringField("credentials", value.getCredentials() == null ? "" : value.getCredentials().toString());
        gen.writeStringField("name", value.getName());

        writeAuthorities(gen, value.getAuthorities());

        writeObject(gen, "details", value.getDetails());

        writeObject(gen, "userAuthentication", value.getUserAuthentication());

        gen.writeObjectFieldStart("oauth2Request");
        // OAuth2Request 对象 json 序列化
        OAuth2Request oAuth2Request = value.getOAuth2Request();
        if (oAuth2Request != null) {
            gen.writeStringField("clientId", oAuth2Request.getClientId());
            gen.writeStringField("grantType", oAuth2Request.getGrantType());
            gen.writeStringField("redirectUri", oAuth2Request.getRedirectUri());
            gen.writeBooleanField("approved", oAuth2Request.isApproved());
            gen.writeBooleanField("refresh", oAuth2Request.isRefresh());

            writeExtensions(gen, oAuth2Request);

            gen.writeObjectField("refreshTokenRequest", oAuth2Request.getRefreshTokenRequest());

            writeStringArray(gen, "resourceIds", oAuth2Request.getResourceIds());

            writeStringArray(gen, "responseTypes", oAuth2Request.getResponseTypes());

            writeAuthorities(gen, oAuth2Request.getAuthorities());

            writeStringArray(gen, "scope", oAuth2Request.getScope());

            writeRequestParameters(gen, oAuth2Request);
        }
        gen.writeEndObject();
    }

    private static void writeObject(JsonGenerator gen, String fieldName, Object value) throws IOException {
        gen.writeObjectField(fieldName, value);
    }

    /**
     * 序列化对象属性时，添加 @class 属性
     * @param fieldName
     * @param value
     * @param valueClass
     * @param gen
     * @param typeIdName
     * @throws IOException
     */
    private static void writeObjectWithTypeId(String fieldName, Object value, Class<?> valueClass, JsonGenerator gen, String typeIdName) throws IOException {
        gen.writeObjectFieldStart(fieldName);
        if (StringUtils.hasText(typeIdName)) {
            gen.writeStringField(typeIdName, valueClass.getName());
        }
        gen.writeObjectField(fieldName, value);
        gen.writeEndObject();
    }

    private static void writeExtensions(JsonGenerator gen, OAuth2Request oAuth2Request) throws IOException {
        gen.writeObjectFieldStart("extensions");
        Map<String, Serializable> extensions = oAuth2Request.getExtensions();
        if (!CollectionUtils.isEmpty(extensions)) {
            for (Map.Entry<String, Serializable> entry : extensions.entrySet()) {
                gen.writeObjectField(entry.getKey(), entry.getValue());
            }
        }
        gen.writeEndObject();
    }

    private static void writeAuthorities(JsonGenerator gen, Collection<? extends GrantedAuthority> authorities) throws IOException {
        gen.writeArrayFieldStart("authorities");
        if (!CollectionUtils.isEmpty(authorities)) {
            for (GrantedAuthority authority : authorities) {
                gen.writeString(authority.getAuthority());
            }
        }
        gen.writeEndArray();
    }

    private static void writeRequestParameters(JsonGenerator gen, OAuth2Request oAuth2Request) throws IOException {
        gen.writeObjectFieldStart("requestParameters");
        Map<String, String> requestParameters = oAuth2Request.getRequestParameters();
        if (!CollectionUtils.isEmpty(requestParameters)) {
            for (Map.Entry<String, String> entry : requestParameters.entrySet()) {
                gen.writeStringField(entry.getKey(), entry.getValue());
            }
        }
        gen.writeEndObject();
    }

    private static void writeStringArray(JsonGenerator gen, String fieldName, Collection<String> collection) throws IOException {
        gen.writeArrayFieldStart(fieldName);
        if (!CollectionUtils.isEmpty(collection)) {
            for (String value : collection) {
                gen.writeString(value);
            }
        }
        gen.writeEndArray();
    }

    @Override
    public void serializeWithType(OAuth2Authentication value, JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer) throws IOException {
        gen.writeStartObject();
        if (typeSer != null) {
            String className = OAuth2Authentication.class.getName();
            // value 是否是代理类，如果是代理类，获取代理类的真实类型
            if (Proxy.isProxyClass(value.getClass())) {
                // 取 OAuth2Authentication 接口的实现类
                Class<?>[] interfaces = value.getClass().getInterfaces();
                for (Class<?> clazz :interfaces){
                    if (OAuth2Authentication.class.isAssignableFrom(clazz)){
                        className = clazz.getName();
                        break;
                    }
                }
            }
            gen.writeStringField(typeSer.getPropertyName(), className);
        }
        doSerialize(value, gen, typeSer.getPropertyName());
        gen.writeEndObject();
    }
}
