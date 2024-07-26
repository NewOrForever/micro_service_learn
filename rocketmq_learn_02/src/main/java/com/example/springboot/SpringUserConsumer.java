package com.example.springboot;

import com.example.springboot.domain.User;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.annotation.SelectorType;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.support.DefaultRocketMQListenerContainer;
import org.springframework.stereotype.Component;

/**
 * ClassName:SpringConsumer
 * Package:com.example.springboot
 * Description: 消费者
 *
 * @Date:2024/7/23 15:36
 * @Author:qs@1.com
 */
@Component
@RocketMQMessageListener(topic = "SpringBootTopicTest", consumerGroup = "SpringBootUserConsumerGroup",
        selectorType = SelectorType.TAG, selectorExpression = "TagUser", consumeMode = ConsumeMode.CONCURRENTLY,
        messageModel = MessageModel.CLUSTERING)
public class SpringUserConsumer implements RocketMQListener<User> {

    /**
     * @param message 传进来的消息 {@link DefaultRocketMQListenerContainer#doConvertMessage(MessageExt)}
     * - 如果定义的泛型是 {@link MessageExt} 则传进来的就是 {@link MessageExt} 对象
     * - 如果定义的泛型是 {@link Message} 则传进来的就是 {@link Message} 对象
     * - 如果定义的泛型不是 {@link MessageExt} 或 {@link Message} 则传进来的就是消息体内容
     *    - 如果定义的泛型是 {@link String} 则传进来的就是消息体字符串内容
     *    - 否则传进来的就是消息体就是将body 转成对应泛型的对象
     */
    /*@Override
    public void onMessage(String message) {
        System.out.printf("消费者接收到消息体内容：%s%n", message);
    }*/

    @Override
    public void onMessage(User message) {
        System.out.printf("消费者接收到user内容：%s%n", message);
    }

}
