package com.example.demo.testMixin;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

import java.io.IOException;

/**
 * ClassName:MyUserSerializer
 * Package:com.example.demo.testMixin
 * Description:
 *
 * @Date:2024/1/26 11:26
 * @Author:qs@1.com
 */
public class MyUserSerializer extends StdSerializer<MyUser> {
    protected MyUserSerializer() {
        super(MyUser.class);
    }
    @Override
    public void serialize(MyUser value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        System.out.println("serialize");
        gen.writeStartObject();
        gen.writeStringField("username", value.getUsername());
        gen.writeStringField("realname", value.getRealname());
        gen.writeEndObject();}

    @Override
    public void serializeWithType(MyUser value, JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer) throws IOException {
        System.out.println("serializeWithType");
        serialize(value, gen, serializers);
    }
}
