package org.example.test_custom_starter;

import com.autoconfigurer.custom.IndexController;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootVersion;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServlet;

/**
 * ClassName:TestMyStarter
 * Package:org.example.test_custom_starter
 * Description: 我自己简单的写了个测试starter，写完就删
 *
 * @Date:2022/6/10 12:56
 * @Author:qs@1.com
 */
@RestController
public class TestMyStarterController implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    // @ConditionalOnProperty("my.test.name") --> 配置文件需要有这个配置否则stater中的MyAutoConfiguration排除不会导进来
    // 那么在MyAutoConfiguration中配置的Bean也就不生效
//    @Autowired
//    private MyTestBean myTestBean;

    @RequestMapping("/test")
    public void test() {
        String[] beanNames = applicationContext.getBeanNamesForType(Servlet.class);
        for (String beanName : beanNames) {
            System.out.println(beanName);
        }

        System.out.println("===============");

        String[] ServletRegistrationBeanNames = applicationContext.getBeanNamesForType(ServletRegistrationBean.class);
        for (String beanName : ServletRegistrationBeanNames) {
            System.out.println(beanName);
        }

//        ApplicationContext parent = applicationContext.getParent();
//        if (parent != null) {
//            String[] beanNamesFromParent = parent.getBeanNamesForType(Servlet.class);
//            for (String beanName : beanNamesFromParent) {
//                System.out.println(beanName);
//            }
//        }

//        System.out.println(myTestBean);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
