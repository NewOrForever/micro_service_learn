package com.example.sentinel.controller;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.Sph;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.Tracer;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ClassName:HelloController
 * Package:com.example.sentinel.controller
 * Description:
 *
 * @Date:2022/7/19 16:42
 * @Author:qs@1.com
 */
@RestController
@RequestMapping("/sentinel/hello")
public class HelloController {
    private static final Logger logger = LoggerFactory.getLogger(HelloController.class);
    private static final String RESOURCE_NAME = "hello";

    @RequestMapping("/demo")
    public String demo() {
        Entry entry = null;
        try {
            // 资源名可使用任意有业务语义的字符串，比如方法名、接口名或其它可唯一标识的字符串
            entry = SphU.entry(RESOURCE_NAME);
            // 被保护的业务逻辑 - 实际就是给业务逻辑做个before的切面
            String str = "hello world";
            logger.info(String.format("----------------> %s", str));

            return str;
        } catch (BlockException e) {
            e.printStackTrace();
        } catch (Exception ex){
            Tracer.traceEntry(ex, entry);
        }
        finally {
            if (entry != null) {
                entry.close();
            }
        }

        return null;
    }

}
