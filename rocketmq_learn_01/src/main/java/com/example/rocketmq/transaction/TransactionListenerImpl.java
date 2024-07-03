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

import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class TransactionListenerImpl implements TransactionListener {
    private AtomicInteger transactionIndex = new AtomicInteger(0);

    private ConcurrentHashMap<String, Integer> localTrans = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Integer> checkTransCountMap = new ConcurrentHashMap<>();

    @Override
    public LocalTransactionState executeLocalTransaction(Message msg, Object arg) {
        int value = transactionIndex.getAndIncrement();
        if (value == 0) {
            return LocalTransactionState.ROLLBACK_MESSAGE;
        } else if (value == 1) {
            return LocalTransactionState.COMMIT_MESSAGE;
        }
        int status = value % 3;
        localTrans.put(msg.getTransactionId(), status);
        return LocalTransactionState.UNKNOW;
    }

    /**
     * @see org.apache.rocketmq.common.BrokerConfig#transactionCheckMax
     * @see org.apache.rocketmq.common.BrokerConfig#transactionTimeOut
     * @see org.apache.rocketmq.common.BrokerConfig#transactionCheckInterval
     * 事务消息回查的配置可以在broker配置文件中配置，具体配置项如上
     */
    @Override
    public LocalTransactionState checkLocalTransaction(MessageExt msg) {
        Integer status = localTrans.get(msg.getTransactionId());
        checkTransCountMap.computeIfAbsent(msg.getTransactionId(), k -> 0);
        // 每次回查时，状态+1
        checkTransCountMap.put(msg.getTransactionId(), checkTransCountMap.get(msg.getTransactionId()) + 1);

        if (null != status) {
            switch (status) {
                case 0:
                    // 状态回查时，如果返回UNKNOW，消息将会再次回查，多次回查后如果仍然返回UNKNOW，消息将被丢弃
                    System.out.printf("回查消息：%s, 消息体：%s，回查次数：%d，状态：UNKNOW%n", msg.getTransactionId(),
                            new String(msg.getBody()), checkTransCountMap.get(msg.getTransactionId()));
                    return LocalTransactionState.UNKNOW;
                case 1:
                    // 状态回查时，如果返回COMMIT_MESSAGE，消息将被提交，消费者可以消费
                    System.out.printf("回查消息：%s, 消息体：%s，状态：COMMIT_MESSAGE%n", msg.getTransactionId(), new String(msg.getBody()));
                    return LocalTransactionState.COMMIT_MESSAGE;
                case 2:
                    // 状态回查时，如果返回ROLLBACK_MESSAGE，消息将被丢弃
                    System.out.printf("回查消息：%s, 消息体：%s，状态：ROLLBACK_MESSAGE%n", msg.getTransactionId(), new String(msg.getBody()));
                    return LocalTransactionState.ROLLBACK_MESSAGE;
                default:
                    return LocalTransactionState.COMMIT_MESSAGE;
            }
        }
        return LocalTransactionState.COMMIT_MESSAGE;
    }
}
