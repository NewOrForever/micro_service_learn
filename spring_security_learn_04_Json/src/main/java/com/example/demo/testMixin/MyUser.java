package com.example.demo.testMixin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * ClassName:MyUser
 * Package:com.example.demo.model
 * Description:
 *
 * @Date:2024/1/25 17:17
 * @Author:qs@1.com
 */
public class MyUser {
    private String username;
    private String realname;

    public MyUser(String username, String realname) {
        this.username = username;
        this.realname = realname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public static void main(String[] args) throws JsonProcessingException {
//        testMixinWithJsonCreator();
        testMixinWithCustomSerOrDeser();
    }

    private static void testMixinWithCustomSerOrDeser() throws JsonProcessingException {
        /**
         * MyUser 没有无参构造函数
         */
        MyUser myUser = new MyUser("test", "hello");
        ObjectMapper objectMapper = new ObjectMapper();
        /**
         * 添加 Mixin
         * @JsonTypeInfo 可以不用，像我这里使用了 JsonTypeInfo.Id.CLASS 的话
         * 1. 会在序列化的时候往 json 串中添加一个 @class 属性，值为对象类型全名
         * 2. 如果反序列化的时候 json 串中没有这个 @class 属性，那么会直接报错 Missing type id
         * 3. 如果反序列化的时候 json 串中 @class 属性值不是 readValue 中需要的类型的相同类型或者子类型则会报错
         * Could not resolve type id '' as a subtype of `com.example.demo.testJsonCreator.MyUser`: no such class found
         * 4. @class 正确加上后，报错没有无参构造方法: Cannot construct instance of `com.example.demo.testMixin.MyUser` (no Creators, like default constructor, exist)
         *     - 所以需要自己实现反序列类（MyUserDeserializer），在 MyUserMixin 上加上 @JsonDeserialize(using = MyUserDeserializer.class)
         *     - 自己实现 deserializeWithType 后 json 串中的 @class 不加也行，因为没有走验证 类型匹配的方法，当然如果自己实行的代码中调用了相应的方法的化还是得加上
         */
        objectMapper.addMixIn(MyUser.class, MyUserMixin.class);

        /**
         * 没有加 Mixin 可以序列化
         * 加入 Mixin 可以序列化，json 串中添加一个 @class 属性，值为 MyUser 类型全名
         */
        String s = objectMapper.writeValueAsString(myUser);
        System.out.println(s);

        String json = "{\"@class\":\"com.example.demo.testMixin.MyUser\",\"username\":\"test\",\"realname\":\"hello\"}";
        /**
         * 没有加 Mixin 反序列化报错
         * 加 Mixin 反序列化也会报错：
         * 1. json 串没有 @class 值报 type id missing，@class 值不是同一类型报错：Could not resolve type id '' as a subtype of
         * 2. @class 正确加上后，报错没有无参构造方法: Cannot construct instance of `com.example.demo.testMixin.MyUser` (no Creators, like default constructor, exist)
         * 3. 所以需要自己实现反序列类（MyUserDeserializer），在 MyUserMixin 上加上 @JsonDeserialize(using = MyUserDeserializer.class)
         *    - 自己实现 deserializeWithType 后 json 串中的 @class 不加也行，因为没有走验证 类型匹配的方法，当然如果自己实行的代码中调用了相应的方法的化还是得加上
         */
        MyUser jsonUser = objectMapper.readValue(json, MyUser.class);
        System.out.println(jsonUser);
    }

    /**
     * @JsonCreator 指定构造函数的测试
     * @throws JsonProcessingException
     */
    private static void testMixinWithJsonCreator() throws JsonProcessingException {
        /**
         * MyUser 没有无参构造函数
         */
        MyUser myUser = new MyUser("test", "hello");
        ObjectMapper objectMapper = new ObjectMapper();
        /**
         * 添加 Mixin 使用 @JsonCreator 注解来指定反序列化时创建对象的构造函数
         * @JsonTypeInfo 可以不用，像我这里使用了 JsonTypeInfo.Id.CLASS 的话
         * 1. 会在序列化的时候往 json 串中添加一个 @class 属性，值为对象类型全名
         * 2. 如果反序列化的时候 json 串中没有这个 @class 属性，那么会直接报错 Missing type id
         * 3. 如果反序列化的时候 json 串中 @class 属性值不是 readValue 中需要的类型的相同类型或者子类型则会报错
         * Could not resolve type id '' as a subtype of `com.example.demo.testJsonCreator.MyUser`: no such class found
         */
        objectMapper.addMixIn(MyUser.class, MyUserMixinWithJsonCreator.class);

        /**
         * 没有加 Mixin 可以序列化
         */
        String s = objectMapper.writeValueAsString(myUser);
        System.out.println(s);

        String json = "{\"@class\": \"com.example.demo.testMixin.MyUser\", \"username\":\"test\",\"realname\":\"hello\"}";
        /**
         * 没有加 Mixin 反序列化报错
         */
        MyUser jsonUser = objectMapper.readValue(json, MyUser.class);
        System.out.println(jsonUser);
    }
}
