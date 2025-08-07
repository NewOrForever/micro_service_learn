package com.example.account.controller;

import com.example.common.BusinessException;
import com.example.common.Result;
import com.example.account.dto.AccountDTO;
import com.example.account.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * ClassName:OrderController
 * Package:com.example.order.controller
 * Description:
 *
 * @Date:2025/3/27 17:29
 * @Author:qs@1.com
 */
@RestController
@RequestMapping("/account")
public class AccountController {


    @Autowired
    private AccountService accountService;

    @PostMapping("/reduce-balance")
    public Result<?> reduceBalance(@RequestBody AccountDTO accountDTO) {
        try {
            accountService.reduceBalance(accountDTO.getUserId(), accountDTO.getPrice());
        }
        catch (BusinessException e) {
            return Result.failed(e.getMessage());
        }
        return Result.success("");
    }

    @GetMapping("/")
    public Result<?> getRemainAccount(String userId) {
        return accountService.getRemainAccount(userId);
    }

    @GetMapping("/balance")
    public Result<?> balance(String userId) {
        return accountService.getRemainAccount(userId);
    }

}