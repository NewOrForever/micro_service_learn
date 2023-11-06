package com.example.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.Base64Codec;
import sun.misc.BASE64Decoder;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * ClassName:JwtDemo
 * Package:com.example.demo
 * Description:
 *
 * @Date:2023/11/4 14:02
 * @Author:qs@1.com
 */
public class JwtDemo {
    public static void main(String[] args) throws JsonProcessingException {
//        testJwtWithClaims();
        testParseToken();
//        testJwtWithPlayload();
    }

    private static void testJwtWithClaims() {
        JwtBuilder jwtBuilder = Jwts.builder()
                // 声明的标识 {"jti": "888"}
                .setId("888")
                // 主体，用户 {"sub": "fox"}
                .setSubject("fox")
                // 签发时间 {"iat": 1604462400}
                .setIssuedAt(new Date())
                // 过期时间 {"exp": 1699083612}  1 小时
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                // 自定义claims
                .addClaims(new HashMap<String, Object>() {{
                    put("username", "fox");
                    put("age", 18);
                }})
                .claim("address", "北京")
                // 签名手段，参数1：算法，参数2：盐
                .signWith(SignatureAlgorithm.HS256, "hello_jwt");
        // 生成token
        String token = jwtBuilder.compact();
        System.out.println(token);

        /******* 解析token ********/
        /**
         * 三个部分的 base64 解码
         * 这种方式解析出来的 json 串是会有点问题的 - 负载出来的 json 串是有点问题的
          */
        System.out.println("===============");
        String[] split = token.split("\\.");
        String header = split[0];
        String payload = split[1];
        String signature = split[2];
        System.out.println("header解密：" + Base64Codec.BASE64.decodeToString(header));
        System.out.println("payload解密：" + Base64Codec.BASE64.decodeToString(payload));
        // 无法解密
        System.out.println("signature解密：" + Base64Codec.BASE64.decodeToString(signature));

        /**
         * token 的正确解析
         */
        Claims claims = Jwts.parser()
                .setSigningKey("hello_jwt")
                .parseClaimsJws(token)
                .getBody();
        System.out.println(claims);
        System.out.println("过期时间：" + claims.getExpiration());
        System.out.println("签发时间：" + claims.getIssuedAt());
    }

    public static void testParseToken() {
//        String token = "eyJhbGciOiJIUzI1NiJ9" +
//                ".eyJqdGkiOiI4ODgiLCJzdWIiOiJmb3giLCJpYXQiOjE2OTkwNzkwMzQsImFnZSI6MTgsInVzZXJuYW1lIjoiZm94In0" +
//                ".Zhosu8kkWAX6riif6LIhB9Z_Ekf66S_daEvVQ1zPCDg";
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJmb3giLCJzY29wZSI6WyJhbGwiXSwiZXhwIjoxNjk5MDkzMDI0LCJhdXRob3JpdGllcyI6WyJhZG1pbiJdLCJqdGkiOiJlZmI3ODk5My0zOGQ1LTQ3NDEtOTkwZC0yMzY2MWUzOTk4NzMiLCJjbGllbnRfaWQiOiJjbGllbnQiLCJlbmhhbmNlIjoiZW5oYW5jZSBpbmZvIn0.0iCa1Sr7OiqvZzmWN-QbM0JRcBiGtwhpdAN1rEUAu1c";

        /**
         * 这种方式解析出来的 json 串是 ok 的
         */
        Claims claims = Jwts.parser()
                .setSigningKey("jwt_oauth2_secret")
                .parseClaimsJws(token)
                .getBody();
        System.out.println(claims);
    }

    /**
     * claims 和 playload 其实是一样的，都是第二部分负载
     * 只能使用一种方式，不能同时使用，否则会报错
     * 只不过 playload 是一个 json 字符串，且完全自定义数据
     */
    private static void testJwtWithPlayload() throws JsonProcessingException {
        String secret = "hello_jwt";

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> map = new HashMap<>();
        map.put("username", "nicaicaikan");
        map.put("age", 18);
        map.put("address", "北京");
        JwtBuilder jwtBuilder = Jwts.builder()
                .setPayload(objectMapper.writeValueAsString(map))
                // 签名手段，参数1：算法，参数2：盐
                .signWith(SignatureAlgorithm.HS256, secret);
        // 生成token
        String token = jwtBuilder.compact();
        System.out.println(token);

        /******* 解析token ********/
        Claims claims = Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
        System.out.println(claims);
    }
}
