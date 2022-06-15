package com.example.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.entity.OrderEntity;
import com.example.common.utils.PageUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * ClassName:OrderService
 * Package:com.example.common.service
 * Description:
 *
 * @Date:2022/6/15 17:26
 * @Author:qs@1.com
 */

public interface OrderService extends IService<OrderEntity> {
    PageUtils queryPage(Map<String, Object> params);
    List<OrderEntity> listByUserId(Integer userId);
}
