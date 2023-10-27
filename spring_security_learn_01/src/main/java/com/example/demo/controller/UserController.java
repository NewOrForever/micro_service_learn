package com.example.demo.controller;

import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafProperties;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * ClassName:LoginController
 * Package:com.example.demo.controller
 * Description:
 *
 * @Date:2023/10/7 10:14
 * @Author:qs@1.com
 */
@Controller
public class UserController {
    @RequestMapping("/user/index")
    public String index() {
        return "测试 request matcher";
    }

}
