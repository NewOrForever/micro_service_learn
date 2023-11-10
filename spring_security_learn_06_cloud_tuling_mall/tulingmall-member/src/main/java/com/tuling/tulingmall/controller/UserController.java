package com.tuling.tulingmall.controller;

import com.ramostear.captcha.HappyCaptcha;
import com.ramostear.captcha.support.CaptchaStyle;
import com.ramostear.captcha.support.CaptchaType;
import com.tuling.tulingmall.SecurityContextHolder;
import com.tuling.tulingmall.common.api.CommonResult;
import com.tuling.tulingmall.common.api.TokenInfo;
import com.tuling.tulingmall.common.constant.RedisMemberPrefix;
import com.tuling.tulingmall.model.UmsMember;
import com.tuling.tulingmall.rediscomm.util.RedisOpsExtUtil;
import com.tuling.tulingmall.service.UmsMemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 会员登录注册管理Controller
 */
@RestController
@Api(tags = "UserController", description = "用户")
@RequestMapping("/member")
@Slf4j
public class UserController {
    @Autowired
    private UmsMemberService memberService;


    @ApiOperation("获取当前登录用户")
    @RequestMapping(value = "/getCurrentUser", method = RequestMethod.GET)
    public UmsMember getCurrentUser() {
        Map<String, Object> map = SecurityContextHolder.get();
        UmsMember umsMember = new UmsMember();
        umsMember.setUsername((String) map.get("username"));
        umsMember.setId(Long.valueOf((String) map.get("memberId")));
        umsMember.setNickname((String) map.get("nickName"));
        return umsMember;
    }


}
