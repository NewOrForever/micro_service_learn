package com.example.authserver;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.util.Assert;

import java.io.IOException;
import java.lang.reflect.Proxy;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * ClassName:Myserial
 * Package:com.example.authserver
 * Description:
 *
 * @Date:2024/1/23 17:18
 * @Author:qs@1.com
 */
public class MysOAuth2AccessTokenJacksonSerializer extends StdSerializer<OAuth2AccessToken> {

    public MysOAuth2AccessTokenJacksonSerializer() {
        super(OAuth2AccessToken.class);
    }

    @Override
    public void serialize(OAuth2AccessToken token, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeStartObject();
        doSerialize(token, jgen);
        jgen.writeEndObject();
    }

    private void doSerialize(OAuth2AccessToken token, JsonGenerator jgen) throws IOException {
        jgen.writeStringField(OAuth2AccessToken.ACCESS_TOKEN, token.getValue());
        jgen.writeStringField(OAuth2AccessToken.TOKEN_TYPE, token.getTokenType());
        OAuth2RefreshToken refreshToken = token.getRefreshToken();
        if (refreshToken != null) {
            jgen.writeStringField(OAuth2AccessToken.REFRESH_TOKEN, refreshToken.getValue());
        }
        Date expiration = token.getExpiration();
        if (expiration != null) {
            long now = System.currentTimeMillis();
            jgen.writeNumberField(OAuth2AccessToken.EXPIRES_IN, (expiration.getTime() - now) / 1000);
        }
        Set<String> scope = token.getScope();
        if (scope != null && !scope.isEmpty()) {
            StringBuffer scopes = new StringBuffer();
            for (String s : scope) {
                Assert.hasLength(s, "Scopes cannot be null or empty. Got " + scope + "");
                scopes.append(s);
                scopes.append(" ");
            }
            jgen.writeStringField(OAuth2AccessToken.SCOPE, scopes.substring(0, scopes.length() - 1));
        }
        Map<String, Object> additionalInformation = token.getAdditionalInformation();
        if (additionalInformation != null) {
            for (String key : additionalInformation.keySet()) {
                jgen.writeObjectField(key, additionalInformation.get(key));
            }
        }
    }

    @Override
    public void serializeWithType(OAuth2AccessToken value, JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer) throws IOException {
        gen.writeStartObject();
        if (typeSer != null) {
            String className = OAuth2AccessToken.class.getName();
            // value 是否是代理类，如果是代理类，获取代理类的真实类型
            if (Proxy.isProxyClass(value.getClass())) {
                // 取 OAuth2AccessToken 接口的实现类
                Class<?>[] interfaces = value.getClass().getInterfaces();
                for (Class<?> clazz :interfaces){
                    if (OAuth2AccessToken.class.isAssignableFrom(clazz)){
                        className = clazz.getName();
                        break;
                    }
                }
            }
            gen.writeStringField(typeSer.getPropertyName(), className);
        }
        doSerialize(value, gen);
        gen.writeEndObject();
    }
}
