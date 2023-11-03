package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;

/**
 * ClassName:AdminController
 * Package:com.example.demo.controller
 * Description:
 *
 * @Date:2023/9/27 10:37
 * @Author:qs@1.com
 */
@RestController
@RequestMapping("/admin")
public class AdminController {
    @GetMapping("/demo")
    public String demo() {
        return "spring security demo";
    }


    @GetMapping("/test")
    public String test() {
        return "test role";
    }

    @GetMapping("/access")
    public String access() {
        return "test custom access";
    }
}
