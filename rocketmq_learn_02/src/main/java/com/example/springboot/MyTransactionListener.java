package com.example.springboot;

import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.autoconfigure.RocketMQTransactionConfiguration;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.apache.rocketmq.spring.support.RocketMQUtil;
import org.springframework.messaging.Message;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * ClassName:MyTransationListener
 * Package:com.example.springboot
 * Description: 事务监听器
 *
 * @Date:2024/7/23 9:49
 * @Author:qs@1.com
 * @see RocketMQTransactionConfiguration#registerTransactionListener(String, Object)
 * 事务监听器：
 * - 实现 {@link RocketMQLocalTransactionListener}接口
 * - 有 {@link RocketMQTransactionListener} 注解
 * - bean
 * 一个 RocketMQTemplate 只能关联一个事务监听器，如果多个事务监听器都指定了同一个 RocketMQTemplate 则会抛出异常
 */
@Component
@RocketMQTransactionListener // 使用默认的 beanName 为 rocketMQTemplate 的 RocketMQTemplate
public class MyTransactionListener implements RocketMQLocalTransactionListener {
    private ConcurrentHashMap<String, String> localTrans = new ConcurrentHashMap<>();

    /**
     * 这里执行本地事务和事务回查方法中的 Message 是 spring 的 {@link Message} 对象
     * 不是 rocketmq 的 {@link org.apache.rocketmq.common.message.Message} 对象
     *
     * @see RocketMQTransactionConfiguration#registerTransactionListener(String, Object)
     * @see RocketMQUtil#convert(RocketMQLocalTransactionListener)
     * 进入 {@link RocketMQLocalTransactionListener} 的消息对象会先由
     * rocketmq 的 {@link org.apache.rocketmq.common.message.Message} 转成 spring 的 {@link Message}
     * 消息转换方法 {@link RocketMQUtil#convertToSpringMessage(org.apache.rocketmq.common.message.Message)}
     */
    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message msg, Object arg) {
        String topic = msg.getHeaders().get(RocketMQUtil.toRocketHeaderKey(RocketMQHeaders.TOPIC), String.class);
        String tags = msg.getHeaders().get(RocketMQUtil.toRocketHeaderKey(RocketMQHeaders.TAGS), String.class);
        String destination = topic + ":" + tags;
        String transactionId = msg.getHeaders().get(RocketMQUtil.toRocketHeaderKey(RocketMQHeaders.TRANSACTION_ID), String.class);
        localTrans.put(transactionId, destination);
        // 将 Message 转成 rocketmq 的 Message 对象
        /*org.apache.rocketmq.common.message.Message message =
                RocketMQUtil.convertToRocketMessage(new StringMessageConverter(), "UTF-8", destination, msg);*/
        if (StringUtils.contains(tags, "TagA")) {
            return RocketMQLocalTransactionState.COMMIT;
        } else if (StringUtils.contains(tags, "TagB")) {
            return RocketMQLocalTransactionState.ROLLBACK;
        } else {
            return RocketMQLocalTransactionState.UNKNOWN;
        }
    }

    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message msg) {
        // 打印事务回查的消息
        System.out.printf("回查消息：%s%n", msg);
        return RocketMQLocalTransactionState.COMMIT;
    }

}
