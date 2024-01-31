package com.example.authserver;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.deser.std.UntypedObjectDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.jsontype.impl.AsPropertyTypeDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.util.TokenBuffer;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.DefaultOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * ClassName:MyOAuth2AccessTokenJacksonDeserializer
 * Package:com.example.authserver
 * Description:
 *
 * @Date:2024/1/24 14:02
 * @Author:qs@1.com
 */
public class MyOAuth2AccessTokenJacksonDeserializer extends StdDeserializer<OAuth2AccessToken> {

    /**
     * 参考 {@link org.springframework.security.oauth2.common.OAuth2AccessTokenJackson2Deserializer}
     */
    public MyOAuth2AccessTokenJacksonDeserializer() {
        super(OAuth2AccessToken.class);
    }

    @Override
    public OAuth2AccessToken deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {

//        DefaultOAuth2AccessToken accessToken = getAccessTokenByNotRecommend(jp);

        String tokenValue = null;
        String tokenType = null;
        Set<String> scope = null;
        String refreshToken = null;
        Long expiresIn = null;
        Map<String, Object> additionalInformation = new HashMap<>();

        /**
         * 参考 {@link com.alibaba.cloud.sentinel.gateway.SentinelGatewayAutoConfiguration.SentinelConverterConfiguration.ApiPredicateItemDeserializer#deserialize(JsonParser, DeserializationContext)}
         */
        ObjectMapper objectMapper = (ObjectMapper) jp.getCodec();
        ObjectNode root = objectMapper.readTree(jp);
        Iterator<Map.Entry<String, JsonNode>> elementsIterator = root.fields();
        while (elementsIterator.hasNext()) {
            Map.Entry<String, JsonNode> element = elementsIterator.next();
            String name = element.getKey();
            JsonNode jsonNode = element.getValue();
            if (OAuth2AccessToken.ACCESS_TOKEN.equals(name)) {
                tokenValue = jsonNode.textValue();
            } else if (OAuth2AccessToken.TOKEN_TYPE.equals(name)) {
                tokenType = jsonNode.textValue();
            } else if (OAuth2AccessToken.REFRESH_TOKEN.equals(name)) {
                refreshToken = jsonNode.textValue();
            } else if (OAuth2AccessToken.EXPIRES_IN.equals(name)) {
                expiresIn = jsonNode.longValue();
            } else if (OAuth2AccessToken.SCOPE.equals(name)) {
                scope = new HashSet<>(jsonNode.findValuesAsText(name));
            } else {
                if ("@class".equals(name)) {
                    continue;
                }
                Object obj = objectMapper.readValue(jsonNode.toString(), Object.class);
                additionalInformation.put(name, obj);
            }
        }

        DefaultOAuth2AccessToken accessToken = new DefaultOAuth2AccessToken(tokenValue);
        accessToken.setTokenType(tokenType);
        if (StringUtils.hasText(refreshToken)) {
            accessToken.setRefreshToken(new DefaultOAuth2RefreshToken(refreshToken));
        }
        if (expiresIn != null) {
            accessToken.setExpiration(new Date(System.currentTimeMillis() + (expiresIn * 1000)));
        }
        accessToken.setScope(scope);
        accessToken.setAdditionalInformation(additionalInformation);

        return accessToken;
    }

