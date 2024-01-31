package com.example.demo.testSubType;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.jsontype.impl.*;
import com.fasterxml.jackson.databind.util.TokenBuffer;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * ClassName:TestSubTypeApp
 * Package:com.example.demo.testSubType
 * Description:
 *
 * @Date:2024/1/31 14:41
 * @Author:qs@1.com
 */
public class TestSubTypeApp {
    public static void main(String[] args) {
        ObjectMapper objectMapper = new ObjectMapper();

        HttpChecker httpChecker = new HttpChecker();
        httpChecker.setPath("http://www.baidu.com");
        httpChecker.setHeaders("this is header");
        // 序列化
        try {
            String json = objectMapper.writeValueAsString(httpChecker);
            System.out.println(json);

            /**
             * 反序列化
             * 设计 subtype 的主要源码：
             * @see com.fasterxml.jackson.databind.deser.BasicDeserializerFactory#findTypeDeserializer(com.fasterxml.jackson.databind.DeserializationConfig, com.fasterxml.jackson.databind.JavaType)
             * -> config.getSubtypeResolver().collectAndResolveSubtypesByTypeId(config, ac)  找subType 信息 -> {@link StdSubtypeResolver#collectAndResolveSubtypesByTypeId(MapperConfig, AnnotatedClass)}
             *      -> {@link StdSubtypeResolver#_collectAndResolveByTypeId(AnnotatedClass, NamedType, MapperConfig, Set, Map)}
             *      -> ai.findSubtypes(annotatedType) -> {@link JacksonAnnotationIntrospector#findSubtypes(Annotated)}
             * -> b.buildTypeDeserializer(config, baseType, subtypes)  构建 TypeDeserializer -> {@link StdTypeResolverBuilder#buildTypeDeserializer(DeserializationConfig, JavaType, Collection)}
             *      -> idResolver(config, baseType, subTypeValidator, subtypes, false, true) 构建 TypeIdResolver -> {@link StdTypeResolverBuilder#idResolver(MapperConfig, JavaType, PolymorphicTypeValidator, Collection, boolean, boolean)}
             *          -> {@link AbstractChecker}配置的是 JsonTypeInfo.Id.NAME -> 返回 {@link com.fasterxml.jackson.databind.jsontype.impl.TypeNameIdResolver}
             *      -> 返回 {@link AsPropertyTypeDeserializer}
             *
             * @see com.fasterxml.jackson.databind.deser.impl.TypeWrappedDeserializer#deserialize(JsonParser, DeserializationContext)
             *      -> {@link com.fasterxml.jackson.databind.deser.AbstractDeserializer#deserializeWithType(JsonParser, DeserializationContext, TypeDeserializer)}
             *      -> typeDeserializer.deserializeTypedFromObject(p, ctxt) -> {@link AsPropertyTypeDeserializer#deserializeTypedFromObject(JsonParser, DeserializationContext)}
             *          ->  if (name.equals(_typePropertyName)
             *                     || (ignoreCase && name.equalsIgnoreCase(_typePropertyName))) if (name.equals(_typePropertyName)
             *                     || (ignoreCase && name.equalsIgnoreCase(_typePropertyName))) 找到了属性名和配置的一样的属性
             *                  {@link AsPropertyTypeDeserializer#_deserializeTypedForId(JsonParser, DeserializationContext, TokenBuffer)}
             *                  -> _findDeserializer(ctxt, typeId) -> {@link TypeDeserializerBase#_findDeserializer(DeserializationContext, String)}
             *                      -> _idResolver.typeFromId(ctxt, typeId) -> {@link TypeNameIdResolver#typeFromId(DatabindContext, String)} 找到了对应的子类
             *                      -> 通常返回 {@link com.fasterxml.jackson.databind.deser.BeanDeserializer} -> 进行反序列化
             *
              */
            AbstractChecker abstractChecker = objectMapper.readValue(json, AbstractChecker.class);
            System.out.println(abstractChecker);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
