package com.example.demo.config;

import com.example.demo.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerSecurityConfiguration;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;

/**
 * ClassName:AuthorizationServerConfigurerAdapter
 * Package:com.example.demo.config
 * Description:
 *
 * @Date:2023/10/21 15:10
 * @Author:qs@1.com
 */
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfigSso extends AuthorizationServerConfigurerAdapter {
    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        /**
         * @see AuthorizationServerSecurityConfiguration#configure(ClientDetailsServiceConfigurer)
         *
         * 配置客户端
         * 1. 授权码模式 - 常用
         *  http://localhost:8080/oauth/authorize?response_type=code&client_id=client&redirect_uri=http://www.baidu.com&scope=all
         *  http://localhost:8080/oauth/authorize?response_type=code&client_id=client
         * 2. 简化模式
         * http://localhost:8080/oauth/authorize?response_type=token&client_id=client&redirect_uri=http://www.baidu.com&scope=all
         * 3. 密码模式
         * http://localhost:8080/oauth/token?grant_type=password&username=fox&password=123123&client_id=client&client_secret=123123
         * 4. 客户端模式
         * http://localhost:8080/oauth/token?grant_type=client_credentials&client_id=client&client_secret=123123&scope=all
         * 5. 刷新token
         * http://localhost:8080/oauth/token?grant_type=refresh_token&refresh_token=xxx&client_id=client&client_secret=123123
         */
        clients.inMemory()
                // client_id
                .withClient("client")
                // client_secret
                .secret(passwordEncoder.encode("123123"))
                // token有效时间
                .accessTokenValiditySeconds(3600)
                // 刷新token有效时间
                .refreshTokenValiditySeconds(864000)
                // 重定向地址，用于授权成功后跳转
//                .redirectUris("http://www.baidu.com")
                .redirectUris("http://localhost:8083/login", "http://localhost:8084/login")
                /**
                 * 自动授权配置
                 * true：如果用户已授权，则自动跳过授权页面，直接同意授权
                 * false：如果用户已授权，则会跳转到授权页面
                 */
                .autoApprove(true)
                // 授权范围
                .scopes("all")
                /**
                 * 授权模式
                 * authorization_code：授权码模式
                 * implicit：隐式授权模式
                 * password：密码模式
                 * client_credentials：客户端模式
                 * refresh_token：更新令牌
                 */
                .authorizedGrantTypes("authorization_code");

    }

    @Autowired
    private AuthenticationManager authenticationManagerBean;
    @Autowired
    private UserServiceImpl userServiceImpl;
    @Autowired
    private TokenStore tokenStore;

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        /**
         * @see org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerEndpointsConfiguration#init()
         */
        endpoints
                .authenticationManager(authenticationManagerBean) // 密码模式需要配置
                .reuseRefreshTokens(false) // 刷新token, 每次刷新token都会重新生成一个refresh_token
                .userDetailsService(userServiceImpl) // 刷新token需要配置
                .tokenStore(tokenStore) // token存储方式 - redis
                .allowedTokenEndpointRequestMethods(HttpMethod.GET, HttpMethod.POST);
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        /**
         * @see AuthorizationServerSecurityConfiguration#configure(AuthorizationServerSecurityConfigurer)
         */
        security.allowFormAuthenticationForClients(); // 允许表单认证
        /**
         * @see AuthorizationServerSecurityConfiguration#configure(org.springframework.security.config.annotation.web.builders.HttpSecurity)
         * @see AuthorizationServerSecurityConfigurer#getCheckTokenAccess() 默认 /oauth/check_token 接口是 denyAll() 的
         * 单点登录时需要校验token，所以需要开放该接口
         */
        security.checkTokenAccess("permitAll()"); // 允许所有人请求check_token接口
    }
}
