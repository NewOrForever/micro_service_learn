package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;

@SpringBootApplication
/**
 * 开启单点登录
 * 建议放在启动类上，因为
 */
@EnableOAuth2Sso
public class SpringSecurityLearn03Application {

    public static void main(String[] args) {
        SpringApplication.run(SpringSecurityLearn03Application.class, args);
    }

}
