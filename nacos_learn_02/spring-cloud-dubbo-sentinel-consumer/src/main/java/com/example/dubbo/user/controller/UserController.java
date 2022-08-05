package com.example.dubbo.user.controller;

import com.alibaba.csp.sentinel.adapter.dubbo.config.DubboAdapterGlobalConfig;
import com.alibaba.csp.sentinel.adapter.dubbo.fallback.DubboFallback;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.example.entity.User;
import com.example.service.UserService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.rpc.AsyncRpcResult;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * ClassName:UserController
 * Package:com.example.dubbo.user.controller
 * Description:
 *
 * @Date:2022/6/21 15:34
 * @Author:qs@1.com
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @DubboReference(mock = "com.example.service.UserServiceDubboMock")
    private UserService userService;

    @RequestMapping("/list")
    public List<User> list() {
        return userService.list();
    }

    @PostConstruct
    public void init() {
        DubboAdapterGlobalConfig.setConsumerFallback(new DubboFallback() {
            @Override
            public Result handle(Invoker<?> invoker, Invocation invocation, BlockException ex) {
                List<User> userList = new ArrayList<>();
                userList.add(new User(0, "======>dubbo fallback"));
                return AsyncRpcResult.newDefaultAsyncResult(userList, invocation);
            }
        });
    }
}
