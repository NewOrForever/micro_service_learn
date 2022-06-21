package com.example.ribbon.Controller;

import com.example.common.utils.R;
import com.example.ribbon.interceptor.MyLoadBalanced;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * ClassName:RibbonController
 * Package:com.example.ribbon.Controller
 * Description:
 *
 * @Date:2022/6/17 8:35
 * @Author:qs@1.com
 */
@RestController
@RequestMapping("/ribbon")
public class RibbonController {

    @Autowired
    private RestTemplate restTemplate;


    @GetMapping("/findOrderByUserId/{user_id}")
    public R findOrderByUserId(@PathVariable("user_id") Integer userId) {
        return restTemplate.getForObject(String.format("http://mall-order/order/findOrderByUserId/%s", userId), R.class);
    }


    // 测试这个的时候需要把@LoadBalanced注解注释掉
    @GetMapping("/findOrderByUserIdForMock/{user_id}")
    public R mockRobbinExecute(@PathVariable Integer user_id) {
        // 模拟robbin的实现，提前将ip:port选出来
        String url = getUri("mall-order") + "/order/findOrderByUserId/" + user_id;
        System.out.println("调用地址：=========> " + url);

        R result = restTemplate.getForObject(url, R.class);
        return result;
    }

    @Autowired
    private DiscoveryClient discoveryClient;

    public String getUri(String serviceName) {
        List<ServiceInstance> allServers = discoveryClient.getInstances(serviceName);
        if (allServers == null || allServers.isEmpty()) {
            return null;
        }

        int serverCount = allServers.size();
        ServiceInstance choosedService = allServers.get(incrementAndGetModulo(serverCount));
        return choosedService.getUri().toString();
    }

    // RoundRobinRule里抄的：轮询算法
    // 1 -> 2 -> 0
    private AtomicInteger nextServerCyclicCounter = new AtomicInteger(0);
    private int incrementAndGetModulo(int modulo) {
        for (;;) {
            int current = nextServerCyclicCounter.get();
            int next = (current + 1) % modulo;
            if (nextServerCyclicCounter.compareAndSet(current, next))
                return next;
        }
    }


    @Bean
    //@LoadBalanced
    @MyLoadBalanced // 使用了自定义的负载均衡注解
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
