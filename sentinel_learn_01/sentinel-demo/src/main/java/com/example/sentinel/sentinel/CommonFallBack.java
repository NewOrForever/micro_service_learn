package com.example.sentinel.sentinel;

import com.example.common.utils.R;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * ClassName:CommonFallBack
 * Package:com.example.sentinel.sentinel
 * Description:
 *
 * @Date:2022/7/20 14:21
 * @Author:qs@1.com
 */
public class CommonFallBack {
    /**
     * 方法必须是static且多个方法之间方法名不能一样
     * 方法参数需要和@SentinelResource注解的方法的参数一样，增加一个Throwable 参数
     */
    public static R fallback(Integer id, Throwable e) {
        return R.error(-1, "============> 熔断降级" + e);
    }
}
