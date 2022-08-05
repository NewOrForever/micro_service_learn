package com.example.sentinel.sentinel;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.example.common.utils.R;

import java.util.Map;

/**
 * ClassName:CommonBlockHandler
 * Package:com.example.sentinel.sentinel
 * Description: Sentinel限流BlockException的处理类
 *
 * @Date:2022/7/20 9:08
 * @Author:qs@1.com
 */
public class CommonBlockHandler {
    /**
     * 方法必须是static且多个方法之间方法名不能一样
     * 方法参数需要和@SentinelResource注解的方法的参数一样，增加一个BlockException 参数
     */
    public static R handlerException(Map<String, Object> params, BlockException blockException) {
        return R.error(-1, String.format("=======> %s", "被限流啦！"));
    }

    public static R handlerUserInfoException(Integer id, BlockException blockException) {
        return R.error(-1, String.format("=======> %s", "被限流啦！"));
    }
}
