package com.example.ribbon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.netflix.ribbon.RibbonClients;

@SpringBootApplication
//配置多个   RibbonConfig不能被@SpringbootApplication的@CompentScan扫描到，否则就是全局配置的效果
//@RibbonClients(value = {
//    // 在SpringBoot主程序扫描的包外定义配置类
//    @RibbonClient(name = "mall-order",configuration = com.example.rule.CustomRibbonConfiguration.class),
//    @RibbonClient(name = "mall-account",configuration = com.example.rule.CustomRibbonConfiguration.class)
//})
public class RibbonDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(RibbonDemoApplication.class, args);
    }

}
