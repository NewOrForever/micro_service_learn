package com.example.demo.testMixin;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.impl.TypeWrappedDeserializer;import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

import java.io.IOException;

/**
 * ClassName:MyUserDeserializer
 * Package:com.example.demo.testMixin
 * Description:
 *
 * @Date:2024/1/26 9:28
 * @Author:qs@1.com
 */
public class MyUserDeserializer extends StdDeserializer<MyUser> {

    protected MyUserDeserializer() {
        super(MyUser.class);
    }

    /**
     * 参考 {@link org.springframework.security.oauth2.common.OAuth2AccessTokenJackson2Deserializer}
     */
    @Override
    public MyUser deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        String username = null;
        String realname = null;
        while (p.nextToken() != JsonToken.END_OBJECT) {
            String name = p.getCurrentName();
            p.nextToken();
            if ("username".equals(name)) {
                username = p.getText();
            } else if ("realname".equals(name)) {
                realname = p.getText();
            }
        }
        return new MyUser(username, realname);
    }

    /**
     * 使用了@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class") 注解后
     * 最好重写这个方法，避免 json 串中没有 @class 属性时报错 Missing type id when trying to resolve subtype of  ...
     * 因为使用了 @JsonTypeInfo 注解后，会将实际的反序列化类包装成一个 {@link TypeWrappedDeserializer} 对象
     * @see TypeWrappedDeserializer#deserialize( JsonParser, DeserializationContext) -> 实际的反序列化类执行的是 deserializeWithType 方法
     */
    @Override
    public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
        // TODO 按实际业务来对 typeDeserializer 进行处理
        String propertyName = typeDeserializer.getPropertyName();
        System.out.println("typeId：" + propertyName);
        TypeIdResolver typeIdResolver = typeDeserializer.getTypeIdResolver();
        System.out.println("typeIdResolver：" + typeIdResolver);
        Class<?> defaultImpl = typeDeserializer.getDefaultImpl();
        System.out.println("defaultImpl：" + defaultImpl);
        return deserialize(p, ctxt);
    }
}
