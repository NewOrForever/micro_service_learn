package com.example.order.feign;

import com.example.common.Result;
import com.example.order.feign.dto.AccountDTO;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * ClassName:AccountServiceFeignClient
 * Package:com.example.order.feign
 * Description:
 *
 * @Date:2025/3/27 17:12
 * @Author:qs@1.com
 */
@FeignClient(name = "integrated-account")
public interface AccountServiceFeignClient {
    @PostMapping("/account/reduce-balance")
    Result<?> reduceBalance(@RequestBody AccountDTO accountReduceBalanceDTO);
}
