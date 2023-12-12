package com.example.sentinel.sentinel;

import com.alibaba.cloud.sentinel.SentinelWebAutoConfiguration;
import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
import com.alibaba.csp.sentinel.slots.system.SystemBlockException;
import com.example.common.utils.R;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;

/**
 * ClassName:MyBlockExceptionHandler
 * Package:com.example.sentinel.sentinel
 * Description: BlockExeptionhandler在springmvc的preHandler中使用
 * @see SentinelWebAutoConfiguration#sentinelWebMvcConfig()
 *
 * @Date:2022/7/21 10:48
 * @Author:qs@1.com
 */
@Component
public class MyBlockExceptionHandler implements BlockExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(MyBlockExceptionHandler.class);

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, BlockException e) throws Exception {
        logger.info("BlockExceptionHandler BlockException================> {}", e.getRule());

        R r = null;

        if (e instanceof FlowException) {
            r = R.error(100, "接口限流了");
        } else if (e instanceof DegradeException) {
            r = R.error(101, "服务降级了");
        } else if (e instanceof ParamFlowException) {
            r = R.error(102, "热点参数限流了");
        } else if (e instanceof SystemBlockException) {
            r = R.error(103, "触发系统保护规则了");
        } else if (e instanceof AuthorityException) {
            r = R.error(104, "授权规则不通过");
        }

        // 返回json数据
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.setCharacterEncoding("utf-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getWriter(), r);
    }
}
