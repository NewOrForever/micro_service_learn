package com.example.sentinel.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.example.common.utils.PageUtils;
import com.example.common.utils.R;
import com.example.sentinel.entity.UserEntity;
import com.example.sentinel.sentinel.CommonBlockHandler;
import com.example.sentinel.sentinel.CommonFallBack;
import com.example.sentinel.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/sentinel/user")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    /**********************springmvc的preHandle中处理BlockException，不使用@SentinelResource注解了************************/
    @RequestMapping("/getUserInfoById/{id}")
//    @SentinelResource(value = "userInfo",
//            blockHandlerClass = CommonBlockHandler.class, blockHandler = "handlerUserInfoException",
//            fallbackClass = CommonFallBack.class, fallback = "fallback")
    public R getUserInfoById(@PathVariable Integer id) {
        UserEntity user = userService.getById(id);
        return R.ok().put("user", user);
    }

    /**
     * 流控模式 - 关联：
     * 当两个资源之间具有资源争抢或者依赖关系的时候，这两个资源便具有了关联
     * 当关联资源达到阈值后，对当前资源进行流控
     * 测试：
     * 资源名：/sentinel/user/getUserInfoById/{id}
     * 关联资源：/sentinel/user/info/{id}
     * qps：阈值2
     * 1. jemeter：3s跑50个线程请求/sentinel/user/getUserInfoById/{id} - 先开
     * 再去请求关联资源使其达到阈值（这个阈值是当前资源 - /sentinel/user/getUserInfoById/{id} 的阈值哦），发现当
     * 关联资源达到阈值后，当前资源就会进行流控
     * 2. 在这种模式下只要关联资源没达到阈值，那么你随便请求当前资源 - 不会被流控
     * 3. 关联资源我这里没去创建流控规则，一开始我创建了，想着当前资源qps - 10，关联资源qps - 2，当关联资源到 2，那么
     * 当前资源就被流控。实际这样理解是错误的，正确的应该是 - 当关联资源达到当前资源的阈值，当前资源会被流控。
     * 4. 那么如果我给关联资源设置了流控规则，那么关联资源流控规则的阈值要大于当前资源的阈值，否则测试不出东西来。
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable Integer id) {
        UserEntity user = userService.getById(id);
        return R.ok().put("user", user);
    }


    /*************************链路模式的测试************************/
    /**
     * 链路不生效问题处理不建议使用CommonFilter -> 会引入更多的问题
     * 我是再配置文件设置 web-context-unify: false + 使用@SentinelResource 这样好像没那么多问题
     */
    @RequestMapping("/test01")
    public R test01() {
        UserEntity user = userService.getUserById(2);
        return R.ok().put("user", user);
    }

    @RequestMapping("/test02")
    public R test02() {
        UserEntity user = userService.getUserById(2);
        return R.ok().put("user", user);
    }


    /**********************流控效果的测试************************/
    // warm up
    @RequestMapping("/test03")
    public String test03() {

        try {
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return "-----------test03---->warm up";
    }

    // 排队等待（匀速排队 - 漏桶算法）
    @RequestMapping("/test04")
    public String test04() {
        try {
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "-----------test04---->排队等待";
    }

    /************************降级规则的测试**************************/
    // 慢调用比例 - RT: 101ms
    @RequestMapping("/test05")
    public String test05() {
        try {
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "-----------test05---->降级规则";
    }

    AtomicInteger count = new AtomicInteger();
    // 异常比例、异常数
    @RequestMapping("/test06")
    public String test06() {
        count.getAndIncrement();
        System.out.println(count.get());
        if (count.get() % 2 == 0) {
            // 模拟异常
            int i = 1 / 0;
        }
        return "------------test06---------->降级规则";
    }

    /*********************热点参数限流************************/
    @RequestMapping("/userInfo/{id}")
    @SentinelResource(value = "userInfo",
    blockHandlerClass = CommonBlockHandler.class, blockHandler = "handlerUserInfoException",
    fallbackClass = CommonFallBack.class, fallback = "fallback")
    public R userInfo(@PathVariable Integer id) {
        UserEntity user = userService.getById(id);
        if (id == 4) {
            throw new IllegalArgumentException(">>>>>>非法参数");
        }
        return R.ok().put("user", user);
    }

    /************************系统规则测试************************/
    @RequestMapping("/sysRule")
    public String sysRule() {
        return "-----------sysRule------>系统规则测试";
    }

    /************************授权规则测试***************************/
    @RequestMapping("/authorityRule")
    public String authorityRule() {
        return "-----------authorityRule------>授权规则测试";
    }

    /*************************集群流控****************************/
    /**
     * sentinel的集群流控有些鸡肋，可以通过整合ribbon（RestTemplate）、feign、dubbo的方式来替代
     * @see sentinel_learn_02
     */
}
