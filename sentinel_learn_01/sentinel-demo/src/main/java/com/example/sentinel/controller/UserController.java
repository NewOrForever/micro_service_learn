package com.example.sentinel.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.example.common.utils.PageUtils;
import com.example.common.utils.R;
import com.example.sentinel.entity.UserEntity;
import com.example.sentinel.sentinel.CommonBlockHandler;
import com.example.sentinel.sentinel.CommonFallBack;
import com.example.sentinel.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/sentinel/user")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    /*********************接入aspect、dashboard，第一波测试***********************/
    /**
     * @SentinelResource 注解
     * blockHandlerClass 和 fallbackClass 没设置的的话默认是去找当前 class 中的 blockHandler 指定的方法
     * 如果blockHandlerClass、fallbackClass指定了的话则取对应的class中找方法 - note：这种方式的话方法需要static
     * <p>
     * 这个 blockHandler、fallback 指定的方法的参数需要和 @SentinelResource 注解的方法是一样的，去它的切面源码 around 就能看出
     */

    @RequestMapping("/list")
    @SentinelResource(value = "UserController#list",
            blockHandler = "handlerException",
            fallback = "fallback")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = userService.queryPage(params);
        //int i = 1/ 0;
        return R.ok().put("page", page);
    }

    // 处理限流异常处理
    public R handlerException(Map<String, Object> params, BlockException blockException) {
        return R.error(-1, String.format("=======> %s", "被限流啦！"));
    }

    // 处理业务异常
    public R fallback(Map<String, Object> params, Throwable ex) {
        ex.printStackTrace();
        logger.info("========>" + ex.toString());
        return R.error(-1, String.format("=======> %s -------> %s", "被熔断降级啦！", ex.getMessage()));
    }

    /**********************接下来的异常处理方法都写在别的类中************************/
    @RequestMapping("/info/{id}")
    @SentinelResource(value = "userInfo",
            blockHandlerClass = CommonBlockHandler.class, blockHandler = "handlerUserInfoException",
            fallbackClass = CommonFallBack.class, fallback = "fallback")
    public R info(@PathVariable Integer id) {
        UserEntity user = userService.getById(id);
        return R.ok().put("user", user);
    }

    /*********************下面是增删改操作************************/
    @RequestMapping("/save")
    public R save(@RequestBody UserEntity user) {
        boolean success = userService.save(user);
        return success ? R.ok() : R.error(-1, "save fail");
    }

    @RequestMapping("/update")
    public R update(@RequestBody UserEntity user) {
        userService.updateById(user);
        return R.ok();
    }

    @RequestMapping("/delete")
    public R delete(@RequestBody List<Integer> ids) {
       userService.removeByIds(ids);
        return R.ok();
    }


}
