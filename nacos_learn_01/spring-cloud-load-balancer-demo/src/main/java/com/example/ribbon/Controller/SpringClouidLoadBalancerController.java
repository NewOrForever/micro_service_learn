package com.example.ribbon.Controller;

import com.example.common.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

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
@RequestMapping("/cloud")
public class SpringClouidLoadBalancerController {

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/findOrderByUserId/{user_id}")
    public R findOrderByUserId(@PathVariable("user_id") Integer userId) {
        return restTemplate.getForObject(String.format("http://mall-order/order/findOrderByUserId/%s", userId), R.class);
    }

    @Autowired
    private WebClient webClient;

    @GetMapping("/findOrderByUserIdWithWebClient/{user_id}")
    public Mono<R> findOrderByUserIdWithWebClient(@PathVariable("user_id") Integer userId) {
        String url = String.format("http://mall-order/order/findOrderByUserId/%s", userId);
        // 基于WebClient
        Mono<R> result = webClient.get().uri(url).retrieve().bodyToMono(R.class);

        return result;
    }

    @Autowired
    private ReactorLoadBalancerExchangeFilterFunction lbFunction;

    @RequestMapping(value = "/findOrderByUserIdWithWebFlux/{user_id}")
    public Mono<R> findOrderByUserIdWithWebFlux(@PathVariable Integer user_id) {
        String url = String.format("http://mall-order/order/findOrderByUserId/%s", user_id);
        // 基于WebClient+WebFlux
        Mono<R> result = WebClient.builder()
                .filter(lbFunction)
                .build()
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(R.class);

        return result;
    }

}
