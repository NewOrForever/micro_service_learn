package com.example.demo.handler;

import org.springframework.security.access.expression.SecurityExpressionOperations;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * ClassName:MySecurityExpression
 * Package:com.example.demo.handler
 * Description:
 *
 * @Date:2023/10/12 10:06
 * @Author:qs@1.com
 */
public interface MySecurityExpressionOperations {

    boolean hasPermission(HttpServletRequest request, Authentication authentication);
}
