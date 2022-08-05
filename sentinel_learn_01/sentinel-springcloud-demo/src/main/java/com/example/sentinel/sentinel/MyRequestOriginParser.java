package com.example.sentinel.sentinel;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.RequestOriginParser;
import com.alibaba.csp.sentinel.util.StringUtil;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * ClassName:MyRequestOriginParser
 * Package:com.example.sentinel.sentinel
 * Description: 解决授权规则不生效的问题
 *
 * RequestOriginParser这个包不要选错啊，选springmvc的
 * 不要选那个servlet的那个包 - 这个包要配合CommonFilter使用
 *
 * @Date:2022/7/26 10:37
 * @Author:qs@1.com
 */
@Component
public class MyRequestOriginParser implements RequestOriginParser {
    @Override
    public String parseOrigin(HttpServletRequest request) {
        // 标识字段名称可以自定义
        String origin = request.getParameter("serviceName");

        if (StringUtil.isBlank(origin)) {
            throw new IllegalArgumentException("serviceName参数未指定");
        }

        return origin;
    }
}
