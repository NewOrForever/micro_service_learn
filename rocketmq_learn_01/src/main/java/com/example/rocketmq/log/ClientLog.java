package com.example.rocketmq.log;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.log.ClientLogger;
import org.apache.rocketmq.logging.InnerLoggerFactory;
import org.apache.rocketmq.logging.InternalLogger;
import org.apache.rocketmq.logging.inner.LoggingBuilder;

import java.util.concurrent.TimeUnit;

/**
 * ClassName:ClientLog
 * Package:com.example.rocketmq.log
 * Description: 客户端日志
 * rocketmq的客户端日志
 * @see org.apache.rocketmq.client.log.ClientLogger
 *  - rocketmq 的客户端日志主要是2种方式：使用项目自带的 slf4j 记录日志、使用rocketmq 自带的 {@link InnerLoggerFactory.InnerLogger}记录日志
 *  - 通过配置环境变量 rocketmq.client.logUseSlf4j 来控制使用哪种方式记录日志，默认是false，即使用rocketmq自带的日志记录方式
 *  - 如果要自定义自带的日志框架配置，可以参考 ClientLogger 的静态变量定义添加到环境变量中
 * @see rocketmq-logging 模块
 * @see org.apache.rocketmq.logging.InternalLogger
 *
 * 实际使用时不需要开启 logUseSlf4j 去使用项目中的 slf4j 记录日志，使用 rocketmq 自带的日志记录即可，这样可以避免日志记录混乱、排查问题困难等问题
 *
 * @Date:2024/7/18 9:46
 * @Author:qs@1.com
 */
public class ClientLog {
    public static void main(String[] args) throws MQClientException, InterruptedException {
        System.out.println(System.getProperty("user.home"));
        System.out.println(System.getProperty("rocketmq.client.logUseSlf4j", "false"));

        // testUseSlf4j();
        testUseRocketmqLog();
    }

    private static void testUseRocketmqLog() throws InterruptedException {
        // 使用rocketmq自带的日志记录
        InternalLogger log = ClientLogger.getLog();
        log.info("test log info use rocketmq log");
        /**
         * @see ClientLogger#createClientAppender()
         * @see LoggingBuilder.AppenderBuilder#withAsync(boolean, int)
         * - 默认是异步的，所以这里需要等待一段时间，否则可能在 rocketmq 日志文件中看不到日志输出
         */
        TimeUnit.SECONDS.sleep(20);
    }

    private static void testUseSlf4j() throws MQClientException {
        // 使用slf4j记录日志
        System.setProperty("rocketmq.client.logUseSlf4j", "true");

        InternalLogger log = ClientLogger.getLog();
        /**
         * 输出路径为在 logback.xml 中配置的路径
         */
        log.info("test log info");
    }


}
