package com.example.demo.config;

import com.alibaba.cloud.sentinel.gateway.scg.SentinelSCGAutoConfiguration;
import com.alibaba.csp.sentinel.adapter.gateway.common.SentinelGatewayConstants;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiDefinition;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiPathPredicateItem;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiPredicateItem;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.GatewayApiDefinitionManager;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayRuleManager;
import com.alibaba.csp.sentinel.adapter.gateway.sc.SentinelGatewayFilter;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.BlockRequestHandler;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import com.alibaba.csp.sentinel.adapter.gateway.sc.exception.SentinelGatewayBlockExceptionHandler;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.result.view.ViewResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * 这种方式配合引入的依赖是：sentinel-spring-cloud-gateway-adapter
 * 问题：
 *  - sentinel控制台没有api管理菜单
 *  - 控制台的流控规则中没有数据，但是通过调用接口测试发现 -> 流控规则是存在的
 * 解决：启动时需要加上 -Dcsp.sentinel.app.type=1 这个参数
 * 综上：这种方式通常是不会去用的 -> 注释掉adapter依赖，引入spring-cloud-alibaba-sentinel-gateway依赖，
 *      这个Configuration注释掉 -> 重启会发现控制台有了api管理菜单项了（经测试：sentinel也要重启才有这个api管理菜单）
 *      spring-cloud-alibaba-sentinel-gateway 具体源码见 {@link SentinelSCGAutoConfiguration} 和我当前的 {@link GatewayConfiguration}
 *      基本上是差不多的，网关流控的核心还是 {@link SentinelGatewayFilter} 和 {@link SentinelGatewayBlockExceptionHandler} 这两个类
 */
// @Configuration
public class GatewayConfiguration {
    private final List<ViewResolver> viewResolvers;
    private final ServerCodecConfigurer serverCodecConfigurer;

    public GatewayConfiguration(ObjectProvider<List<ViewResolver>> viewResolversProvider,
                                ServerCodecConfigurer serverCodecConfigurer) {
        this.viewResolvers = viewResolversProvider.getIfAvailable(Collections::emptyList);
        this.serverCodecConfigurer = serverCodecConfigurer;
    }

    /**
     * 限流异常处理器
     * @return
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SentinelGatewayBlockExceptionHandler sentinelGatewayBlockExceptionHandler() {
        // Register the block exception handler for Spring Cloud Gateway.
        return new SentinelGatewayBlockExceptionHandler(viewResolvers, serverCodecConfigurer);
    }

    /**
     * 限流过滤器
     * @return
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public GlobalFilter sentinelGatewayFilter() {
        return new SentinelGatewayFilter();
    }

    @PostConstruct
    public void doInit() {
        //初始化自定义的API
        initCustomizedApis();
        //初始化网关限流规则
        initGatewayRules();
        //自定义限流异常处理器
        initBlockRequestHandler();
    }

    private void initCustomizedApis() {
        Set<ApiDefinition> definitions = new HashSet<>();
        ApiDefinition api = new ApiDefinition("order_service_api")
                .setPredicateItems(new HashSet<ApiPredicateItem>() {{
                    add(new ApiPathPredicateItem().setPattern("/order/**")
                            .setMatchStrategy(SentinelGatewayConstants.URL_MATCH_STRATEGY_PREFIX));
                }});
        definitions.add(api);
        GatewayApiDefinitionManager.loadApiDefinitions(definitions);
    }

    private void initGatewayRules() {
        Set<GatewayFlowRule> rules = new HashSet<>();
        //resource：资源名称，可以是网关中的 route_id或者用户自定义的 API（ApiDefinition） 分组名称。
        //count：限流阈值
        //intervalSec：统计时间窗口，单位是秒，默认是 1 秒。
        rules.add(new GatewayFlowRule("order-service") // 配置文件中配的网关的route id
                .setCount(2)
                .setIntervalSec(1)
        );
        rules.add(new GatewayFlowRule("order_service_api") // 上面initCustomizedApis方法中配置的ApiDefinition
                .setCount(2)
                .setIntervalSec(1)
        );

        // 加载网关规则
        GatewayRuleManager.loadRules(rules);
    }

    private void initBlockRequestHandler() {
        BlockRequestHandler blockRequestHandler = new BlockRequestHandler() {
            @Override
            public Mono<ServerResponse> handleRequest(ServerWebExchange exchange, Throwable t) {
                HashMap<String, String> result = new HashMap<>();
                result.put("code",String.valueOf(HttpStatus.TOO_MANY_REQUESTS.value()));
                result.put("msg", HttpStatus.TOO_MANY_REQUESTS.getReasonPhrase());

                return ServerResponse.status(HttpStatus.TOO_MANY_REQUESTS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(result));
            }
        };
        //设置自定义异常处理器
        GatewayCallbackManager.setBlockHandler(blockRequestHandler);
    }

}
