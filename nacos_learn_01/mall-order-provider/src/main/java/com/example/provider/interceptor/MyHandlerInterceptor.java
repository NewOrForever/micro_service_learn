package com.example.provider.interceptor;

import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * ClassName:MyInterceptor
 * Package:com.example.provider.interceptor
 * Description:
 *
 * @Date:2022/6/20 16:41
 * @Author:qs@1.com
 */
public class MyHandlerInterceptor implements HandlerInterceptor {
    /**
     * 简单的权限验证：feign-demo请求过来
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        boolean flag = true;
        String auth = request.getHeader("Authorization");
        System.out.println("==========> Authorization: "+ auth );
        if (StringUtils.isEmpty(auth)) {
            String access_token = request.getParameter("access_token");
            System.out.println("--------------> access_token: " + access_token);
            if (StringUtils.isEmpty(access_token)) {
                flag = false;
            }
        }

        return flag;
    }
}
