package com.example.sentinel.service.impl;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;
import com.example.sentinel.entity.UserEntity;
import com.example.sentinel.mapper.UserMapper;
import com.example.sentinel.service.UserService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements UserService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<UserEntity> page = this.page(
                new Query<UserEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }

    @SentinelResource(value = "getUser", blockHandler = "handlerBlockException")
//    @SentinelResource(value = "getUser") // 不指定blockHandler，使用CommonFilter的setUrlBlockHandler来指定处理BlockException - 结果：拦截不到BlockException
    @Override
    public UserEntity getUserById(int id) {
        return baseMapper.selectById(id);
    }
    public UserEntity handlerBlockException(int id, BlockException blockException) {
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername("--------------> 被限流降级啦");
        return userEntity;
    }
}
