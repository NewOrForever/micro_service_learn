package com.example.ribbon;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

// 配置文件里面没有配置数据源信息，所以引入了druid和mybatis的依赖的情况下这里因为我不需要配数据库所以排除相应的自动配置类
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class,
        DruidDataSourceAutoConfigure.class})
public class SpringCloudLoadBalancerDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringCloudLoadBalancerDemoApplication.class, args);
    }

}
