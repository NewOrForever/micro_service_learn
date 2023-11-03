package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * ClassName:UserDetailServiceImpl
 * Package:com.example.demo.Service
 * Description:
 *
 * @Date:2023/9/28 9:01
 * @Author:qs@1.com
 */
@Service
public class UserDetailServiceImpl implements UserDetailsService {

    /**
     * 配置 PasswordEncoder
     * 指定了 PasswordEncoder 为 BCryptPasswordEncoder，这样在登录时，就会使用 BCryptPasswordEncoder 来进行密码匹配
     */
//    @Bean
//    public PasswordEncoder passwordEncoder(){
////return NoOpPasswordEncoder.getInstance();
//        return new BCryptPasswordEncoder();
//    }
//    @Autowired
//    private PasswordEncoder passwordEncoder;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        /**
         *  password 要加上 {noop} 这种 encodingId 前缀，否则会报错：There is no PasswordEncoder mapped for the id "null"
         * @see PasswordEncoderFactories#createDelegatingPasswordEncoder() 看具体的 前缀-PasswordEncoder 映射关系
         *
         * @see org.springframework.security.crypto.password.DelegatingPasswordEncoder#matches(java.lang.CharSequence, java.lang.String) 登录时会调用这个方法
         * 进行密码匹配，这个方法会根据 password 的 encodingId 前缀来选择对应的 PasswordEncoder 进行密码匹配
         * 委托代理模式，根据 encodingId 前缀来选择对应的 PasswordEncoder 进行密码匹配
         *
         * @see DelegatingPasswordEncoder#encode(CharSequence) 默认的 PasswordEncoder 是 {@link BCryptPasswordEncoder}
         * @see PasswordEncoderFactories#createDelegatingPasswordEncoder() 的return
         * @see org.springframework.security.crypto.password.DelegatingPasswordEncoder#DelegatingPasswordEncoder(java.lang.String, java.util.Map)
         *
         * 测试开发时，可以这样写，就不用每次都去生成加密后的密码了，但是不安全
         * UserDetails user = User.withDefaultPasswordEncoder()
         *     .username("user")
         *     .password("password")
         *     .roles("USER")
         *     .build();
         * // outputs {bcrypt}$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG
         * System.out.println(user.getPassword());
         *
         *
         * 生产环境时，可以这样写，先生成加密后的密码，然后再写到代码中
         *  PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
         *   // outputs {bcrypt}$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG
         *   // remember the password that is printed out and use in the next step
         *   System.out.println(encoder.encode("password"));
         *
         *     UserDetails user = User.withUsername("user")
         *       .password("{bcrypt}$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG")
         *       .roles("USER") // 这里的 roles 等同于 authorities
         *       .build();
         *
         */
        UserDetails userDetails = User.withUsername("sql").password("{noop}111111").authorities("admin").build();
        System.out.println(userDetails.getPassword());

//        指定了 PasswordEncoder 为 BCryptPasswordEncoder
//        这样在登录时，就会使用 BCryptPasswordEncoder 来进行密码匹配，不会走 DelegatingPasswordEncoder 了
//        String encodePassword = passwordEncoder.encode("111111");
//        UserDetails userDetails = User.withUsername("sql").password(encodePassword).authorities("admin").build();
//        System.out.println(userDetails.getPassword());

        return userDetails;
    }
}
