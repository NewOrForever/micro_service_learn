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
package com.example.rocketmq.schedule;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

public class ScheduledMessageProducer {

    public static final String PRODUCER_GROUP = "ScheduleProducerGroup";
    public static final String DEFAULT_NAMESRVADDR = "192.168.50.65:9876";
    public static final String TOPIC = "TopicTestDelay";

    public static void main(String[] args) throws Exception {
        // Instantiate a producer to send scheduled messages
        DefaultMQProducer producer = new DefaultMQProducer(PRODUCER_GROUP);

        producer.setNamesrvAddr(DEFAULT_NAMESRVADDR);

        // Launch producer
        producer.start();
        int totalMessagesToSend = 100;
        for (int i = 0; i < totalMessagesToSend; i++) {
            Message message = new Message(TOPIC, ("Hello scheduled message " + i).getBytes());
            /**
             * This message will be delivered to consumer 10 seconds later.
             * 设置延时级别为3，延时时间为10s
             * 延时投递的等级对应的延时时间官方文档：https://rocketmq.apache.org/zh/docs/4.x/producer/04message3#%E5%BB%B6%E6%97%B6%E6%B6%88%E6%81%AF%E7%BA%A6%E6%9D%9F
              */
            message.setDelayTimeLevel(3);
            // Send the message
            SendResult sendResult = producer.send(message);
            System.out.printf("%s%n", sendResult);
        }

        // Shutdown producer after use.
        producer.shutdown();
    }

}