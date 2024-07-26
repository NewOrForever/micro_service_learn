package com.example.springboot;

import com.example.springboot.domain.User;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.spring.autoconfigure.RocketMQAutoConfiguration;
import org.apache.rocketmq.spring.core.RocketMQLocalRequestCallback;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * ClassName:SpringProducer
 * Package:com.example.springboot
 * Description: 只要注入RocketMQTemplate就可以成为生产者
 * RocketMQTemplate是中封装了真正的 producer（实际是 {@link org.apache.rocketmq.client.producer.TransactionMQProducer} 能发送事务消息的生产者）
 * 可以直接使用RocketMQTemplate发送消息
 * RocketMQTemplate虽然封装了producer，但是我们可以通过RocketMQTemplate获取producer，然后进行一些高级操作
 *
 * @Date:2024/7/23 8:40
 * @Author:qs@1.com
 */
@Component
public class SpringProducer {
    @Autowired
    @Qualifier(RocketMQAutoConfiguration.ROCKETMQ_TEMPLATE_DEFAULT_GLOBAL_NAME)
    private RocketMQTemplate rocketMQTemplate;

    public void sendMessageInTransaction(String topic, String msg) throws InterruptedException {
        String[] tags = new String[]{"TagA", "TagB", "TagC"};
        for (int i = 0; i < 10; i++) {
            /**
             * 使用 RocketMQTemplate 发送消息时 topic 与 tags 是绑定在一起以 ':' 分隔作为 destination 进行发送的
             */
            String destination = topic + ":" + tags[i % tags.length];
            // 转成 spring 的 Message 对象
            Message<String> message = MessageBuilder.withPayload(msg).build();
            // 发送事务消息
            SendResult sendResult = rocketMQTemplate.sendMessageInTransaction(destination, message, null);
            System.out.printf("发送事务消息结果：%s%n", sendResult);

            Thread.sleep(10);
        }
    }

    public RocketMQTemplate getRocketMQTemplate() {
        return rocketMQTemplate;
    }

    public void syncSend(String topic, String msg) {
        Message<String> message = MessageBuilder.withPayload(msg).build();
        String destination = topic + ":TagNormal";
        SendResult sendResult = rocketMQTemplate.syncSend(destination, message);
        System.out.printf("发送普通消息结果：%s%n", sendResult);
    }

    public void convertAndSend(String topic, Object playload) {
        String destination = topic + ":TagUser";
        rocketMQTemplate.convertAndSend(destination, playload);
    }

    public void asyncSend(String topic, String msg) {
        String destination = topic + ":TagAsync";
        rocketMQTemplate.asyncSend(destination, MessageBuilder.withPayload("spring boot async send message").build(), new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                System.out.printf("异步发送消息成功：%s%n", sendResult);
            }

            @Override
            public void onException(Throwable e) {
                System.out.printf("异步发送消息失败：%s%n", e);
            }
        });
    }

    public void sendAndReceive(String topic, String msg) {
        // Send request in sync mode with timeout parameter and receive a reply of byte[] type.
        String destination = topic + ":TagSendAndReceive";
        byte[] replyBytes = rocketMQTemplate.sendAndReceive(destination, MessageBuilder.withPayload(msg).build(), byte[].class, 5000);
        System.out.printf("send %s and receive %s %n", "request byte[]", new String(replyBytes));
    }

    public void asyncSendAndReceive(String topic, Object playload) {
        // Send request in async mode and receive a reply of User type.
        String destination = topic + ":TagAsyncSendAndReceive";
        rocketMQTemplate.sendAndReceive(destination, playload, new RocketMQLocalRequestCallback<User>() {
            @Override
            public void onSuccess(User message) {
                System.out.printf("send user object and receive %s %n", message.toString());
            }

            @Override
            public void onException(Throwable e) {
                e.printStackTrace();
            }
        }, 5000);
    }


    public void testBatchMessages(String topic) {
        List<Message> msgs = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            msgs.add(MessageBuilder.withPayload("Hello RocketMQ Batch Msg#" + i).
                    setHeader(RocketMQHeaders.KEYS, "KEY_" + i).build());
        }

        String destination = topic + ":TagBatch";
        SendResult sendResult = rocketMQTemplate.syncSend(topic, msgs, 60000);

        System.out.println("--- Batch messages send result :" + sendResult);
    }

    public void testSendBatchMessageOrderly(String topic) {
        for (int q = 0; q < 4; q++) {
            // send to 4 queues
            List<Message> msgs = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                int msgIndex = q * 10 + i;
                String msg = String.format("Hello RocketMQ Batch Msg#%d to queue: %d", msgIndex, q);
                msgs.add(MessageBuilder.withPayload(msg).
                        setHeader(RocketMQHeaders.KEYS, "KEY_" + msgIndex).build());
            }

            String destination = topic + ":TagBatchOrderly";
            /**
             * hashKey: 作为消息的分区键 sharding key，用于选择发送到的队列
             * 这里 q 作为 sharding key，可以保证同一个 q 的消息会发送到同一个队列，保证同一组的消息有序
             * 这里的 q 取值范围是 0-3，分成了 4 组，每组 10 条消息
             */
            SendResult sr = rocketMQTemplate.syncSendOrderly(destination, msgs, q + "", 60000);
            System.out.println("--- Batch messages orderly to queue :" + sr.getMessageQueue().getQueueId() + " send result :" + sr);
        }
    }

}
