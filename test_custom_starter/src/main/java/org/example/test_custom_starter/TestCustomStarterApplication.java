package org.example.test_custom_starter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.Servlet;

@SpringBootApplication
public class TestCustomStarterApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestCustomStarterApplication.class, args);
    }

    /**
     * springboot如何添加一个Servlet
     * @return
     */
    @Bean
    public ServletRegistrationBean myServlet() {
        // 声明一个servlet注册器Bean
        ServletRegistrationBean<Servlet> servletRegistrationBean = new ServletRegistrationBean<>();
        // 设置相应的servlet
        servletRegistrationBean.setServlet(new MyServlet());
        // 设置名字
        servletRegistrationBean.setName("myServlet");
        // 添加映射规则
        servletRegistrationBean.addUrlMappings("/hello");
        return servletRegistrationBean;
    }
}
