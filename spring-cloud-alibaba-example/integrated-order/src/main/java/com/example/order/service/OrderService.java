package com.example.order.service;

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
public interface OrderService {
    Result<?> createOrder(String userId, String commodityCode, Integer count)
            throws BusinessException;
}
