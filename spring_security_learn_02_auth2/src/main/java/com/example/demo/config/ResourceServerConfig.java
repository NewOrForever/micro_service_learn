package com.example.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.*;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;
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
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .anyRequest().authenticated()
                /**
                 * 会给 HttpSecurity#requestMatcher 的属性赋值
                 * @see org.springframework.security.config.annotation.web.builders.HttpSecurity#requestMatcher
                 * 该属性是用来匹配请求的，如果匹配成功，则执行该 HttpSecurity 的过滤器链
                 * 如果匹配失败，则不会执行该 HttpSecurity 的过滤器链，也就是不会进入 security 的过滤器链
                 * @see FilterChainProxy#getFilters(HttpServletRequest)
                 * @see DefaultSecurityFilterChain#matches(HttpServletRequest)
                 *
                 * 注意：
                 *  1. {@link FilterChainProxy#getFilters(HttpServletRequest)} 方法能够知道一个请求只会匹配一个过滤器链
                 *  2. 请求会按照 HttpSecurity 的配置顺序去匹配，如果匹配成功，则执行该 HttpSecurity 的过滤器链
                 *      比如 /oauth/token 先匹配{@link AuthorizationServerSecurityConfiguration} 的过滤器链，匹配成功则执行该过滤器链
                 *      不匹配的话再匹配下个过滤器链
                 *
                 * 怪不得我这里把 .and().requestMatchers().antMatchers("/user/**") 这行代码删掉后访问 /login 页面就失败了
                 * 1. 先说下HttpSecurity 去匹配的顺序 （注意：{@link WebSecurityConfigurerAdapter} 子类的 order 不能重复不然会<报错>的）：
                 * @see WebSecurityConfiguration#setFilterChainProxySecurityConfigurer(ObjectPostProcessor, List)
                 * #if (previousOrder != null && previousOrder.equals(order)) -> 有相同的 order 则抛错
                 * 授权（AuthorizationServerSecurityConfiguration 的order 为0）-> 资源（ResourceServerConfiguration 的order 为3）
                 * -> WebSecurityConfig（自定义的 order 未设置，默认为 100 - {@link WebSecurityConfigurerAdapter}）
                 * 2. 各 HttpSecurity 的 requestMatcher 属性的值：
                 * WebSecurityConfig 默认使用了 {@link AnyRequestMatcher}，所以会匹配所有的请求去执行过滤器链
                 * ResourceServerConfiguration 默认使用了 {@link ResourceServerConfiguration.NotOAuthRequestMatcher} ，只要不是 oauth 的请求就会执行过滤器链
                 * @see ResourceServerConfiguration#configure(HttpSecurity)}
                 * AuthorizationServerSecurityConfiguration 使用了 {@link OrRequestMatcher}，只要是 oauth 的请求就会执行过滤器链
                 * /oauth/token -> /oauth/token_key -> /oauth/check_token
                 * @see org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerSecurityConfiguration#configure(HttpSecurity)
                 * 3. 最后说下原因：
                 * /login 请求进来后，会先匹配 AuthorizationServerSecurityConfiguration 的过滤器链，不是 oauth 的请求，所以不会执行过滤器链
                 * 然后匹配 ResourceServerConfiguration 的过滤器链，只要不是 oauth 的请求都会执行过滤器链，所以执行该过滤器链，但是没有配置 login 登陆页面所以直接抛错
                 * 所以 自定义的链路就不会执行了
                 * 4. 解决办法：
                 * 1）.and().requestMatchers().antMatchers("/user/**") 这行代码加上，这样就只有 /user/** 的请求才会执行该 HttpSecurity 的过滤器链
                 *  /login 请求就不会执行该 HttpSecurity 的过滤器链，能进入自定义的过滤器链（配置了登录页）
                 * 2）修改 WebSecurityConfig 的 order 为 1，这样就会先匹配 WebSecurityConfig 的过滤器链，最后再去匹配 ResourceServerConfiguration 的过滤器链
                 */
                .and().requestMatchers().antMatchers("/user/**");
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        /**
         * @see ResourceServerConfiguration
         * @see ResourceServerSecurityConfigurer
         */
        super.configure(resources);
    }
}
