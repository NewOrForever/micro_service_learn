package com.example.sentinel.controller;

import com.example.common.utils.PageUtils;
import com.example.common.utils.R;
import com.example.sentinel.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/sentinel/user")
public class UserController {
    @Autowired
    private UserService userService;

    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = userService.queryPage(params);
        return R.ok().put("page", page);
    }
}
