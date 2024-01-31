# 这个工程的目的是为了测试解决自定义 Spring Security 中RedisTokenStore 的序列化反序列化问题

使用的是 Jackson 来进行序列化反序列化 <br/>
OAuth2AccessToken 的序列化反序列化问题<br/>
OAuth2Authentication 的序列化反序列化问题<br/>

## 1. 处理 OAuth2AccessToken 的序列化反序列化问题

看 com.example.demo.AccessTokenApp 这个类<br/>
没做任何处理，因为 OAuth2AccessToken 定义了序列化反序列化的方法<br/>

```
@org.codehaus.jackson.map.annotate.JsonSerialize(using = OAuth2AccessTokenJackson1Serializer.class)
@org.codehaus.jackson.map.annotate.JsonDeserialize(using = OAuth2AccessTokenJackson1Deserializer.class)
@com.fasterxml.jackson.databind.annotation.JsonSerialize(using = OAuth2AccessTokenJackson2Serializer.class)
@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = OAuth2AccessTokenJackson2Deserializer.class)
```

## 2. 处理 OAuth2Authentication 的序列化反序列化问题

看 com.example.demo.AuthenticationApp 这个类<br/>
因为 jackson 反序列化时，会调用默认的构造函数，而 OAuth2Authentication 没有默认的构造函数，所以会报错<br/>
自定义 OAuth2Authentication 的反序列化类 OAuth2AuthenticationDeserializer<br/> 注册进 ObjectMapper 中<br/>

```
ObjectMapper objectMapper = new ObjectMapper();
SimpleModule simpleModule = new SimpleModule();
simpleModule.addDeserializer(OAuth2Authentication.class, new OAuth2AuthenticationDeserializer());
objectMapper.registerModule(simpleModule);
```

## 3. 整合代码处理 OAuth2AccessToken 和 OAuth2Authentication 的序列化反序列化问题

看 com.example.demo.TokenAndAuthenticationApp#executeWithoutCoreJacksonModule 这个方法<br/>

## 4. 将 spring-security 自带的 mixin module 类 org.springframework.security.jackson2.CoreJackson2Module 注册进 ObjectMapper 中

看 com.example.demo.TokenAndAuthenticationApp#executeWithCoreJacksonModule 这个方法<br/>
OAuth2AccessToken 和 OAuth2Authentication 的序列化类 如果没有实现 deserializeWithType 和 serializeWithType
方法，那么就会报错<br/>
OAuth2AccessToken 的序列化类需要自己去实现<br/>
```
com.example.demo.json.serializer.MysOAuth2AccessTokenJacksonSerializer
com.example.demo.json.deserializer.OAuth2AuthenticationDeserializer
```
OAuth2Authentication 的反序列化类在解析属性到 Map 时也要注意
```
/**
 * Map<String, String> requestParameters =
 *                 objectMapper.readValue(requestParametersJsonNode.traverse(), new TypeReference<Map<String, String>>() {});
 * 不能使用上面的方式   Map -> {@link com.fasterxml.jackson.databind.deser.std.MapDeserializer}
 * 因为 {@link org.springframework.security.jackson2.CoreJackson2Module} 的原因走的是
 * {@link MapDeserializer#deserializeWithType(JsonParser, DeserializationContext, TypeDeserializer)} 会报错 type id missing
 */
Map<String, String> requestParameters = new HashMap<>();
Iterator<Map.Entry<String, JsonNode>> iterator = requestParametersJsonNode.fields();
while (iterator.hasNext()) {
    Map.Entry<String, JsonNode> entry = iterator.next();
    requestParameters.put(entry.getKey(), entry.getValue().textValue());
}
```

