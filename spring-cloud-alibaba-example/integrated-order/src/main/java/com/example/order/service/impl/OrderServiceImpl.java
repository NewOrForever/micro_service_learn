package com.example.order.service.impl;

import com.example.common.BusinessException;
import com.example.common.Result;
import com.example.common.ResultEnum;
import com.example.order.entity.Order;
import com.example.order.feign.AccountServiceFeignClient;
import com.example.order.feign.StorageServiceFeignClient;
import com.example.order.feign.dto.AccountDTO;
import com.example.order.feign.dto.StorageDTO;
import com.example.order.mapper.OrderMapper;
import com.example.order.service.OrderService;
import io.seata.core.context.RootContext;
import io.seata.spring.annotation.GlobalTransactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

/**
 * ClassName:OrderServiceImpl
 * Package:com.example.order.service.impl
 * Description:
 *
 * @Date:2025/3/27 17:09
 * @Author:qs@1.com
 */
@Service
public class OrderServiceImpl implements OrderService {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private AccountServiceFeignClient accountService;

    @Autowired
    private StorageServiceFeignClient storageService;

    @Override
    @GlobalTransactional
    public Result<?> createOrder(String userId, String commodityCode, Integer count) {
        // seata 内部会自动传递 XID 给下游服务
        logger.info("[createOrder] current XID: {}", RootContext.getXID());

        // 扣减库存
        StorageDTO storageDTO = new StorageDTO();
        storageDTO.setCommodityCode(commodityCode);
        storageDTO.setCount(count);
        Integer storageCode = storageService.reduceStock(storageDTO).getCode();
        if (storageCode.equals(ResultEnum.COMMON_FAILED.getCode())) {
            throw new BusinessException("stock not enough");
        }

        // 扣减账户余额
        int price = count * 2;
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setUserId(userId);
        accountDTO.setPrice(price);
        Integer accountCode = accountService.reduceBalance(accountDTO).getCode();
        if (accountCode.equals(ResultEnum.COMMON_FAILED.getCode())) {
            throw new BusinessException("balance not enough");
        }

        // 创建订单
        Order order = new Order();
        order.setUserId(userId);
        order.setCommodityCode(commodityCode);
        order.setCount(count);
        order.setMoney(price);
        order.setCreateTime(new Timestamp(System.currentTimeMillis()));
        order.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        orderMapper.saveOrder(order);
        logger.info("[createOrder] orderId: {}", order.getId());

        return Result.success(order);
    }
}
