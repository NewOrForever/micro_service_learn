package com.example.mongodb.config;

import com.example.mongodb.model.LocationHisTrack;
import org.apache.commons.lang.time.DateUtils;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterRegistry;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.data.convert.CustomConversions;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.mapping.PersistentPropertyAccessor;
import org.springframework.data.mapping.model.SpELExpressionEvaluator;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.CollectionCallback;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.AbstractMongoConverter;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.core.mapping.MongoPersistentEntity;
import org.springframework.data.mongodb.core.mapping.MongoPersistentProperty;
import org.springframework.data.util.TypeInformation;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * ClassName:MongoConfiguration
 * Package:com.example.mongodb.config
 * Description: 自定一个转换器，将String转换为Date
 * 1.自定义一个转换器，实现Converter接口
 * 2.在MongoConfiguration中，使用@Bean注解，将自定义的转换器注册到Spring容器中
 * 3.使用 @ReadingConverter注解，表示在读取数据时，使用该转换器将String转换为Date
 *
 * @Date:2024/1/5 16:26
 * @Author:qs@1.com
 */
@Configuration
public class MongoConfiguration {

    /**
     * @see MongoDatabaseFactoryDependentConfiguration#mappingMongoConverter(MongoDatabaseFactory, MongoMappingContext, MongoCustomConversions)
     * 在该配置方法注入 MongoCustomConversions
     * {@link MappingMongoConverter} extends {@link AbstractMongoConverter} implements {@link InitializingBean}
     * {@link MongoCustomConversions} extends {@link CustomConversions} -> mappingMongoConverter 调用 setCustomConversions 方法
     * 将 MongoCustomConversions 赋值给 {@link AbstractMongoConverter#conversions}
     *
     * {@link MappingMongoConverter} 执行 {@link AbstractMongoConverter#afterPropertiesSet()} 方法 ->
     * {@link AbstractMongoConverter#initializeConverters()} ->
     * {@link CustomConversions#registerConvertersIn(ConverterRegistry)} 将自定义的 Converter 注册到 {@link GenericConversionService} 中 ->
     * @see CustomConversions#registerConverterIn(Object, ConverterRegistry) -> {@link ConverterRegistry#addConverter(Converter)}
     * @see GenericConversionService#addConverter(Converter) ->
     * 通过创建 {@link GenericConversionService.ConverterAdapter} implements {@link GenericConverter} 适配器将 Converter 转换为 {@link GenericConverter} ->
     * 最后都添加到 {@link GenericConversionService#converters} 属性对象的 {@link GenericConversionService.Converters#converters} 中
     * @return
     */
    @Bean
    public MongoCustomConversions mongoCustomConversions() {
        List<Converter<?,?>> converters = new ArrayList<Converter<?,?>>();

        converters.add(new DateConverter());

        return new MongoCustomConversions(converters);
    }

    /**
     * 不通版本的 Spring Data MongoDB 可能执行的方法可能有点差异，但是大体的流程基本都是一样的
     * @see ReadingConverter
     * @see org.springframework.data.convert.WritingConverter
     * @see CustomConversions.StoreConversions#getRegistrationsFor(Object)
     * findOne 查询举例
     * @see MongoTemplate#executeFindOneInternal(CollectionCallback, MongoTemplate.DocumentCallback, String) ->
     * @see MongoTemplate.ReadDocumentCallback#doWith(Document) ->
     * @see MappingMongoConverter#read(Class, Bson) ->
     * @see MappingMongoConverter#read(TypeInformation, Bson, org.springframework.data.mongodb.core.convert.ObjectPath) ->
     * @see MappingMongoConverter#populateProperties(MongoPersistentEntity, DocumentAccessor, ObjectPath, SpELExpressionEvaluator, Object) ->
     * @see MappingMongoConverter#readProperties(MongoPersistentEntity, PersistentPropertyAccessor, DocumentAccessor, MappingMongoConverter.MongoDbPropertyValueProvider, ObjectPath, SpELExpressionEvaluator) ->
     * @see MappingMongoConverter.MongoDbPropertyValueProvider#getPropertyValue(MongoPersistentProperty)
     * @see MappingMongoConverter#readValue(Object, TypeInformation, ObjectPath) -> hasCustomReadTarget 就是有没有 @ReadingConverter 的Converter ->
     * 没配自定义的Converter 则进入 {@link MappingMongoConverter#getPotentiallyConvertedSimpleRead(Object, Class)} ->
     * @see GenericConversionService#convert(Object, Class) -> {@link GenericConversionService#getConverter(TypeDescriptor, TypeDescriptor)} ->
     * @see GenericConversionService#getConverter(TypeDescriptor, TypeDescriptor)
     * 从 {@link GenericConversionService#converters} 的 {@link GenericConversionService.Converters#converters} 中获取匹配的 {@link GenericConverter} ->
     * @see  ConversionUtils#invokeConverter(GenericConverter, Object, TypeDescriptor, TypeDescriptor) ->
     * @see GenericConverter#convert(Object, TypeDescriptor, TypeDescriptor) 执行转换
     *
     * {@link LocationHisTrack#startTime} 这个字段是 {@link Date} 类型的，但是通过使用 @JsonFormat 使得存入 MongoDB 的是 {@link String} 类型的
     * 所以在读取的时候需要将 {@link String} 类型的转换为 {@link Date} 类型的
     * 如果没自定义这个 DateConverter 那么匹配的 {@link GenericConverter} 就是 {@link ObjectToObjectConverter} ->
     * 最终会执行 {@link java.util.Date#Date(java.lang.String)} 方法，yyyy-MM-dd HH:mm:ss 格式的日期字符串进来就会报格式不正确的异常 ->
     * 所以需要自定义一个 {@link Converter} 将 {@link String} 类型的转换为 {@link Date} 类型
     */
    @ReadingConverter
    public static class DateConverter implements Converter<String, Date> {
        @Override
        public Date convert(String source) {
            try {
                return DateUtils.parseDate(source, new String[]{"yyyy-MM-dd HH:mm:ss"});
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
