package com.example.demo.json.mixin;

import com.example.demo.json.deserializer.MyOAuth2AccessTokenJacksonDeserializer;
import com.example.demo.json.serializer.MysOAuth2AccessTokenJacksonSerializer;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;

/**
 * ClassName:MyMixin
 * Package:com.example.authserver
 * Description:
 *
 * @Date:2024/1/23 17:30
 * @Author:qs@1.com
 */

/**
 * @JsonTypeInfo 用于定制JSON的序列化和反序列化过程。
 * use = JsonTypeInfo.Id.CLASS：这表示在JSON中使用的类型信息将是类本身的名称。
 * property = "@class"：表示类型信息将被存储在名为"@class"的属性中
 * 使用该注解的化就需要对应的 {@link com.fasterxml.jackson.databind.JsonSerializer} 实现类实现{@link JsonSerializer#serializeWithType(Object, JsonGenerator, SerializerProvider, TypeSerializer)}
 * {@link com.fasterxml.jackson.databind.JsonDeserializer} 实现类需要实现{@link JsonDeserializer#deserializeWithType(JsonParser, DeserializationContext, TypeDeserializer)}
 * 如果没有实现这两个方法，那么在序列和反序列化的时候会报错 Missing type id when trying to resolve subtype of  ...
 * 当然，如果不使用@JsonTypeInfo注解，那么就不需要实现这两个方法了 <br/>
 * <b><span style="color:red">补充</span><br/>
 * 1. 序列化反序列化类型的信息，可以使用@JsonTypeInfo注解
 * 2. JsonTypeInfo.Id.CLASS：序列化时会将类型信息写入到json串中，反序列化时会从json串中读取类型信息 （@class 属性）
 *  - 反序列化时 json 串中必须包含 @class 属性，否则会报错 Missing type id when trying to resolve subtype of  ...
 *  - 如果json 中不包含 @class 属性，那么需要重写对应的序列化反序列化类，实现serializeWithType 和 deserializeWithType 方法
 * 3. JsonTypeInfo.Id.NONE: 序列化时不会将类型信息写入到json串中，反序列化时也不会从json串中读取类型信息
 * </b>
 *
 * @JsonSerialize 用于指定序列化时的自定义行为。
 * using = MysOAuth2AccessTokenJacksonSerializer.class：表示使用MysOAuth2AccessTokenJacksonSerializer类作为序列化时的自定义方式
 *
 * @JsonAutoDetect 用于定制自动检测哪些字段和getter方法应该被序列化和反序列化。
 * fieldVisibility = JsonAutoDetect.Visibility.ANY：表示任何字段都可以被序列化或反序列化。
 * getterVisibility = JsonAutoDetect.Visibility.NONE：表示getter方法不会被自动检测为需要序列化或反序列化的字段。
 * isGetterVisibility = JsonAutoDetect.Visibility.NONE：表示isXXX()方法（例如isVisible()）不会被自动检测为需要序列化或反序列化的字段。
 *
 * @JsonIgnoreProperties 用于指定在反序列化时忽略哪些属性。
 * ignoreUnknown = true：表示如果JSON中存在未知属性，它们将被忽略，而不是引发错误。
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
@JsonSerialize(using = MysOAuth2AccessTokenJacksonSerializer.class)
@JsonDeserialize(using = MyOAuth2AccessTokenJacksonDeserializer.class)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class MyOAuth2AccessTokenJacksonMixin {
}

