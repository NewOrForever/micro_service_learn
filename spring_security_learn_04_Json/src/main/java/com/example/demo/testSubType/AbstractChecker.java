package com.example.demo.testSubType;

import com.alibaba.nacos.api.naming.pojo.healthcheck.AbstractHealthChecker;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * ClassName:TestSubTypeApp
 * Package:com.example.demo
 * Description: @JsonSubTypes 主要还是用于反序列化
 * 当反序列化一个JSON对象时，Jackson库将根据"type"属性的值来决定应该将该对象映射到哪个具体的子类
 *
 * @Date:2024/1/31 14:26
 * @Author:qs@1.com
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", defaultImpl = AbstractHealthChecker.None.class)
@JsonSubTypes({@JsonSubTypes.Type(name = HttpChecker.TYPE, value = HttpChecker.class),
        @JsonSubTypes.Type(name = MysqlChecker.TYPE, value = MysqlChecker.class),
        @JsonSubTypes.Type(name = AbstractChecker.None.TYPE, value = AbstractChecker.None.class)})
public abstract class AbstractChecker {
/**
 * copy from {@link com.alibaba.nacos.api.naming.pojo.healthcheck.AbstractHealthChecker}
  */
    @JsonIgnore
    protected final String type;

    protected AbstractChecker(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }


    /**
     * Default implementation of Health checker.
     */
    public static class None extends AbstractChecker {

        public static final String TYPE = "NONE";

        public None() {
            super(TYPE);
        }

    }
}
