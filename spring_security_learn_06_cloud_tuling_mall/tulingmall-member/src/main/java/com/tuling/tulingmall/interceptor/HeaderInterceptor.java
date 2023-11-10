package com.tuling.tulingmall.interceptor;

import com.tuling.tulingmall.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class HeaderInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        /**
         *   .header("username", claims.get("user_name", String.class))
         *                 .header("memberId", memberId)
         *                 .header("nickName", nickName)
         */
        SecurityContextHolder.set("username", request.getHeader("username"));
        SecurityContextHolder.set("memberId", request.getHeader("memberId"));
        SecurityContextHolder.set("nickName", request.getHeader("nickName"));
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        SecurityContextHolder.remove();
    }
}
