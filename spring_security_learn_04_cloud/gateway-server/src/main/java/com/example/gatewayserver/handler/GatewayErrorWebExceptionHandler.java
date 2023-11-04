package com.example.gatewayserver.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.reactive.error.DefaultErrorWebExceptionHandler;
import org.springframework.boot.autoconfigure.web.reactive.error.ErrorWebFluxAutoConfiguration;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.io.Serializable;

/**
 * ClassName:GatewayErrorWebExceptionHandler
 * Package:com.example.gatewayserver.handler
 * Description: 自定义网关异常处理
 *
 * @Date:2023/11/3 14:21
 * @Author:qs@1.com
 * @see ErrorWebFluxAutoConfiguration 默认 {@link DefaultErrorWebExceptionHandler}
 */
@Slf4j
@Order(-1)
@Component
public class GatewayErrorWebExceptionHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 处理给定的异常
     *
     * @param exchange
     * @param ex
     * @return
     */
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();

        if (response.isCommitted()) {
            return Mono.error(ex);
        }

        // header set_json响应
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        // 是否响应状态异常
        if (ex instanceof ResponseStatusException) {
            response.setStatusCode(((ResponseStatusException) ex).getStatus());
        }

        return response
                .writeWith(Mono.fromSupplier(() -> {
                    DataBufferFactory bufferFactory = response.bufferFactory();
                    try {
                        log.error("网关异常", ex);
                        // 返回json异常原因给前端
                        return bufferFactory.wrap(objectMapper.writeValueAsBytes(R.fail(ex.getMessage())));
                    } catch (JsonProcessingException e) {
                        log.warn("Error writing response", ex);
                        return bufferFactory.wrap(new byte[0]);
                    }
                }));
    }

    public static class R<T> implements Serializable
    {
        private static final long serialVersionUID = 1L;

        /** 成功 */
        public static final int SUCCESS = 200;

        /** 失败 */
        public static final int FAIL = 500;

        private int code;

        private String msg;

        private T data;

        public static <T> R<T> ok()
        {
            return restResult(null, SUCCESS, "操作成功");
        }

        public static <T> R<T> ok(T data)
        {
            return restResult(data, SUCCESS, "操作成功");
        }

        public static <T> R<T> ok(T data, String msg)
        {
            return restResult(data, SUCCESS, msg);
        }

        public static <T> R<T> fail()
        {
            return restResult(null, FAIL, "操作失败");
        }

        public static <T> R<T> fail(String msg)
        {
            return restResult(null, FAIL, msg);
        }

        public static <T> R<T> fail(T data)
        {
            return restResult(data, FAIL, "操作失败");
        }

        public static <T> R<T> fail(T data, String msg)
        {
            return restResult(data, FAIL, msg);
        }

        public static <T> R<T> fail(int code, String msg)
        {
            return restResult(null, code, msg);
        }

        private static <T> R<T> restResult(T data, int code, String msg)
        {
            R<T> apiResult = new R<>();
            apiResult.setCode(code);
            apiResult.setData(data);
            apiResult.setMsg(msg);
            return apiResult;
        }

        public int getCode()
        {
            return code;
        }

        public void setCode(int code)
        {
            this.code = code;
        }

        public String getMsg()
        {
            return msg;
        }

        public void setMsg(String msg)
        {
            this.msg = msg;
        }

        public T getData()
        {
            return data;
        }

        public void setData(T data)
        {
            this.data = data;
        }

        public static <T> Boolean isError(R<T> ret)
        {
            return !isSuccess(ret);
        }

        public static <T> Boolean isSuccess(R<T> ret)
        {
            return R.SUCCESS == ret.getCode();
        }
    }
}
