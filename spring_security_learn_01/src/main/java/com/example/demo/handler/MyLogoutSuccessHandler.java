package com.example.demo.handler;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * ClassName:MyLogoutSuccessHandler
 * Package:com.example.demo.handler
 * Description:
 *
 * @Date:2023/10/9 14:11
 * @Author:qs@1.com
 */
public class MyLogoutSuccessHandler implements LogoutSuccessHandler {
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // 退出成功后，跳转到登录页面
        System.out.println(">>>>>>>>>>>>退出成功，跳转回登录页面<<<<<<<<<<<<<<");
        response.sendRedirect("/login.html?customLogout");
    }
}
