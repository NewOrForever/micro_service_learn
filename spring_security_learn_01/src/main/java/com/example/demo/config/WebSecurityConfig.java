package com.example.demo.config;

import com.example.demo.controller.LoginController;
import com.example.demo.handler.*;
import com.example.demo.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.config.annotation.web.configurers.RememberMeConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.rememberme.AbstractRememberMeServices;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationFilter;
import org.springframework.security.web.session.ConcurrentSessionFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.util.Collection;
import java.util.List;

/**
 * ClassName:WebSecurityConfig
 * Package:com.example.demo.config
 * Description:
 *
 * @Date:2023/9/28 11:03
 * @Author:qs@1.com
 */
// @Configuration
@EnableGlobalMethodSecurity(jsr250Enabled = true, securedEnabled = true, prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /******************************sample for learn********************************/
//    // 用户认证管理
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
////        String encode = passwordEncoder().encode("123456");
//        auth.inMemoryAuthentication()
//                .passwordEncoder(passwordEncoder())
//                .withUser("sq").password("123456").roles("admin")
//                .and()
//                .withUser("sq2").password("123456").roles("user");
//
//    }

// 也可以这样写来实现用户认证 => 等同于上面的configure(AuthenticationManagerBuilder auth)方法
//    @Override
//    protected UserDetailsService userDetailsService() {
//        return new UserDetailsService() {
//            @Override
//            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//                String pwd = passwordEncoder().encode("123456");
//                return User.withUsername("sq").password(pwd).roles("admin", "user").build();
//            }
//        };
//    }

    // 自定义登录页面配置
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin() // 自定义自己编写的登录页面 - 表单提交
                .loginPage("/login.html") // 登录页面设置
                /**
                 * 和 login.html 中的 form 表单的 action="/user/login" 一致
                 * authFilter.setRequiresAuthenticationRequestMatcher(createLoginProcessingUrlMatcher(loginProcessingUrl));
                 * createLoginProcessingUrlMatcher(loginProcessingUrl) -> 创建 AntPathRequestMatcher 对象
                 * authFilter extends AbstractAuthenticationProcessingFilter -> 给 requiresAuthenticationRequestMatcher 属性赋值
                 *
                 * @see AbstractAuthenticationProcessingFilter#requiresAuthentication(HttpServletRequest, HttpServletResponse)
                 * 该方法中调用了 requiresAuthenticationRequestMatcher.matches(request) 方法 -> 判断登录action 是否匹配 -> 如果匹配则进行登录认证
                 */
                .loginProcessingUrl("/user/login") // 登录访问路径 => form表单提交时，action的地址 -> 如果不设置则使用loginPage设置的路径
                // .defaultSuccessUrl("/admin/demo") // 登录成功之后，跳转路径
                /**
                 * 认证成功/失败之后转发的路径，必须是Post请求
                 * 认证失败之后转发的路径，必须是Post请求
                 *
                 * 方法点进去看源码实际调用的是
                 * successHandler -> ForwardAuthenticationSuccessHandler
                 * failureHandler -> ForwardAuthenticationFailureHandler
                 */
//                .successForwardUrl("/main")
//                .failureForwardUrl("/toerror")
                .successHandler(new MyAuthenticationSuccessHandler("/main.html"))
                .failureHandler(new MyAuthenticationFailHandler("/error.html"))
                /**
                 * @see UsernamePasswordAuthenticationFilter#usernameParameter -> 默认是 username
                 * @see UsernamePasswordAuthenticationFilter#passwordParameter -> 默认是 password
                 * 配置了 usernameParameter 和 passwordParameter 之后，登录表单提交时，必须和这里的参数名一致 -> 该 login.html 中的 form 表单的 input 的 name 属性
                 */
//                 .usernameParameter("username001") // 登录表单提交时，用户名的name属性
//                 .passwordParameter("password001") // 登录表单提交时，密码的name属性
                /**
                 * 方法级别的权限控制
                 * @see org.springframework.security.web.access.intercept.FilterSecurityInterceptor
                 * authorizeRequests() 方法会创建 {@link ExpressionUrlAuthorizationConfigurer} 对象
                 * @see ExpressionUrlAuthorizationConfigurer#configure(org.springframework.security.config.annotation.web.HttpSecurityBuilder)
                 * 该方法中会创建 {@link org.springframework.security.web.access.intercept.FilterSecurityInterceptor} 对象添加到
                 * @see org.springframework.security.config.annotation.web.builders.HttpSecurity#filters 中
                 * 最后在 Security Filter Chain 过滤器链路中执行
                 *
                 * @see ExpressionUrlAuthorizationConfigurer.AuthorizedUrl#access(String)
                 * @see ExpressionUrlAuthorizationConfigurer#interceptUrl(Iterable, Collection)
                 * 自定义权限表达式，通过bean的beanName.方法(参数)的形式进行调用
                 * @see org.springframework.security.access.expression.SecurityExpressionRoot
                 * @see org.springframework.security.access.expression.SecurityExpressionHandler
                 *
                 */
                .and().authorizeRequests()
                // 设置哪些路径可以直接访问，不需要认证
                .antMatchers("/login.html", "/user/login", "/session/invalid", "/error.html").permitAll()
                .antMatchers("/showLogin").permitAll()

                /**
                 * 设置访问权限：大小写敏感
                 */
                // 当前登录用户，只有具有admin权限才可以访问这个路径
                // .antMatchers("/admin/test").hasAuthority("ADMIN")
                // 当前登录用户, 只有具有指定IP地址才可以访问这个路径
                // 可以通过 request.getRemoteAddr() 获取 ip 地址, localhost 和 127.0.0.1 输出的 ip地址是不一样的
                // localhost -> getRemoteAddr: 0:0:0:0:0:0:0:1
                .antMatchers("/admin/test").hasIpAddress("127.0.0.1")
                // 当前登录用户，只要具有其中一个权限就可以访问这个路径
                // .antMatchers("/test/index").hasAnyAuthority("admins,manager")
                // 当前登录用户，必须具有admins权限才可以访问这个路径
                // .antMatchers("/test/index").hasRole("sale")
                // 当前登录用户，只要具有其中一个角色就可以访问这个路径
                // .antMatchers("/test/index").hasAnyRole("sale,manager")
                /**
                 * 自定义权限表达式，通过bean的beanName.方法(参数)的形式进行调用
                 * @see org.springframework.security.access.expression.SecurityExpressionRoot
                 * @see org.springframework.security.access.expression.SecurityExpressionHandler
                 *
                 * 也可以直接使用 SecurityExpressionRoot 自带的
                 * access("hasAnyAuthority('admins,manager')")
                 */
                .antMatchers("/admin/access").access("@mySecurityExpression.hasPermission(request, authentication)")
                .antMatchers("/admin/access2").access("hasAuthority('ADMIN')")
                .anyRequest().authenticated(); // 任何请求，登录后才可以访问
//                .and().csrf().disable(); // 关闭csrf防护

        /**
         *  会给 HttpSecurity#requestMatcher 的属性赋值
         *  @see org.springframework.security.config.annotation.web.builders
         *  >>> 该属性是用来匹配请求的，如果匹配成功，则执行该 HttpSecurity 的过滤器链 <<<
         *  >>> 如果匹配失败，则不会执行该 HttpSecurity 的过滤器链，也就是不会进入 security 的过滤器链 <<<
         *  @see FilterChainProxy#getFilters(HttpServletRequest)
         *  @see DefaultSecurityFilterChain#matches(HttpServletRequest)
         */
//        http.requestMatchers().antMatchers("/user/**", "/admin/**");

        // 403（无权限）处理
        http.exceptionHandling()
                .accessDeniedHandler(new MyAccessDeniedHandler())
                ;

        /**
         * 会话管理 Session
         */
        http.sessionManagement()
                /**
                 * always               如果session不存在总是需要创建
                 * ifRequired        如果需要就创建一个session（默认）登录时
                 * never                Spring Security 将不会创建session，但是如果应用中其他地方创建了session，那么Spring Security将会使用它
                 * stateless           Spring Security将绝对不会创建session，也不使用session。并且它会暗示不使用cookie，所以每个请求都需要
                 * 重新进行身份验证。这种无状态架构适用于 REST API 及其无状态认证机制。
                 */
                // .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // 默认会使用 IF_REQUIRED
                .invalidSessionUrl("/session/invalid")
                /**
                 * @see SessionManagementConfigurer#createConcurrencyFilter(org.springframework.security.config.annotation.web.HttpSecurityBuilder)
                 * 会创建一个 ConcurrentSessionFilter 对象 -> MyExpiredSessionStrategy 这个自定义的会话过期策略会赋值到 ConcurrentSessionFilter 对象中
                 * @see ConcurrentSessionFilter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
                 */
                .maximumSessions(1) // 最大会话数
                .expiredSessionStrategy(new MyExpiredSessionStrategy())
                /**
                 * false    当达到最大会话数时，是否保留已经登录的用户 -> 默认为false，新用户登录时，会将已经登录的用户踢出
                 * true     当达到最大会话数时，不允许后面的用户登录
                 */
                .maxSessionsPreventsLogin(false);

        /**
         * 应用做了集群之后，需要配置一个共享的session存储 -> 例如：redis、mongodb、mysql等，这里使用的是redis
         * 1. 引入依赖：
         * <dependency>
         *     <groupId>org.springframework.session</groupId>
         *     <artifactId>spring-session-data-redis</artifactId>
         * </dependency>
         * <dependency>
         *     <groupId>redis.clients</groupId>
         *     <artifactId>jedis</artifactId>
         *     <version>3.1.0</version>
         * </dependency>
         *
         * 2. 配置redis
         * spring:
         *   redis:
         *     host: 123.60.150.23
         *     port: 6379
         *   session:
         *     store-type: redis
         */

        /**
         * 记住我 Remember Me 功能 - login.html 中的记住我复选框
         * @see RememberMeAuthenticationFilter
         * @see RememberMeServices
         * @see AbstractRememberMeServices
         * @see RememberMeConfigurer
         */
        http.rememberMe()
                .tokenRepository(persistentTokenRepository())                  // 设置持久化仓库
                .tokenValiditySeconds(3600)                                               // 超时时间，单位s，默认两周
//                .rememberMeParameter("custom_remmberme_name")    // 设置登录时，记住我复选框的name属性值, 默认是 remember-me
                .userDetailsService(userServiceImpl);                                  // 设置自定义登录逻辑

        /**
         * 退出登录
         * Spring security默认实现了logout退出，用户只需要向 Spring Security 项目中发送 /logout 退出请求即可
         * 默认的退出 url 为 /logout ，退出成功后跳转到 /login?logout
         * @see org.springframework.security.web.authentication.logout.LogoutFilter
         * @see LogoutConfigurer
         * @see SecurityContextLogoutHandler 处理退出登录操作
         * - 销毁HTTPSession 对象
         * - 清除认证状态
         * @see LogoutSuccessHandler 处理退出成功之后的操作
         * @see org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler 默认实现类
         * 可以自定义退出登录逻辑
         */
        http.logout()
                .logoutUrl("/logout")                       // 退出登录的url
                // 退出成功之后跳转的url，配合默认的 LogoutSuccessHandler 使用
                // 如果自定义了 LogoutSuccessHandler ，则该配置无效（除非自定义的使用 super.handle 方法）
                .logoutSuccessUrl("/login.html")
                .logoutSuccessHandler(new MyLogoutSuccessHandler());

        /**
         * CSRF 防护
         * @see org.springframework.security.web.csrf.CsrfFilter
         * @see LoginController#showLogin() -> templates/login.html 模板中引入了csrf标签
         * 设置了 csrf 防护之后，登录页面中必须引入 csrf 标签，否则会报错
         * <input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}"/>
         *
         *  测试时改下 formLogin.loginPage 的路径为 /showLogin
         *       http.formLogin()
         *                 .loginPage("/showLogin")
         *                 .loginProcessingUrl("/user/login");
         *
         *  原理：
         *  1. 在渲染请求页面时埋入_csrf token
         *  2. 在提交表单时，将_csrf 作为参数一起提交到后台，后台会校验_csrf token是否正确
         *  3. csfr 会对 HttpMethod 判断是否需要校验 {@link org.springframework.security.web.csrf.CsrfFilter.DefaultRequiresCsrfMatcher}
         */
        http.csrf().disable(); // 关闭csrf防护

    }

    @Autowired
    private DataSource dataSource;

    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        //设置数据源
        jdbcTokenRepository.setDataSource(dataSource);
        return jdbcTokenRepository;
    }


    /**
     * 一般项目就是这么做的，自定义用户认证逻辑
     */
    @Autowired
    private UserServiceImpl userServiceImpl;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userServiceImpl);
    }

//    /**
//     * 全局用户认证配置
//     */
//    @Autowired
//    public void configureGobal(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(userServiceImpl);
//    }


//    /**
//     * 静态资源放行
//     * @param web
//     * @throws Exception
//     */
//    @Override
//    public void configure(WebSecurity web){
//        web.ignoring().antMatchers("/css/**", "/js/**", "/images/**");
//    }
}
