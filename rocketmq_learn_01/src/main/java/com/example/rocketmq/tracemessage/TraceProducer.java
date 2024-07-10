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

package com.example.rocketmq.tracemessage;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.trace.AsyncTraceDispatcher;
import org.apache.rocketmq.client.trace.TraceDispatcher;
import org.apache.rocketmq.client.trace.hook.SendMessageTraceHookImpl;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;

import java.util.concurrent.TimeUnit;

public class TraceProducer {

    public static final String PRODUCER_GROUP = "TraceProducerGroup";
    public static final String DEFAULT_NAMESRVADDR = "192.168.50.65:9876";
    public static final String TOPIC = "TopicTest";
    public static final String TAG = "TagA";
    public static final String KEY = "OrderID188";
    public static final int MESSAGE_COUNT = 128;

    public static void main(String[] args) throws MQClientException, InterruptedException {

        /**
         * 设置自定义的消息轨迹 topic 使用方式如下：
         * DefaultMQProducer producer = new DefaultMQProducer(PRODUCER_GROUP, true, "CustomizedTraceTopic");
         */
        DefaultMQProducer producer = new DefaultMQProducer(PRODUCER_GROUP, true);

        producer.setNamesrvAddr(DEFAULT_NAMESRVADDR);
        producer.start();

        for (int i = 0; i < MESSAGE_COUNT; i++) {
            try {
                    Message msg = new Message(TOPIC,
                        TAG,
                        KEY,
                        "Hello world".getBytes(RemotingHelper.DEFAULT_CHARSET));
                    SendResult sendResult = producer.send(msg);
                    System.out.printf("%s%n", sendResult);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * 测试的时候，不要让生产者进程退出，不然消息轨迹数据会无法发送到服务器
         * 由于消息轨迹是异步的，所以在发送完消息后，需要等待一段时间，以确保消息轨迹数据已经发送到服务器
         */
        TimeUnit.SECONDS.sleep(5000);
        producer.shutdown();
    }
}
