package com.example.demo.controller;

import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2SsoProperties;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/user")
public class UserController {

    @RequestMapping("/getCurrentUser")
    public Object getCurrentUser(Authentication authentication) {
        return authentication;
    }
    /**
     * @see org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2SsoProperties#loginPath
     * 修改默认的路径的话，授权中心的 redirect_uri 也要修改
     *
     * @see org.springframework.boot.autoconfigure.security.oauth2.client.SsoSecurityConfigurer#oauth2SsoFilter(OAuth2SsoProperties)
     * new OAuth2ClientAuthenticationProcessingFilter(sso.getLoginPath())
     * @see OAuth2ClientAuthenticationProcessingFilter#OAuth2ClientAuthenticationProcessingFilter(String) - 设置需要去授权中心进行登录认证的url
     * @see AbstractAuthenticationProcessingFilter#AbstractAuthenticationProcessingFilter(String)
     * @see AbstractAuthenticationProcessingFilter#requiresAuthentication(HttpServletRequest, HttpServletResponse)
     *
     * 资源访问拒绝处理器 - 重定向到客户端的 loginPath -> OAuth2ClientAuthenticationProcessingFilter 过滤器匹配到的该 loginPath url
     * -> 与授权中心进行 OAuth2 的 Authorization Code 认证交互
     * @see SsoSecurityConfigurer#addAuthenticationEntryPoint(HttpSecurity, OAuth2SsoProperties)
     * new LoginUrlAuthenticationEntryPoint(sso.getLoginPath())
     * @see org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint#loginFormUrl 属性
     *
     */

}
