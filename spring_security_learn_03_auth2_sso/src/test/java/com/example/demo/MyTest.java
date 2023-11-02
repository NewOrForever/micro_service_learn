//package com.example.demo;
//
//
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.data.redis.core.DefaultHashOperations;
//import org.springframework.test.context.junit4.SpringRunner;
//import redis.clients.jedis.Client;
//import redis.clients.jedis.Jedis;
//
//import java.io.ByteArrayInputStream;
//import java.io.IOException;
//import java.io.ObjectInputStream;
//
//@SpringBootTest(classes = SpringSecurityLearn01Application.class)
//@RunWith(SpringRunner.class)
//public class MyTest {
//
//    /**
//     * 测试反序列化 - 有问题，还是得用 RedisTemplate
//     * @see DefaultHashOperations#entries(Object)
//     *
//     * @throws IOException
//     * @throws ClassNotFoundException
//     */
//    @Test
//    public void testRedisDecode() throws IOException, ClassNotFoundException {
//        byte[] bytes = hget("spring:session:sessions:b8492b51-6380-442e-b983-f16d6c954ff0", "sessionAttr:SPRING_SECURITY_CONTEXT");
//        System.out.println(new String(bytes, "UTF-8"));
//
//    }
//
//
//    // 将十六进制字符串转换为字节数组
//    private static byte[] hexStringToByteArray(String hex) {
//        int len = hex.length();
//        byte[] data = new byte[len / 2];
//        for (int i = 0; i < len; i += 2) {
//            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
//                    + Character.digit(hex.charAt(i + 1), 16));
//        }
//        return data;
//
//    }
//
//    private byte[] hget(String key, String field) {
//        Jedis jedis = new Jedis("123.60.150.23");
//        jedis.auth("root@123456");
//        Client client = jedis.getClient();
//        client.hget(key, field);
//        return client.getBinaryBulkReply();
//    }
//}
