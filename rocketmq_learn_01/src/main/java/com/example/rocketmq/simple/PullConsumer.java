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
package com.example.rocketmq.simple;

import org.apache.rocketmq.client.consumer.DefaultMQPullConsumer;
import org.apache.rocketmq.client.consumer.PullResult;
import org.apache.rocketmq.client.consumer.store.ReadOffsetType;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

@SuppressWarnings("deprecation")
public class PullConsumer {

    public static void main(String[] args) throws MQClientException {

        DefaultMQPullConsumer consumer = new DefaultMQPullConsumer("PUllConsumerGroup");
        consumer.setNamesrvAddr("192.168.50.65:9876");
        Set<String> topics = new HashSet<>();
        // You would better to register topics,It will use in rebalance when starting
        // 这个topic 如果不注册的话，该topic和消费者组之间就没有订阅关系
        // dashboard 的 topic 中不显示该消费者组
        topics.add("TopicTest");
        consumer.setRegisterTopics(topics);
        consumer.start();

        ExecutorService executors = Executors.newFixedThreadPool(topics.size(), new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "PullConsumerThread");
            }
        });
        for (String topic : consumer.getRegisterTopics()) {

            executors.execute(new Runnable() {

                public void doSomething(List<MessageExt> msgs) {
                    // do your business
                    for (MessageExt msg : msgs) {
                        System.out.printf("%s Receive New Messages: %s %n", Thread.currentThread().getName(), new String(msg.getBody()));
                    }
                }

                @Override
                public void run() {
                    while (true) {
                        try {
                            consumer.fetchSubscribeMessageQueues(topic);
                            Set<MessageQueue> messageQueues = consumer.fetchMessageQueuesInBalance(topic);
                            if (messageQueues == null || messageQueues.isEmpty()) {
                                Thread.sleep(1000);
                                continue;
                            }
                            PullResult pullResult = null;
                            for (MessageQueue messageQueue : messageQueues) {
                                try {
                                    long offset = this.consumeFromOffset(messageQueue);
                                    // 消费者是从消息队列中拉取消息
                                    pullResult = consumer.pull(messageQueue, "*", offset, 32);
                                    switch (pullResult.getPullStatus()) {
                                        case FOUND:
                                            List<MessageExt> msgs = pullResult.getMsgFoundList();

                                            if (msgs != null && !msgs.isEmpty()) {
                                                this.doSomething(msgs);
                                                // update offset to broker
                                                // TODO 位点更新失败会不会导致消息重复消费？如何保证位点更新成功？
                                                // RocketMq 必然会有消息重复消费的问题，因为消费者组提交消费位点是异步的
                                                // 通常情况下消费位点正常更新不会有重复消费的问题，但是如果消费者宕机或者新的消费者加入消费组，就会触发重平衡
                                                // 重平衡可能会导致消费者分配到新的消息队列，如果之前的消费者还没有提交消费位点，那么新的消费者就会重复消费这些少量的消息
                                                consumer.updateConsumeOffset(messageQueue, pullResult.getNextBeginOffset());
                                                // print pull tps
                                                this.incPullTPS(topic, pullResult.getMsgFoundList().size());
                                                // 拉模式下针对批量消费，如果某条消息消费失败，也只能重试整个批次消息，不能只重试某条消息不然会导致消费位点不一致
                                            }
                                            break;
                                        case OFFSET_ILLEGAL:
                                            consumer.updateConsumeOffset(messageQueue, pullResult.getNextBeginOffset());
                                            break;
                                        case NO_NEW_MSG:
                                            Thread.sleep(1);
                                            consumer.updateConsumeOffset(messageQueue, pullResult.getNextBeginOffset());
                                            break;
                                        case NO_MATCHED_MSG:
                                            consumer.updateConsumeOffset(messageQueue, pullResult.getNextBeginOffset());
                                            break;
                                        default:
                                    }
                                } catch (RemotingException e) {
                                    e.printStackTrace();
                                } catch (MQBrokerException e) {
                                    e.printStackTrace();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (MQClientException e) {
                            // reblance error
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                public long consumeFromOffset(MessageQueue messageQueue) throws MQClientException {
                    //-1 when started
                    long offset = consumer.getOffsetStore().readOffset(messageQueue, ReadOffsetType.READ_FROM_MEMORY);
                    if (offset < 0) {
                        //query from broker
                        offset = consumer.getOffsetStore().readOffset(messageQueue, ReadOffsetType.READ_FROM_STORE);
                    }
                    if (offset < 0) {
                        //first time start from last offset
                        offset = consumer.maxOffset(messageQueue);
                    }
                    //make sure
                    if (offset < 0) {
                        offset = 0;
                    }
                    return offset;
                }

                public void incPullTPS(String topic, int pullSize) {
                    consumer.getDefaultMQPullConsumerImpl().getRebalanceImpl().getmQClientFactory()
                            .getConsumerStatsManager().incPullTPS(consumer.getConsumerGroup(), topic, pullSize);
                }
            });

        }
//        executors.shutdown();
//        consumer.shutdown();
    }
}
