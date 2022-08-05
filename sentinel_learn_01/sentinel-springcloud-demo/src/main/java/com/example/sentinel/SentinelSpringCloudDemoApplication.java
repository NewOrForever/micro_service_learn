package com.example.sentinel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
public class SentinelSpringCloudDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SentinelSpringCloudDemoApplication.class, args);
    }

}
