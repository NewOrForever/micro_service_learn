package com.example.demo.config;

import com.example.demo.JwtTokenEnhancer;
import com.example.demo.service.UserServiceImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerSecurityConfiguration;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.*;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import java.util.ArrayList;
import java.util.List;

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
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {
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
                .redirectUris("http://www.baidu.com")
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
                .authorizedGrantTypes("authorization_code", "implicit", "password", "client_credentials", "refresh_token");

    }

    @Autowired
    private AuthenticationManager authenticationManagerBean;
    @Autowired
    private UserServiceImpl userServiceImpl;
    /**
     * RedisTokenStore 的 Bean 我没有注掉，所以这里需要指定一下
     * 也可以在 JwtConfig 中 JwtTokenStore 的 Bean 使用 @Primary 注解
     * 推荐还是使用 @Primary 注解，不然不知道又会那个配置类注入的 TokenStore 变成 RedisTokenStore 了
     */
    @Autowired
//    @Qualifier("jwtTokenStore")
    private TokenStore tokenStore;
    @Autowired
    private AccessTokenConverter jwtAccessTokenConverter;
    @Autowired
    private JwtTokenEnhancer jwtTokenEnhancer;

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        /**
         * @see org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerEndpointsConfiguration#init()
         */

        /**
         * 配置 jwt 内容增强 TokenEnhancer
         * 使用  jwt 时 JwtAccessTokenConverter是必须的，且要放在 enhancer chain 的最后一个 -> 需要生成 token 字符串
         * @see JwtAccessTokenConverter#encode(org.springframework.security.oauth2.common.OAuth2AccessToken, org.springframework.security.oauth2.provider.OAuth2Authentication)
         * @see TokenEnhancerChain#enhance(org.springframework.security.oauth2.common.OAuth2AccessToken, org.springframework.security.oauth2.provider.OAuth2Authentication)
         */
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        List<TokenEnhancer> delegates = new ArrayList<>();
        tokenEnhancerChain.setTokenEnhancers(delegates);
        delegates.add(jwtTokenEnhancer);
        delegates.add((JwtAccessTokenConverter)jwtAccessTokenConverter);

        endpoints
                .authenticationManager(authenticationManagerBean) // 密码模式需要配置
                .reuseRefreshTokens(false) // 刷新token, 每次刷新token都会重新生成一个refresh_token
                .userDetailsService(userServiceImpl) // 刷新token需要配置
                /**
                 * 当 TokenStore bean 有多个时
                 * 授权服务和资源服务都需要指定同一个 TokenStore
                 */
                .tokenStore(tokenStore) // token存储方式 - jwt
                /**
                 * 使用 jwt 时还需要配置 AccessTokenConverter
                 * @see DefaultTokenServices#readAccessToken(String) 提取 token 中的信息
                 * @see DefaultTokenServices#createAccessToken(OAuth2Authentication, OAuth2RefreshToken)
                 * @see JwtAccessTokenConverter#encode(org.springframework.security.oauth2.common.OAuth2AccessToken, org.springframework.security.oauth2.provider.OAuth2Authentication)
                 */
                .accessTokenConverter(jwtAccessTokenConverter)
                .tokenEnhancer(tokenEnhancerChain)

                .allowedTokenEndpointRequestMethods(HttpMethod.GET, HttpMethod.POST);
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        /**
         * @see AuthorizationServerSecurityConfiguration#configure(AuthorizationServerSecurityConfigurer)
         */
        security.allowFormAuthenticationForClients(); // 允许表单认证
    }

}
