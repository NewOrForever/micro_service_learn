package com.example.demo;

import com.example.demo.json.deserializer.OAuth2AuthenticationDeserializer;
import com.example.demo.json.mixin.MyOAuth2AccessTokenJacksonMixin;
import com.example.demo.testMixin.MyUser;
import com.example.demo.testMixin.MyUserMixin;
import com.example.demo.testMixin.MyUserSerializer;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.BasicDeserializerFactory;
import com.fasterxml.jackson.databind.deser.impl.TypeWrappedDeserializer;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.jsontype.impl.AsPropertyTypeDeserializer;
import com.fasterxml.jackson.databind.jsontype.impl.StdTypeResolverBuilder;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.BasicSerializerFactory;
import com.fasterxml.jackson.databind.ser.BeanSerializerFactory;
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;
import com.fasterxml.jackson.databind.ser.impl.TypeWrappedSerializer;
import com.fasterxml.jackson.databind.util.TokenBuffer;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import java.util.Collection;

/**
 * ClassName:JacksonSourceCodeApp
 * Package:com.example.demo
 * Description: <span style="color:red">Jackson 源码学习</span>
 *
 * @Date:2024/1/26 15:07
 * @Author:qs@1.com
 */
public class JacksonSourceCodeApp {
    public static void main(String[] args) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.addMixIn(OAuth2AccessToken.class, MyOAuth2AccessTokenJacksonMixin.class);
//        SimpleModule simpleModule = new SimpleModule();
//        simpleModule.addDeserializer(OAuth2Authentication.class, new OAuth2AuthenticationDeserializer());
//        objectMapper.registerModule(simpleModule);
        MyUser myUser = new MyUser("test", "hello");

        objectMapper.addMixIn(MyUser.class, MyUserMixin.class);

        /**
         * 序列化源码流程：
         * @see com.fasterxml.jackson.databind.ObjectMapper#_configAndWriteValue(com.fasterxml.jackson.core.JsonGenerator, java.lang.Object)
         * @see DefaultSerializerProvider#serializeValue(JsonGenerator, Object)
         * @see SerializerProvider#findTypedValueSerializer(Class, boolean, BeanProperty)
         *      - {@link SerializerProvider#findValueSerializer(Class, BeanProperty)} 找目标对象匹配的序列化类
         *           - {@link SerializerProvider#_createUntypedSerializer(JavaType)}
         *           - {@link BeanSerializerFactory#createSerializer(SerializerProvider, JavaType)}
         *           - {@link BasicSerializerFactory#findSerializerFromAnnotation(SerializerProvider, Annotated)}
         *           - {@link JacksonAnnotationIntrospector#findSerializer(Annotated)}
         *      - {@link BasicSerializerFactory#createTypeSerializer(SerializationConfig, JavaType)} 创建目标对象的序列化类型信息
         *           - {@link ObjectMapper.DefaultTypeResolverBuilder#buildTypeSerializer(SerializationConfig, JavaType, Collection)}
         *              如果注册进来了 {@link org.springframework.security.jackson2.CoreJackson2Module}
         *              -> 注册了 {@link org.springframework.security.jackson2.CoreJackson2Module}
         *              -> {@link SecurityJackson2Modules#createWhitelistedDefaultTyping()}
         *              -> 匹配 NON_FINAL {@link ObjectMapper.DefaultTypeResolverBuilder#useForType(JavaType)}
         *                  -> JavaType 不是 final 修饰且不是 TreeNode 类型则会去创建 TypeSerializer，否则 TypeSerializer 为 null
         *                  -> JavaType 匹配则执行 {@link StdTypeResolverBuilder#buildTypeDeserializer(DeserializationConfig, JavaType, Collection)} 创建 TypeSerializer
         *      - 如果 createTypeSerializer 返回的不是 null 则创建 {@link TypeWrappedSerializer} 包装类对实际的序列化类进行包装
         * @see com.fasterxml.jackson.databind.ser.DefaultSerializerProvider#_serialize(com.fasterxml.jackson.core.JsonGenerator, java.lang.Object, com.fasterxml.jackson.databind.JsonSerializer)
         *      - 如果 findTypedValueSerializer 返回的是 JsonSerializer 是包装类 {@link com.fasterxml.jackson.databind.ser.impl.TypeWrappedSerializer} 则进入
         *          - {@link TypeWrappedSerializer#serialize(Object, JsonGenerator, SerializerProvider)} 最终调用的是 实际的序列化类的 serializeWithType 方法
         *          - 如果序列化类没有实现 serializeWithType 方法则会报错 type id handling not implemented for type
         *      - 如果 {@link SerializerProvider#findTypedValueSerializer} 返回的 JsonSerializer 是实际的序列化类则直接调用序列化类的 serialize 方法
         */
        String s = objectMapper.writeValueAsString(myUser);
        System.out.println(s);

//        String json = "{\"@class\":\"com.example.demo.testMixin.MyUser\",\"username\":\"test\",\"realname\":\"hello\"}";
        String json = "{\"username\":\"test\",\"realname\":\"hello\"}";

