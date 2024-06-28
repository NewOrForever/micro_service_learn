package com.example.rocketmq.simple;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.nio.charset.StandardCharsets;

/**
 * ClassName:SyncProducer
 * Package:com.example.rocketmq.demo
 * Description: 同步发送普通消息
 * <a href="https://github.com/apache/rocketmq/tree/release-4.9.4/example/src/main/java/org/apache/rocketmq/example/simple">官方demo</a>
 * @see org.apache.rocketmq.example.simple.Producer - 官方demo（已经下载下来了）
 * @Date:2024/5/20 14:03
 * @Author:qs@1.com
 */
public class Producer {
    private static final String TOPIC = "TopicTest";
    private static final String TAG = "TagA";
    private static final String MESSAGE_KEY = "OrderID188";
    private static final String PRODUCER_GROUP = "ProducerGroupName";

    public static void main(String[] args) throws MQClientException {
        // 1. 创建生产者
        DefaultMQProducer producer = new DefaultMQProducer(PRODUCER_GROUP);
        // 2. 设置nameserver地址
        producer.setNamesrvAddr("192.168.50.65:9876");
        // 3. 启动生产者
        producer.start();
        // 4. 创建消息
        for (int i = 0; i < 128; i++) {
            Message message = new Message(TOPIC,
                    TAG, // 设置消息Tag，用于消费端根据指定Tag过滤消息
                    MESSAGE_KEY, // 设置消息索引键，可根据关键字精确查找某条消息
                    "Hello world".getBytes(StandardCharsets.UTF_8));
            // 5. 发送消息
            try {
                SendResult sendResult = producer.send(message);
                System.out.printf("%s%n", sendResult);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 6. 关闭生产者
        producer.shutdown();
    }


}
