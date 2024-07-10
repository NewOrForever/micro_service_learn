package com.example.rocketmq.test.retry;

import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ClassName:ConsumeRetryTest
 * Package:com.example.rocketmq.test.retry
 * Description: 并发消费重试测试
 *
 * @Date:2024/7/6 8:35
 * @Author:qs@1.com
 */
public class ConsumeConcurrentRetryTest {
    public static void main(String[] args) throws MQClientException {
        // 1. 创建消费者
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("ConsumeRetryGroup");
        // 2. 设置nameserver地址
        consumer.setNamesrvAddr("192.168.50.65:9876");
        // 3. 设置订阅关系
        consumer.subscribe("RetryTopicTest", "*");
        /**
         * 设置消息最大重试次数
         *  - 该值默认为 -1，在并发消费模式下表示最大重试次数为16次
         *  - 并发消费模式下，消费重试间隔时间是阶梯式的，无法自定义设置
         *     10s, 30s, 1m, 2m, 3m, 4m, 5m, 6m, 7m, 8m, 9m, 10m, 20m, 30m, 1h, 2h，超过16次后，每次间隔2小时
         */
        consumer.setMaxReconsumeTimes(5);
        // 4. 注册消息监听器
        consumer.registerMessageListener(new MessageListenerConcurrently() {

            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                /**
                 * 批量消费时，如果某条消息消费失败，会导致该批次消息全部重试
                 * - 重试次数达到最大重试次数后，消息会进入到死信队列
                 * - 死信队列默认为：%DLQ% + consumerGroupName
                 *
                 * 批量消费时要注意某条消息消费失败的话，将该批次消息的业务逻辑进行回滚，所以每次最大拉取的消息不能太多
                 * 不要考虑只回滚失败的那条消息，因为消息是批量消费的，如果只回滚失败的那条消息，会导致消费位点不一致
                 * 所以我的方案是：实际的消费业务逻辑在业务层中单独写一个方法，如果消费失败，将该批次消息的业务逻辑进行回滚
                 *  - 引入业务service
                 *  - 执行service业务逻辑
                 */
                for (MessageExt msg : msgs) {
                    String body = new String(msg.getBody());
                    System.out.printf("%s Receive New Messages: %s，消息数量: %d %n", Thread.currentThread().getName(), body, msgs.size());
                    // 取body中的最后一个字符，如果是0则模拟消息消费失败
                    int i = Integer.parseInt(StringUtils.substring(body, body.length() - 1));
                    if (i % 3 == 0) {
                        // 模拟消息消费失败直至重试次数达到最大重试次数
                        System.out.printf("消息消费失败，消息内容：%s，消息重试次数：%d %n", new String(msg.getBody()), msg.getReconsumeTimes());
                        return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                    }
                }
                System.out.printf("消息消费成功，消息内容：%s %n", new String(msgs.get(0).getBody()));
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });

        // 5. 启动消费者
        consumer.start();
        System.out.printf("Consumer Started.%n");
    }

}
