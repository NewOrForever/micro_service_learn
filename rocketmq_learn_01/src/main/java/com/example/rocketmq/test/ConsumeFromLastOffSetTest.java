package com.example.rocketmq.test;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
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
public class ConsumeFromLastOffSetTest {
    private static final AtomicInteger count = new AtomicInteger(0);
    public static void main(String[] args) throws MQClientException {
        // 1. 创建消费者
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("ConsumeFromLastOffSetGroup");
        // 2. 设置nameserver地址
        consumer.setNamesrvAddr("192.168.50.65:9876");
        // 3. 设置订阅关系
        consumer.subscribe("TopicTest", "*");
        /**
         * 设置消费者从哪里开始消费
         * - CONSUME_FROM_LAST_OFFSET：和最佳实践描述的不一致，最佳实践中描述的是会忽略历史消息，但是实际测试中并不会忽略历史消息
         * 最佳实践官方文档：https://rocketmq.apache.org/zh/docs/4.x/bestPractice/01bestpractice#%E5%85%B3%E4%BA%8E%E6%B6%88%E8%B4%B9%E4%BD%8D%E7%82%B9
         */
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        // 4. 注册消息监听器
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
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
