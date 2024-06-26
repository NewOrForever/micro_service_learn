/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.rocketmq.ordermessage;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.client.consumer.store.OffsetStore;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class Consumer {

    public static void main(String[] args) throws MQClientException {
       // 实例化消费者
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("orderMsgConsumerGroup");
        // 设置NameServer的地址
        consumer.setNamesrvAddr("192.168.50.65:9876");
        // 设置消费其实位置
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        // 订阅一个或者多个Topic，以及Tag来过滤需要消费的消息
        consumer.subscribe("TopicTest", "TagA || TagC || TagD");
        // 注册消息监听器来处理从broker拉取回来的消息
        // 使用 MessageListenerOrderly 在RocketMQ内部就会通过锁队列的方式保证消息是一个一个队列来取的
        consumer.registerMessageListener(new MessageListenerOrderly() {
            AtomicLong consumeTimes = new AtomicLong(0);
            @Override
            public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
                // 设置自动提交
                context.setAutoCommit(true);
                System.out.printf("%s Receive New Messages: %s %n", Thread.currentThread().getName(), msgs);
                this.consumeTimes.incrementAndGet();
                if ((this.consumeTimes.get() % 2) == 0) {
                    return ConsumeOrderlyStatus.SUCCESS;
                } else if ((this.consumeTimes.get() % 5) == 0) {
                    /**
                     * 暂停当前队列一会儿
                     * 如果消费者在处理消息时遇到了临时的问题，比如需要等待一些资源，可以返回这个状态，这样 RocketMQ 会暂时停止向这个消费者推送消息，过一会儿再继续
                     * 挂起结束后，会继续从上次消费的位置继续消费，当前批次的消息会重新拉取消费
                     * 如果要挂起当前队列，当前批次的消息不要进行业务处理，否则会导致消息重复消费
                     *
                     * 我在测试顺序消息消费时发现，我当前代码中的挂起操作结束后，会重新消费当前批次的消息（控制台打印出了重复的消息）
                     * 挂起后因为当前批次的消息没有消费完成，所以会重新拉取当前批次的消息进行消费（很合理）
                     * 但是一定要注意，如果要挂起当前队列，当前批次的消息不要进行业务处理（如果进行了业务处理就得进行回滚操作），否则会导致消息重复消费
                     * 当然，如果你的业务逻辑是幂等的，那么就不用担心这个问题
                     *
                     * 顺序消息消费测试请使用 {@link ConsumerTest01} {@link ConsumerTest02} {@link ConsumerTest03}
                     */
                    context.setSuspendCurrentQueueTimeMillis(3000);
                    return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
                }

                return ConsumeOrderlyStatus.SUCCESS;
            }
        });
        // 启动消费者
        consumer.start();
        System.out.printf("Consumer Started.%n");
    }

}
