package com.example.rocketmq.test;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ClassName:ConsumeBatchMessage
 * Package:com.example.rocketmq.test
 * Description: 一次消费多条消息
 *
 * @Date:2024/7/5 17:09
 * @Author:qs@1.com
 */
public class ConsumeBatchMessage {
    private static final AtomicInteger count = new AtomicInteger(0);
    public static void main(String[] args) throws MQClientException {
        // 1. 创建消费者
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("ConsumeBatchMessageGroup");
        // 2. 设置nameserver地址
        consumer.setNamesrvAddr("192.168.50.65:9876");
        // 3. 设置订阅关系
        consumer.subscribe("TopicTest", "*");
        /**
         * 设置一次最多能消费多少条消息
         * 也就是消费者从服务器拉取消息时，一次最多能拉取多少条消息需要注意的是，这个值
         * 只是一个上限，实际的消费数量可能会小于这个值。例如，如果服务器中只有5条消息可供消费，那么消费者一次也只能消费这5条消息
         */
        consumer.setConsumeMessageBatchMaxSize(10);
        // 4. 注册消息监听器
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                System.out.println("-----------------------------------------第" + count.incrementAndGet() + "次消费消息数量" + msgs.size() + "-----------------------------------------");
                for (MessageExt msg : msgs) {
                    System.out.printf("%s Receive New Messages: %s %n", Thread.currentThread().getName(), new String(msg.getBody()));
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        // 5. 启动消费者
        consumer.start();
        System.out.printf("Consumer Started.%n");
    }
}
