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

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * 生产顺序消息
 */
public class Producer {
    public static void main(String[] args) throws UnsupportedEncodingException {
        // 实例化生产者
        DefaultMQProducer producer = new DefaultMQProducer("orderMsgProducerGroup");
        // 设置NameServer的地址
        producer.setNamesrvAddr("192.168.50.65:9876");
        try {
            // 启动生产者
            producer.start();
            String[] tags = new String[] {"TagA", "TagB", "TagC", "TagD", "TagE"};
            for (int i = 0; i < 100; i++) {
                // sharding key
                int orderId = i % 10;
                // 创建消息
                Message msg = new Message("TopicTest", tags[i % tags.length], "KEY" + i,
                        ("Hello RocketMQ Order Message" + i).getBytes(RemotingHelper.DEFAULT_CHARSET));
                System.out.println("msg: " + new String(msg.getBody()));
                // 发送消息
                SendResult sendResult = producer.send(msg, new MessageQueueSelector() {
                    @Override
                    public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
                        Integer shardingKey = (Integer) arg;
                        int index = shardingKey % mqs.size();
                        return mqs.get(index);
                    }
                }, orderId);
                System.out.printf("%s%n", sendResult);
            }
            // 关闭生产者
            producer.shutdown();
        } catch (MQClientException| RemotingException | MQBrokerException | InterruptedException e) {
            e.printStackTrace();
        }

    }
}
