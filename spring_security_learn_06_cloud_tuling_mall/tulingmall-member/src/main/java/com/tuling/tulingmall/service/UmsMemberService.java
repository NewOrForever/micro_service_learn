package com.tuling.tulingmall.service;

import com.tuling.tulingmall.common.api.CommonResult;
import com.tuling.tulingmall.common.api.TokenInfo;
import com.tuling.tulingmall.model.UmsMember;
import org.springframework.transaction.annotation.Transactional;

/**
 * 会员管理Service
 * Created by macro on 2018/8/3.
 */
public interface UmsMemberService {
    /**
     * 根据用户名获取会员
     */
    UmsMember getByUsername(String username);

    /**
     * 根据会员编号获取会员
     */
    UmsMember getById(Long id);


    /**
     * 生成验证码
     */
    CommonResult generateAuthCode(String telephone);

    /**
     * 获取当前登录会员
     */
    UmsMember getCurrentMember();



    /**
     * 登录后获取token
     */
    TokenInfo login(String username, String password);

    /**
     * 刷新token
     */
    String refreshToken(String token);

    @Transactional
    int updateUmsMember(UmsMember umsMember);
}
