package com.tuling.tulingmallauthcenter.feign;

import com.tuling.tulingmall.common.api.CommonResult;
import com.tuling.tulingmallauthcenter.model.UmsMember;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * fallbackFactory 服务降级属性 - 和 sentinel 限流配合使用
 */
@FeignClient(value = "tulingmall-member", path = "/member/center")
public interface UmsMemberFeignService {

    @RequestMapping("/loadUmsMember")
    CommonResult<UmsMember> loadUserByUsername(@RequestParam("username") String username);
}
