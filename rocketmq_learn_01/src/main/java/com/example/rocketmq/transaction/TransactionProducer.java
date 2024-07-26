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
package com.example.rocketmq.transaction;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.*;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.common.RemotingHelper;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class TransactionProducer {

    public static final String PRODUCER_GROUP = "TransactionProducerGroup";
    public static final String DEFAULT_NAMESRVADDR = "192.168.50.65:9876";
    public static final String TOPIC = "TopicTestTransaction";

    public static final int MESSAGE_COUNT = 10;

    public static void main(String[] args) throws MQClientException, InterruptedException {
        TransactionListener transactionListener = new TransactionListenerImpl();
        TransactionMQProducer producer = new TransactionMQProducer(PRODUCER_GROUP);
        producer.setNamesrvAddr(DEFAULT_NAMESRVADDR);
        ExecutorService executorService = new ThreadPoolExecutor(2, 5, 100, TimeUnit.SECONDS, new ArrayBlockingQueue<>(2000), r -> {
            Thread thread = new Thread(r);
            thread.setName("client-transaction-msg-check-thread");
            return thread;
        });

        /**
         * @see TransactionMQProducer#start() 生产者启动时会调用initTransactionEnv()方法
         * @see org.apache.rocketmq.client.impl.producer.DefaultMQProducerImpl#initTransactionEnv()
         *      - 如果没有设置ExecutorService，会创建一个默认的线程池
         *      - 线程池最终会赋值给 {@link org.apache.rocketmq.client.impl.producer.DefaultMQProducerImpl#checkExecutor} 属性
         */
        producer.setExecutorService(executorService);
        producer.setTransactionListener(transactionListener);
        producer.start();

        String[] tags = new String[] {"TagA", "TagB", "TagC", "TagD", "TagE"};
        for (int i = 0; i < MESSAGE_COUNT; i++) {
            try {
                Message msg =
                    new Message(TOPIC, tags[i % tags.length], "KEY" + i,
                        ("Hello RocketMQ Transaction Msg " + i).getBytes(RemotingHelper.DEFAULT_CHARSET));
                SendResult sendResult = producer.sendMessageInTransaction(msg, null);
                System.out.printf("%s%n", sendResult);

                /**
                 * 非必须
                 * 1. 为了模拟网络延迟
                 * 2. 为了防止消息发送过快，导致消费者处理不过来
                 */
                Thread.sleep(10);
            } catch (MQClientException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < 100000; i++) {
            Thread.sleep(1000);
        }
        producer.shutdown();
    }
}
