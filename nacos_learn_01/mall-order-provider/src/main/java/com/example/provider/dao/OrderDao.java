package com.example.provider.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.provider.entity.OrderEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
}
