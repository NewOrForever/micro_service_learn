package com.example.account.service;

import com.example.common.BusinessException;
import com.example.common.Result;

/**
 * ClassName:OrderService
 * Package:com.example.order.service
 * Description:
 *
 * @Date:2025/3/27 17:08
 * @Author:qs@1.com
 */
public interface AccountService {
    void reduceBalance(String userId, Integer price) throws BusinessException;

    Result<?> getRemainAccount(String userId);
}
