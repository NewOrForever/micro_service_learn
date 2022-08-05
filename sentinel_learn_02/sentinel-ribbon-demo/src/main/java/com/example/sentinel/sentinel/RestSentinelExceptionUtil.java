package com.example.sentinel.sentinel;

import com.alibaba.cloud.sentinel.rest.SentinelClientHttpResponse;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.example.common.utils.R;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;

/**
 * ClassName:GlobalExceptionUtil
 * Package:com.example.sentinel.sentinel
 * Description: RestTemplate 整合 sentinel，异常处理
 * 注意：方法要static且参数和返回值不能错啊，具体可以看下SentinelProtectInterceptor的源码
 *
 * @Date:2022/7/27 9:26
 * @Author:qs@1.com
 */
public class RestSentinelExceptionUtil {
    public static ClientHttpResponse handlerBlockException(HttpRequest request, byte[] body,
            ClientHttpRequestExecution execution, BlockException ex) {
        R r = R.error(-1, "========> 被限流啦");
        try {
            return new SentinelClientHttpResponse(new ObjectMapper().writeValueAsString(r));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static ClientHttpResponse fallback(HttpRequest request, byte[] body,
            ClientHttpRequestExecution execution, BlockException ex) {
        R r = R.error(-1, "========> 被熔断降级啦");
        try {
            return new SentinelClientHttpResponse(new ObjectMapper().writeValueAsString(r));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return null;
    }
}
