package com.tuling.tulingmallauthcenter.tokenStore;

import com.alibaba.nacos.common.utils.MapUtils;
import com.google.common.collect.Maps;
import com.tuling.tulingmall.rediscomm.util.RedisOpsExtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.ExpiringOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.tuling.tulingmall.common.constant.RedisMemberPrefix.*;

/**
 * ClassName:tokenStore
 * Package:com.tuling.tulingmallauthcenter
 * Description: 扩展了下 jwt token store，让他能存储到redis中避免刷新后 旧的token还能使用
 * 应该还是不太成熟的，还得多多的参考 {@link JdbcTokenStore}、{@link RedisTokenStore} 等等
 * 这个简单测试了下有问题 --->>>> 还需要优化的
 * 这样配合 jwt 还是有点问题的，key太长了，考虑使用 若依的方案：
 * uuid -> 作为 jwt 的负载
 *         -> 作为 redis 的 key 存储用户信息（uuid 作为属性放入用户信息中）
 *         -> jwt 可以解析 token 信息得到 uuid，但是不能解析用户信息，用户信息需要从 redis 中获取
 *         -> uuid 从缓存中获取用户信息
 *
 * @Date:2023/11/10 14:28
 * @Author:qs@1.com
 */
public class MyJwtTokenStore extends JwtTokenStore {

    /**
     * 扩展了下 jwt token store，让他能存储到redis中避免刷新后 旧的token还能使用
     * 应该还是不太成熟的，还得多多的参考 {@link JdbcTokenStore}、{@link RedisTokenStore} 等等
     * 这个简单测试了下有问题 --->>>> 还需要优化的
     */
    @Autowired
    private RedisOpsExtUtil redisOpsUtil;

    /**
     * Create a JwtTokenStore with this token enhancer (should be shared with the DefaultTokenServices if used).
     *
     * @param jwtTokenEnhancer
     */
    public MyJwtTokenStore(JwtAccessTokenConverter jwtTokenEnhancer) {
        super(jwtTokenEnhancer);
    }

    @Override
    public void storeAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
        String memberId = getMemberId(token);
        redisOpsUtil.set(MEMBER_ACCESS_TOKEN_PREFIX + memberId, token.getValue(), token.getExpiresIn(), TimeUnit.SECONDS);

        OAuth2RefreshToken refreshToken = token.getRefreshToken();
        redisOpsUtil.set(MEMBER_REFRESH_TO_ACCESS_PREFIX + refreshToken.getValue(),
                token.getValue(), token.getExpiresIn(), TimeUnit.SECONDS);
        expireRefreshToken(refreshToken, MEMBER_REFRESH_TO_ACCESS_PREFIX);

    }

    private void expireRefreshToken(OAuth2RefreshToken refreshToken, String prefix) {
        if (refreshToken instanceof ExpiringOAuth2RefreshToken) {
            ExpiringOAuth2RefreshToken expiringRefreshToken = (ExpiringOAuth2RefreshToken) refreshToken;
            Date expiration = expiringRefreshToken.getExpiration();
            if (expiration != null) {
                int seconds = Long.valueOf((expiration.getTime() - System.currentTimeMillis()) / 1000L)
                        .intValue();
                redisOpsUtil.expire(prefix + refreshToken.getValue(), seconds, TimeUnit.SECONDS);
            }
        }
    }

    private static String getMemberId(OAuth2AccessToken token) {
        Map<String, Object> map = token.getAdditionalInformation();
        if (map != null && map.containsKey("additionalInfo")) {
            Map<String, Object> memberMap = (Map<String, Object>) map.get("additionalInfo");
            if (memberMap != null && memberMap.containsKey("memberId") && memberMap.get("memberId") != null) {
                String memberId = memberMap.get("memberId").toString();
                return memberId;
            }
        }
        throw new RuntimeException("memberId is missing or null");
    }

    @Override
    public void removeAccessToken(OAuth2AccessToken token) {
        String memberId = getMemberId(token);
        redisOpsUtil.delete(MEMBER_ACCESS_TOKEN_PREFIX + memberId);
    }

    @Override
    public void storeRefreshToken(OAuth2RefreshToken refreshToken, OAuth2Authentication authentication) {
        /**
         * 默认和access_token一样的过期时间
         */
        redisOpsUtil.set(MEMBER_REFRESH_TOKEN_PREFIX + refreshToken.getValue(),
                refreshToken.getValue(), 60 * 60 * 12, TimeUnit.SECONDS);
        expireRefreshToken(refreshToken, MEMBER_REFRESH_TOKEN_PREFIX);
    }

    @Override
    public void removeRefreshToken(OAuth2RefreshToken token) {
        redisOpsUtil.delete(MEMBER_REFRESH_TOKEN_PREFIX + token.getValue());
        redisOpsUtil.delete(MEMBER_REFRESH_TO_ACCESS_PREFIX + token.getValue());
    }

    @Override
    public void removeAccessTokenUsingRefreshToken(OAuth2RefreshToken refreshToken) {
        String access_token = redisOpsUtil.get(MEMBER_REFRESH_TO_ACCESS_PREFIX + refreshToken.getValue());
        redisOpsUtil.delete(MEMBER_REFRESH_TO_ACCESS_PREFIX + refreshToken.getValue());
        OAuth2AccessToken oAuth2AccessToken = readAccessToken(access_token);
        if (oAuth2AccessToken != null) {
            String memberId = getMemberId(oAuth2AccessToken);
            redisOpsUtil.delete(MEMBER_ACCESS_TOKEN_PREFIX + memberId);
        }

    }
}
