//package com.example.sentinel.sentinel;
//
//import com.alibaba.csp.sentinel.adapter.servlet.callback.UrlBlockHandler;
//import com.alibaba.csp.sentinel.slots.block.BlockException;
//import com.alibaba.csp.sentinel.slots.block.authority.AuthorityException;
//import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
//import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
//import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
//import com.alibaba.csp.sentinel.slots.system.SystemBlockException;
//import com.example.common.utils.R;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.http.MediaType;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
///**
// * ClassName:MyUrlBlockedHandler
// * Package:com.example.sentinel.sentinel
// * Description: 自定义一个UrlBlockHandler 添加到CommonFilter来测试是否能拦截到 BlockException
// *
// * @Date:2022/7/25 11:12
// * @Author:qs@1.com
// */
//public class MyUrlBlockHandler implements UrlBlockHandler {
//    private static final Logger logger = LoggerFactory.getLogger(MyBlockExceptionHandler.class);
//
//    @Override
//    public void blocked(HttpServletRequest request, HttpServletResponse response, BlockException ex) throws IOException {
//        logger.info("UrlBlockHandler BlockException================> {}", ex.getRule());
//
//        R r = null;
//
//        if (ex instanceof FlowException) {
//            r = R.error(100, "接口限流了");
//        } else if (ex instanceof DegradeException) {
//            r = R.error(101, "服务降级了");
//        } else if (ex instanceof ParamFlowException) {
//            r = R.error(102, "热点参数限流了");
//        } else if (ex instanceof SystemBlockException) {
//            r = R.error(103, "触发系统保护规则了");
//        } else if (ex instanceof AuthorityException) {
//            r = R.error(104, "授权规则不通过");
//        }
//
//        // 返回json数据
//        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//        response.setCharacterEncoding("utf-8");
//        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//        new ObjectMapper().writeValue(response.getWriter(), r);
//    }
//}
