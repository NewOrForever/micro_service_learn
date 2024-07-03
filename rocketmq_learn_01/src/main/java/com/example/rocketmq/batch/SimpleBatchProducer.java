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
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.example.rocketmq.batch;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.MixAll;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageBatch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SimpleBatchProducer {

    public static final String PRODUCER_GROUP = "BatchProducerGroup";
    public static final String DEFAULT_NAMESRVADDR = "192.168.50.65:9876";
    public static final String TOPIC = "BatchTest";
    public static final String TAG = "Tag";

    public static void main(String[] args) throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer(PRODUCER_GROUP);
        producer.setNamesrvAddr(DEFAULT_NAMESRVADDR);
        producer.start();

        /**
         * If you just send messages of no more than 1MiB at a time, it is easy to use batch
         * Messages of the same batch should have: same topic, same waitStoreMsgOK and no schedule support
         * 消息总量不要超过1M
         *
         * 使用批量消息时，创建的消息需要满足以下条件：
         * 1. 同一个topic
         * 2. 相同 waitStoreMsgOK
         * 3. 不支持延时消息
         * 4. 不支持以 {@link MixAll#RETRY_GROUP_TOPIC_PREFIX} 开头的topic
         * 具体看 {@link MessageBatch#generateFromList(Collection)}
         */
        List<Message> messages = new ArrayList<>();
        Message message1 = new Message(TOPIC, TAG, "OrderID001", "Hello world Batch Msg 0".getBytes());
        Message message2 = new Message(TOPIC, TAG, "OrderID002", "Hello world Batch Msg 1".getBytes());
        Message message3 = new Message(TOPIC, TAG, "OrderID003", "Hello world Batch Msg 2".getBytes());
        messages.add(message1);
        messages.add(message2);
        messages.add(message3);

        /**
         * 生产批量消息核心方法
         * @see DefaultMQProducer#batch(Collection)
         *  @see MessageBatch#generateFromList(Collection)
         */
        SendResult sendResult = producer.send(messages);
        System.out.printf("%s", sendResult);

        producer.shutdown();
    }
}
