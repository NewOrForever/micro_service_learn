package com.example.sentinel.controller;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.Sph;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.Tracer;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * ClassName:HelloController
 * Package:com.example.sentinel.controller
 * Description:
 *
 * @Date:2022/7/19 16:42
 * @Author:qs@1.com
 */
@RestController
@RequestMapping("/sentinel/hello")
public class HelloController {
    private static final Logger logger = LoggerFactory.getLogger(HelloController.class);
    private static final String RESOURCE_NAME = "hello";

    @RequestMapping("/demo")
    public String demo() {
        Entry entry = null;
        try {
            // 资源名可使用任意有业务语义的字符串，比如方法名、接口名或其它可唯一标识的字符串
            entry = SphU.entry(RESOURCE_NAME);
            // 被保护的业务逻辑 - 实际就是给业务逻辑做个before的切面
            String str = "hello world";
            logger.info("----------------> {}", str);

            return str;
        } catch (BlockException e) {
            // 资源访问阻止，被限流或被降级  Sentinel定义异常  流控规则，降级规则，热点参数规则。。。。  服务降级(降级规则)
            // 进行相应的处理操作
            logger.info("block!");
            return "被流控啦！";
        } catch (Exception ex) {
            // 若需要配置降级规则，需要通过这种方式记录业务异常  RuntimeException  服务降级  mock  feign:fallback
            Tracer.traceEntry(ex, entry);
        } finally {
            if (entry != null) {
                entry.exit();
            }
        }

        return null;
    }

    // 设置流控规则
    @PostConstruct
    private void initFlowRules() {
        List<FlowRule> rules = new ArrayList<>();
        FlowRule flowRule = new FlowRule();
        // 设置受保护的资源
        flowRule.setResource(RESOURCE_NAME);
        // 设置流控规则 QPS
        flowRule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        // 设置受保护资源的阈值
        flowRule.setCount(1);

        rules.add(flowRule);
        // 加载配置好的规则
        FlowRuleManager.loadRules(rules);
    }

}
