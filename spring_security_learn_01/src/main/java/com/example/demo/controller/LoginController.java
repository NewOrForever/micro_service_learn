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
public class LoginController {
    @RequestMapping("/main")
    public String main() {
        return "redirect:/main.html";
    }
    @RequestMapping("/toerror")
    public String error() {
        return "redirect:/error.html";
    }

    @RequestMapping("/showLogin")
    public String showLogin() {
        /**
         * 引入了thymeleaf模板引擎，所以这里的login会被自动拼接为templates 下的 login.html
         * @see ThymeleafProperties
          */
        return "login";
    }
}
