package com.example.sentinel.controller;

import com.example.common.utils.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/sentinel/user")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping("/findUserInfoById/{id}")
    public R findOrderByUserId(@PathVariable Integer id) {
        String url = "http://order-service/order/findOrderByUserId/" + id;
        String res = restTemplate.getForObject(url, String.class);
        return R.ok().put("result", res);
    }
}
