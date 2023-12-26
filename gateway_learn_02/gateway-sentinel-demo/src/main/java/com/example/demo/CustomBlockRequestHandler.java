package com.example.demo;

import com.alibaba.csp.sentinel.EntryType;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.BlockRequestHandler;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import com.alibaba.csp.sentinel.adapter.gateway.sc.exception.SentinelGatewayBlockExceptionHandler;
import com.alibaba.csp.sentinel.adapter.reactor.SentinelReactorSubscriber;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
import com.alibaba.csp.sentinel.slots.system.SystemBlockException;
import com.alibaba.csp.sentinel.util.function.Supplier;
import com.example.demo.config.GatewayConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.reactivestreams.Subscription;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.handler.ExceptionHandlingWebHandler;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.HashMap;

/**
 * ClassName:CustomBlockRequestHandler
 * Package:com.example.demo
 * Description: 自定义限流异常处理
 * @see GatewayConfiguration#initBlockRequestHandler() 参考这个类的实现
 *
 * @see SentinelReactorSubscriber#hookOnSubscribe(Subscription) ->
 * @see SentinelReactorSubscriber#doWithContextOrCurrent(Supplier, Runnable) -> f.run() ->
 * 网关sentinel 的入口 {@link com.alibaba.csp.sentinel.adapter.reactor.SentinelReactorSubscriber#entryWhenSubscribed()} ->
 * 网关sentinel 的入口 {@link SphU#asyncEntry(String, int, EntryType, int, Object[])} ->
 * 捕获 {@link SphU#asyncEntry(String, int, EntryType, int, Object[])} 异常 -> actual.onError(ex) 进入异常处理 ->
 * @see ExceptionHandlingWebHandler#handle(ServerWebExchange) ->
 * @see SentinelGatewayBlockExceptionHandler#handle(ServerWebExchange, Throwable) ->
 * @see GatewayCallbackManager#setBlockHandler(BlockRequestHandler)
 *
 * @Date:2023/12/14 9:27
 * @Author:qs@1.com
 */
@Component
public class CustomBlockRequestHandler implements BlockRequestHandler {
    @Override
    public Mono<ServerResponse> handleRequest(ServerWebExchange exchange, Throwable t) {
        HashMap<String, String> result = new HashMap<>();
        result.put("code",String.valueOf(HttpStatus.TOO_MANY_REQUESTS.value()));
        result.put("msg", "网关限流啦 >>>>>>>>>> " + HttpStatus.TOO_MANY_REQUESTS.getReasonPhrase());

        return ServerResponse.status(HttpStatus.TOO_MANY_REQUESTS)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(result));
    }

    @PostConstruct
    public void init(){
        GatewayCallbackManager.setBlockHandler(this);
    }
}
