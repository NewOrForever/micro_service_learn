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

package com.example.rocketmq.filter;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.MessageSelector;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.impl.MQClientAPIImpl;
import org.apache.rocketmq.client.impl.factory.MQClientInstance;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.SubscriptionData;

import java.util.List;

public class SqlFilterConsumer {

    public static void main(String[] args) throws Exception {

        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("SqlFilterConsumerGroup");
        consumer.setNamesrvAddr("192.168.50.65:9876");
        /**
         * Don't forget to set enablePropertyFilter=true in broker
         * 需要在broker.conf配置文件中添加 enablePropertyFilter=true
         * 否则消费者会报错：The broker does not support consumer to filter message by SQL92
         *
         * @see MQClientInstance#checkClientInBroker()
         *  - 该方法的主要作用是检查消费者在Broker中的状态，确保消费者可以正常地从Broker消费消息
         *      - 这个方法会遍历当前客户端实例中所有的消费者（`consumerTable`），并对每个消费者的订阅信息（`subscriptionInner`）进行检查，如果订阅信息为空，那么就直接返回
         *      - 对于非空的订阅信息，如果其表达式类型（`ExpressionType`）是标签类型（`Tag`），那么就跳过这个订阅信息，继续检查下一个，否则，它会尝试找到订阅主题对应的Broker地址（`addr`）
         *      - 如果找到了Broker地址，那么就会调用`MQClientAPIImpl`的`checkClientInBroker`方法，向Broker发送一个检查请求，请求中包含了消费者组名、客户端ID和订阅信息等内容，这个请求
         *      的目的是检查Broker是否支持消费者使用的过滤表达式类型
         *      - 如果在向Broker发送检查请求的过程中发生了异常，那么就会抛出一个`MQClientException`异常，这个异常的消息会包含一些关于错误的详细信息，例如
         *      可能是因为消费者使用了Broker不支持的过滤表达式类型
         *
         * @see MessageSelector#bySql(String) 会设置消息过滤表达式类型为`SQL92`
         * @see MessageSelector#byTag(String) 会设置消息过滤表达式类型为`TAG`
         *
         * 最终发送给 broker 的订阅信息是一个 `SubscriptionData` 对象 {@link SubscriptionData}
         *  - {@link SubscriptionData#subString} 是消息过滤表达式
         *  - {@link SubscriptionData.SUB_ALL} 表示订阅所有消息
         * 消息过滤表达式类型 {@link SubscriptionData#expressionType}
         */
        consumer.subscribe("SqlFilterTest",
            MessageSelector.bySql("(TAGS is not null and TAGS in ('TagA', 'TagB'))" +
                "and (a is not null and a between 0 and 3)"));

        consumer.registerMessageListener(new MessageListenerConcurrently() {

            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs,
                ConsumeConcurrentlyContext context) {
                System.out.printf("%s Receive New Messages: %s %n", Thread.currentThread().getName(), msgs);
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });

        consumer.start();
        System.out.printf("Consumer Started.%n");
    }
}
