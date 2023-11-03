package com.example.demo.handler;

import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * ClassName:MyExpiredSessionStrategy
 * Package:com.example.demo.handler
 * Description:
 *
 * @Date:2023/10/7 17:04
 * @Author:qs@1.com
 */
public class MyExpiredSessionStrategy implements SessionInformationExpiredStrategy {
    @Override
    public void onExpiredSessionDetected(SessionInformationExpiredEvent event) throws IOException, ServletException {
        System.out.println(">>>>>>>>>>会话次数超限");
        HttpServletResponse response = event.getResponse();
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write("并发登录，您已被挤下线！");
    }
}
