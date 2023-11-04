package com.example.authserver.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.builders.JdbcClientDetailsServiceBuilder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

import javax.sql.DataSource;

/**
 * @author Fox
 */
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private DataSource dataSource;
    @Autowired
    private UserDetailsService userDetailsService;


    @Autowired
    private TokenStore tokenStore;

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
//        clients.jdbc(dataSource)
//                .withClient("client_id")
//                .secret("client_secret");


        /**
         * password模式 - client_id 和 client_secret、grant_type、scope 需要预先在oauth_client_details表中设置
         * http://localhost:8888/oauth/token?username=fox&password=123456&grant_type=password&client_id=gateway-server&client_secret=123123&scope=read
         *
         * 注意：实际使用中，client_id 和 client_secret 是不会暴露给客户端的，客户端只需要传入 username、password、grant_type、scope
         * 授权中心 /oauth/token 这个接口不应该在 客户端（web、app、小程序登等）直接调用，而是在服务端调用
         * 客户端调用自己的登录接口，登录接口中调用授权中心的 /oauth/token 接口，获取token，然后返回给客户端
         *
         * 场景：
         * 1. 自家系统：直接使用 password 模式或者 client 模式
         * 客户端登录页面填写账号密码调用 /user/login -> 网关（自定义过滤器验证 token 和 直接放行 url）
         * -> 用户服务的 /user/login 接口，该接口中调用授权中心的 /oauth/token 接口获取token
         * -> token 返回给客户端 -> 客户端携带token访问资源服务器的接口 -> 网关 token 验证 -> 微服务
         *
         * 2. 三方系统：使用 authorization_code 模式
         * 授权我们的用户登录三方系统，三方系统需要调用我们的接口，我们的接口需要验证token，所以三方系统需要先获取token
         * 比如微信授权登录三方网站：
         * 三方网站的登录页面选择微信登录
         * -> 用户使用微信扫码，跳转到微信授权页面（/oauth/authorize?response_type=code&client_id=client&redirect_uri=三方系统回调地址）
         * -> 用户同意授权 -> 跳转到三方网站的回调接口 -> 三方网站的回调接口中调用微信获取 token 的接口，获取token
         * -> 三方系统拿到微信 token 就能调用微信的接口了
         *
         * 3. 需要搭配下后台管理系统使用
         * 管理客户端 client 信息 -> oauth_client_details 表 -> 这个 client_id 和 client_secret 是给三方系统使用的
         *
         */

        /**
         * 走数据库的方式的话使用 {@link ClientDetailsServiceConfigurer#withClientDetails(ClientDetailsService)} 比较好，比较灵活
         * 可以进行自定义扩展，使用 {@link ClientDetailsServiceConfigurer#jdbc(DataSource)} 的话只能使用默认的
         * 其实可以直接把 {@link JdbcClientDetailsServiceBuilder#performBuild()} 的代码复制进来，然后自己扩展
         * @see JdbcClientDetailsService  操作 oauth_client_details表，这个表是oauth2的客户端信息表
         * sql 都在类中写好了
         */
        clients.withClientDetails(clientDetails());
    }

    @Bean
    public ClientDetailsService clientDetails() {
        // 读取oauth_client_details表
        return new JdbcClientDetailsService(dataSource);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.authenticationManager(authenticationManager) //使用密码模式需要配置
                .tokenStore(tokenStore)  //指定token存储到redis
                .reuseRefreshTokens(false)  //refresh_token是否重复使用
                .userDetailsService(userDetailsService) //刷新令牌授权包含对用户信息的检查
                .allowedTokenEndpointRequestMethods(HttpMethod.GET,HttpMethod.POST); //支持GET,POST请求
    }



    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        //允许表单认证
        security.allowFormAuthenticationForClients()
        // 配置校验token需要带入clientId 和clientSeret配置
            .checkTokenAccess("isAuthenticated()");
    }
}
