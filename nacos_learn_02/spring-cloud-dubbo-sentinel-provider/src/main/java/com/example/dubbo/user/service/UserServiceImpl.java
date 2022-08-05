package com.example.dubbo.user.service;

import com.alibaba.csp.sentinel.adapter.dubbo.config.DubboAdapterGlobalConfig;
import com.alibaba.csp.sentinel.adapter.dubbo.fallback.DubboFallback;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.example.dubbo.user.mapper.UserMapper;
import com.example.entity.User;
import com.example.service.UserService;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.dubbo.rpc.AsyncRpcResult;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * ClassName:UserServiceImpl
 * Package:com.example.dubbo.service.impl
 * Description:
 *
 * @Date:2022/6/21 13:23
 * @Author:qs@1.com
 */
@DubboService
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public List<User> list() {
        return userMapper.list();
    }

    @Override
    public User getById(Integer id) {
        return userMapper.getById(id);
    }

    @PostConstruct
    public void init() {
        DubboAdapterGlobalConfig.setProviderFallback(new DubboFallback() {
            @Override
            public Result handle(Invoker<?> invoker, Invocation invocation, BlockException ex) {
                List<User> userList = new ArrayList<>();
                userList.add(new User(0, "======>dubbo provider fallback"));
                return AsyncRpcResult.newDefaultAsyncResult(userList, invocation);
            }
        });
    }
}
