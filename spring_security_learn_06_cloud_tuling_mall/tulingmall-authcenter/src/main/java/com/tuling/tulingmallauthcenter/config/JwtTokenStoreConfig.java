package com.tuling.tulingmallauthcenter.config;

import com.tuling.tulingmallauthcenter.enhancer.TulingTokenEnhancer;
import com.tuling.tulingmallauthcenter.properties.JwtCAProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.rsa.crypto.KeyStoreKeyFactory;

import java.security.KeyPair;

@Configuration
@EnableConfigurationProperties(value = JwtCAProperties.class)
public class JwtTokenStoreConfig {

    /**
     * jwt token 是无状态的，所以不需要存储到数据库中
     * 但是有一个问题啊：我刷新 token 后，原来的 jwt token 还是可以用的，这个怎么解决呢？
     * 如果应用场景不允许的话就得考虑使用 redis 存储了 - RedisTokenStore/JdbcTokenStore，如果应用场景更复杂点的话那就得
     * 考虑我们自己实现 TokenStore 了（当然可以扩展下 RedisTokenStore、JdbcTokenStore、JwtTokenStore 嘛）
     * 如果应用场景允许这种情况的话，那就不用管了，因为 jwt token 是有过期时间的
     *
     * 不管是 jwt 还是 redis 还是 jdbc，每个 client 每日refresh token 次数都需要作数量限制
     */
    @Bean
    @Primary
    public TokenStore jwtTokenStore(){
        return new JwtTokenStore(jwtAccessTokenConverter());
    }

    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter(){
        JwtAccessTokenConverter accessTokenConverter = new
                JwtAccessTokenConverter();
        // 配置JWT使用的秘钥
        // accessTokenConverter.setSigningKey("123123");

        // 配置JWT使用的秘钥 非对称加密
        accessTokenConverter.setKeyPair(keyPair());

        return accessTokenConverter;
    }

    @Autowired
    private JwtCAProperties jwtCAProperties;

    @Bean
    public KeyPair keyPair() {
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(
                new ClassPathResource(jwtCAProperties.getKeyPairName()), jwtCAProperties.getKeyPairSecret().toCharArray());
        return keyStoreKeyFactory.getKeyPair(jwtCAProperties.getKeyPairAlias(), jwtCAProperties.getKeyPairStoreSecret().toCharArray());
    }

    /**
     * token的增强器 根据自己业务添加字段到Jwt中
     * @return
     */
    @Bean
    public TulingTokenEnhancer tulingTokenEnhancer() {
        return new TulingTokenEnhancer();
    }
}