        /**
         * 反序列化源码流程：
         * @see ObjectMapper#_readMapAndClose(JsonParser, JavaType)
         * @see _findRootDeserializer(ctxt, valueType) -> {@link ObjectMapper#_findRootDeserializer(DeserializationContext, JavaType)}
         *      - {@link DeserializationContext#findRootValueDeserializer(JavaType)}
         *      - _factory.findTypeDeserializer(_config, type) -> {@link BasicDeserializerFactory#findTypeDeserializer(DeserializationConfig, JavaType)}
         *          - b.buildTypeDeserializer(config, baseType, subtypes) -> {@link ObjectMapper.DefaultTypeResolverBuilder#buildTypeDeserializer(DeserializationConfig, JavaType, Collection)}
         *          - useForType(baseType) ? super.buildTypeDeserializer(config, baseType, subtypes) : null
         *              - {@link ObjectMapper.DefaultTypeResolverBuilder#useForType(JavaType)} JavaType 匹配
         *                  则执行 {@link StdTypeResolverBuilder#buildTypeDeserializer(DeserializationConfig, JavaType, Collection)} 创建 {@link com.fasterxml.jackson.databind.jsontype.TypeDeserializer}
         *      - 如果 {@link DeserializationContext#findRootValueDeserializer(JavaType)} 返回的 {@link TypeWrappedDeserializer} 不为null
         *          则创建包装类 {@link TypeWrappedDeserializer} 对实际的反序列化类进行包装
         * @see deser.deserialize(p, ctxt)
         *      - 如果 {@link ObjectMapper#_findRootDeserializer} 返回的是包装类 {@link com.fasterxml.jackson.databind.deser.impl.TypeWrappedDeserializer} 则进入
         *          - {@link TypeWrappedDeserializer#deserialize(JsonParser, DeserializationContext)} 最终调用的是实际的反序列化类的 deserializeWithType 方法
         *          -> 以 @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class") 为例
         *          - {@link AsPropertyTypeDeserializer#deserializeTypedFromAny(JsonParser, DeserializationContext)}
         *          - {@link AsPropertyTypeDeserializer#deserializeTypedFromObject(JsonParser, DeserializationContext)}
         *              - if (name.equals(_typePropertyName)
         *                     || (ignoreCase && name.equalsIgnoreCase(_typePropertyName))) 如果json串中包含 @class 属性则进入
         *                  - {@link AsPropertyTypeDeserializer#_deserializeTypedForId(JsonParser, DeserializationContext, TokenBuffer)}
         *                  - deser.deserialize(p, ctxt) -> 进入实际的反序列化类的 deserialize 方法
         *                  >>> 这种 反序列类没有实现 deserializeWithType 方法但是 json 串中包含 @class 属性的情况下是能够正常反序列化的
         *              - 如果 json 串中不包含 @class 属性则进入
         *              -> {@link AsPropertyTypeDeserializer#_deserializeTypedUsingDefaultImpl(JsonParser, DeserializationContext, TokenBuffer)}
         *              >>> 这种 反序列类没有实现 deserializeWithType 方法且 json 串中不包含 @class 属性的情况下是不能够正常反序列化的
         *              >>> 除非设置了 defaultImpl （具体怎么弄就不研究了）
         *      - 如果 {@link ObjectMapper#_findRootDeserializer} 返回的是实际的反序列化类则直接调用反序列化类的 deserialize 方法
         */
        MyUser jsonUser = objectMapper.readValue(json, MyUser.class);
        System.out.println(jsonUser);
    }
}
