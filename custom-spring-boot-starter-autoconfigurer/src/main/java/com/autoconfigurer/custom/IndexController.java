package com.autoconfigurer.custom;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ClassName:IndexController
 * Package:com.autoconfigurer.custom
 * Description:
 *
 * @Date:2022/6/9 17:00
 * @Author:qs@1.com
 */
@RestController
public class IndexController {

    private final HelloProperties helloProperties;

    public IndexController(HelloProperties helloProperties) {
        this.helloProperties = helloProperties;
    }

    @RequestMapping("/index")
    public String Index() {
        return helloProperties.getName() + "欢迎您。。。";
    }
}
