package com.example.storage.service;

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
public interface StorageService {
    void reduceStock(String commodityCode, Integer orderCount) throws BusinessException;

    Result<?> getRemainCount(String commodityCode);
}
