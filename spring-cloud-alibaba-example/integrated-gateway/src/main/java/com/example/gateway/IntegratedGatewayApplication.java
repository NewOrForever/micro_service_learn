package com.example.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class IntegratedGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(IntegratedGatewayApplication.class, args);
        // 打印 spring.datasource.url 环境变量

    }

}
