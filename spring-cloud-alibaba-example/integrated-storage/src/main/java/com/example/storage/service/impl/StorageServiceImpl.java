package com.example.storage.service.impl;

import com.example.common.BusinessException;
import com.example.common.Result;
import com.example.common.ResultEnum;
import com.example.storage.dto.StorageDTO;
import com.example.storage.mapper.StorageMapper;
import com.example.storage.service.StorageService;
import io.seata.core.context.RootContext;
import io.seata.spring.annotation.GlobalTransactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
public class StorageServiceImpl implements StorageService {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private StorageMapper storageMapper;

    @Override
    @Transactional
    public void reduceStock(String commodityCode, Integer count)
            throws BusinessException {
        logger.info("[reduceStock] current XID: {}", RootContext.getXID());

        checkStock(commodityCode, count);

        Timestamp updateTime = new Timestamp(System.currentTimeMillis());
        int updateCount = storageMapper.reduceStock(commodityCode, count, updateTime);
        if (updateCount == 0) {
            throw new BusinessException("deduct stock failed");
        }
    }

    @Override
    public Result<?> getRemainCount(String commodityCode) {
        Integer stock = storageMapper.getStock(commodityCode);
        if (stock == null) {
            return Result.failed("commodityCode wrong,please check commodity code");
        }
        return Result.success(stock);
    }

    private void checkStock(String commodityCode, Integer count)
            throws BusinessException {
        Integer stock = storageMapper.getStock(commodityCode);
        if (stock < count) {
            throw new BusinessException("no enough stock");
        }
    }
}
