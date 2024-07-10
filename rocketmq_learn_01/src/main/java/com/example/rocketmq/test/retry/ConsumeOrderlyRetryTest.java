package com.example.rocketmq.test.retry;

import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.*;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;

/**
 * ClassName:ConsumeRetryTest
 * Package:com.example.rocketmq.test.retry
 * Description: 并发消费重试测试
 *
 * @Date:2024/7/6 8:35
 * @Author:qs@1.com
 */
public class ConsumeOrderlyRetryTest {
    public static void main(String[] args) throws MQClientException {
        // 1. 创建消费者
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("ConsumeOrderlyRetryGroup");
        // 2. 设置nameserver地址
        consumer.setNamesrvAddr("192.168.50.65:9876");
        // 3. 设置订阅关系
        consumer.subscribe("RetryOrderlyTopicTest", "*");
        /**
         * 设置消息最大重试次数
         *  - 该值默认为 -1，在顺序消费模式下表示最大重试次数为 Integer.MAX_VALUE 次
         */
        consumer.setMaxReconsumeTimes(5);
        /**
         * 顺序消费模式下，消费重试间隔时间是固定的，可以自定义设置
         * 需结合 {@link ConsumeOrderlyStatus#SUSPEND_CURRENT_QUEUE_A_MOMENT} 使用
         */
        consumer.setSuspendCurrentQueueTimeMillis(5000);
        // 4. 注册消息监听器
        consumer.registerMessageListener(new MessageListenerOrderly() {
            @Override
            public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
                for (MessageExt msg : msgs) {
                    String body = new String(msg.getBody());
                    System.out.printf("%s Receive New Messages: %s，消息数量: %d %n", Thread.currentThread().getName(), body, msgs.size());
                    // 取body中的最后一个字符，如果是0则模拟消息消费失败
                    int i = Integer.parseInt(StringUtils.substring(body, body.length() - 1));
                    if (i % 3 == 0) {
                        // 模拟消息消费失败直至重试次数达到最大重试次数
                        System.out.printf("消息消费失败，消息内容：%s，消息重试次数：%d %n", new String(msg.getBody()), msg.getReconsumeTimes());
                        /**
                         * 结合 {@link DefaultMQPushConsumer#suspendCurrentQueueTimeMillis} 使用
                         */
                        return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
                    }
                }
                System.out.printf("消息消费成功，消息内容：%s %n", new String(msgs.get(0).getBody()));
                return ConsumeOrderlyStatus.SUCCESS;
            }
        });

        // 5. 启动消费者
        consumer.start();
        System.out.printf("Consumer Started.%n");
    }

}