    /**
     * 这个方法不推荐
     * 虽然是从下面的 spring-security 的源码中拷贝过来稍微调整了下，但是一旦我直接
     * objectMapper.readValue(bytes, OAuth2AccessToken.class) 的话数据就又对不上了
     * {@link org.springframework.security.oauth2.common.OAuth2AccessTokenJackson2Deserializer#deserialize(JsonParser, DeserializationContext)}
     *
     * @param jp
     * @return
     * @throws IOException
     */
    @Deprecated
    private DefaultOAuth2AccessToken getAccessTokenByNotRecommend(JsonParser jp) throws IOException {
        JsonToken fieldJsonToken = jp.getCurrentToken();
        DefaultOAuth2AccessToken accessToken = new DefaultOAuth2AccessToken((String) null);
        /**
         * 这里为什么要单独处理一份呢？
         * @see AsPropertyTypeDeserializer#_deserializeTypedForId(JsonParser, DeserializationContext, TokenBuffer)
         * 这里会执行一次 jp.nextToken() -> 如果不单独处理一次的话，后面的数据都会对不上
         *
         * 导致这个问题的原因是：
         * 我的 {@link MyJacksonSerializationStrategy} 包装了
         * {@link org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer}，而它内部使用了 (T)objectMapper.readValue(bytes, Object.class)
         * -> 进入 {@link UntypedObjectDeserializer.Vanilla#deserializeWithType(JsonParser, DeserializationContext, TypeDeserializer)}
         * -> {@link AsPropertyTypeDeserializer#deserializeTypedFromAny(JsonParser, DeserializationContext)}
         * -> {@link AsPropertyTypeDeserializer#deserializeTypedFromObject(JsonParser, DeserializationContext)}
         * 因为 json 串中有 @class 字段，所以会进入：
         * -> {@link AsPropertyTypeDeserializer#_deserializeTypedForId(JsonParser, DeserializationContext, TokenBuffer)} 找到真正的反序列化类
         * 这个方法中在进入真正的反序列化类之前，会执行一次 jp.nextToken()
         *
         * 将 {@link org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer} 替换成
         * {@link org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer} 就不会有这个问题了
         * -> 就不需要单独处理一份了
         */
        loadJsonParser(jp, accessToken);

        // TODO What should occur if a parameter exists twice
        // while (jp.nextToken() != JsonToken.END_OBJECT) {
        while (jp.nextToken() != JsonToken.END_OBJECT) {
            loadJsonParser(jp, accessToken);
        }

        // TODO What should occur if a required parameter (tokenValue or tokenType) is missing?
        return accessToken;
    }

    private void loadJsonParser(JsonParser jp, DefaultOAuth2AccessToken accessToken) throws IOException {
        String name = jp.getCurrentName();
        jp.nextToken();
        if (OAuth2AccessToken.ACCESS_TOKEN.equals(name)) {
            String tokenValue = jp.getText();
            accessToken.setValue(tokenValue);
        } else if (OAuth2AccessToken.TOKEN_TYPE.equals(name)) {
            String tokenType = jp.getText();
            accessToken.setTokenType(tokenType);
        } else if (OAuth2AccessToken.REFRESH_TOKEN.equals(name)) {
            String refreshToken = jp.getText();
            if (StringUtils.hasText(refreshToken)) {
                accessToken.setRefreshToken(new DefaultOAuth2RefreshToken(refreshToken));
            }
        } else if (OAuth2AccessToken.EXPIRES_IN.equals(name)) {
            Long expiresIn = jp.getLongValue();
            if (expiresIn != null) {
                accessToken.setExpiration(new Date(System.currentTimeMillis() + (expiresIn * 1000)));
            }
        } else if (OAuth2AccessToken.SCOPE.equals(name)) {
            Set<String> scope = parseScope(jp);
            accessToken.setScope(scope);
        } else {
            Map<String, Object> additionalInformation = new HashMap<>();
            additionalInformation.put(name, jp.readValueAs(Object.class));
            accessToken.setAdditionalInformation(additionalInformation);
        }
    }

    @Override
    public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
        return deserialize(p, ctxt);
    }

    private Set<String> parseScope(JsonParser jp) throws JsonParseException, IOException {
        Set<String> scope;
        if (jp.getCurrentToken() == JsonToken.START_ARRAY) {
            scope = new TreeSet<String>();
            while (jp.nextToken() != JsonToken.END_ARRAY) {
                scope.add(jp.getValueAsString());
            }
        } else {
            String text = jp.getText();
            scope = OAuth2Utils.parseParameterList(text);
        }
        return scope;
    }
}
