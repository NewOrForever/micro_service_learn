package com.example.springboot;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.example.springboot.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.RequestTimeoutException;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.autoconfigure.RocketMQAutoConfiguration;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.support.MessageBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * ClassName:RocketMqApplication
 * Package:com.example.rocketmq
 * Description:
 *
 * @Date:2024/5/20 13:51
 * @Author:qs@1.com
 */
@Slf4j
@SpringBootApplication(exclude = {DruidDataSourceAutoConfigure.class, DataSourceAutoConfiguration.class})
public class RocketMqSpringBootApplication implements CommandLineRunner, ApplicationContextAware {

    private ApplicationContext applicationContext;

    public static void main(String[] args) {
        SpringApplication.run(RocketMqSpringBootApplication.class, args);
    }



    @Autowired
    private SpringProducer springProducer;
    @Autowired
    private static final String TOPIC = "SpringBootTopicTest";

    /**
     * 更多测试样例参考源码：https://github.com/apache/rocketmq-spring/blob/rocketmq-spring-all-2.2.2/rocketmq-spring-boot-samples/rocketmq-produce-demo/src/main/java/org/apache/rocketmq/samples/springboot/ProducerApplication.java
     */
    @Override
    public void run(String... args) throws Exception {
        System.out.println("RocketMqSpringBootApplication is running...");
        RocketMQTemplate rocketMQTemplate = springProducer.getRocketMQTemplate();

        // 发送事务消息
        springProducer.sendMessageInTransaction(TOPIC, "spring boot send transaction message");

        // 发送普通消息
        springProducer.syncSend(TOPIC, "spring boot send message");
        springProducer.convertAndSend(TOPIC, new User("张三", 18, "北京"));
        // 异步发送消息
        springProducer.asyncSend(TOPIC, "spring boot async send message");

        /**
         * send and receive in sync mode
         * 这个方法不管是异步还是同步老是报错：请求已发送但是响应超时，不知道为啥
          */
       /* try {
            springProducer.sendAndReceive(TOPIC, "spring boot send and receive message");
        } catch (Exception e) {
        }
        // send and receive in async mode
        springProducer.asyncSendAndReceive(TOPIC, new User("异步发送并接收", 18, "rocketmq"));*/

        springProducer.testBatchMessages(TOPIC);

        springProducer.testSendBatchMessageOrderly(TOPIC);

        /**
         * @see RocketMQAutoConfiguration#defaultLitePullConsumer
         * 验证一下：@ConditionalOnProperty(prefix = "rocketmq", value = {"name-server", "consumer.group", "consumer.topic"})
         * 配置文件中需要配置了这三个属性 rocketmq.name-server、rocketmq.consumer.group、rocketmq.consumer.topic
         * 才会创建这个 defaultLitePullConsumer bean
          */
        /*Object bean = null;
        try {
            bean = applicationContext.getBean(RocketMQAutoConfiguration.CONSUMER_BEAN_NAME);
        } catch (BeansException e) {
            e.printStackTrace();
        }
        System.out.println("defaultLitePullConsumer bean: " + bean);*/
    }



    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
