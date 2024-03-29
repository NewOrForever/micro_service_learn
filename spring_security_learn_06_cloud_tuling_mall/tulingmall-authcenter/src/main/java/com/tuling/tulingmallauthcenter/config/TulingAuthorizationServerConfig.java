package com.tuling.tulingmallauthcenter.config;

import com.tuling.tulingmallauthcenter.enhancer.TulingTokenEnhancer;
import com.tuling.tulingmallauthcenter.service.TulingUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.client.ClientCredentialsTokenEndpointFilter;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableAuthorizationServer
public class TulingAuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private TulingUserDetailsService tulingUserDetailsService;

    @Autowired
//    @Qualifier("jwtTokenStore")
    private TokenStore tokenStore;

    @Autowired
    private JwtAccessTokenConverter jwtAccessTokenConverter;

    @Autowired
    private AuthenticationManager authenticationManagerBean;

    @Autowired
    private TulingTokenEnhancer tulingTokenEnhancer;

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        // 配置JWT的内容增强器
        TokenEnhancerChain enhancerChain = new TokenEnhancerChain();
        List<TokenEnhancer> delegates = new ArrayList<>();
        delegates.add(tulingTokenEnhancer);
        delegates.add(jwtAccessTokenConverter);
        enhancerChain.setTokenEnhancers(delegates);

        // 使用密码模式需要配置
        endpoints.authenticationManager(authenticationManagerBean)
                .tokenStore(tokenStore)  // 指定token存储策略是jwt
                .accessTokenConverter(jwtAccessTokenConverter) // 使用 jwt 时需要配置 -> readAccessToken
                .tokenEnhancer(enhancerChain) // 配置tokenEnhancer
                .reuseRefreshTokens(false)  // refresh_token是否重复使用
                .userDetailsService(tulingUserDetailsService) // 刷新令牌授权包含对用户信息的检查
                .allowedTokenEndpointRequestMethods(HttpMethod.GET,HttpMethod.POST); //支持GET,POST请求
    }


    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        /**第三方客户端校验token需要带入 clientId 和clientSecret来校验
         * @see org.springframework.security.access.expression.SecurityExpressionRoot#isAuthenticated()
          */
        security.checkTokenAccess("isAuthenticated()")
                .tokenKeyAccess("isAuthenticated()");//来获取我们的tokenKey需要带入clientId,clientSecret

        /**
         * /oauth/token 允许表单认证 - client_id、client_secret 直接放在 form 表单中提交
         * @see AuthorizationServerSecurityConfigurer#clientCredentialsTokenEndpointFilter(org.springframework.security.config.annotation.web.builders.HttpSecurity)
         * @see ClientCredentialsTokenEndpointFilter
         *
         * 别的请求不走 ClientCredentialsTokenEndpointFilter 过滤器，还是走原来的过滤器链 {@link BasicAuthenticationFilter}
         */
        security.allowFormAuthenticationForClients();
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        // 配置授权服务器存储第三方客户端的信息  基于DB存储   oauth_client_details
        clients.withClientDetails(clientDetails());

    }

    @Bean
    public ClientDetailsService clientDetails(){
        return new JdbcClientDetailsService(dataSource);
    }


}
