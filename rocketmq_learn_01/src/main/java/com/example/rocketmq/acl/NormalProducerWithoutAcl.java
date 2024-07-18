package com.example.rocketmq.acl;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;

/**
 * ClassName:NormalProducer
 * Package:com.example.rocketmq.acl
 * Description: 测试broker 开启 ACL 的情况下，如果生产者客户端没有设置 ACL 的异常信息
 *
 * @Date:2024/7/10 16:55
 * @Author:qs@1.com
 */
public class NormalProducerWithoutAcl {

    public static void main(String[] args) throws MQClientException {
        DefaultMQProducer producer = new DefaultMQProducer("AclProducerGroup");
        producer.setNamesrvAddr("192.168.50.65:9876");
        producer.start();

        for (int i = 0; i < 128; i++)
            try {
                {
                    Message msg = new Message("AclTopicTest",
                            "TagA",
                            "OrderID188",
                            "Hello world acl msg".getBytes(RemotingHelper.DEFAULT_CHARSET));
                    SendResult sendResult = producer.send(msg);
                    System.out.printf("%s%n", sendResult);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        producer.shutdown();
    }
}
