package com.example.frontend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * ClassName:IntegrationFrontendController
 * Package:com.example.frontend.controller
 * Description:
 *
 * @Date:2025/3/27 16:21
 * @Author:qs@1.com
 */
@Controller
public class IntegrationController {

    @RequestMapping("/order")
    public String order() {
        return "order";
    }


    @RequestMapping("/sentinel")
    public String sentinel() {
        return "sentinel";
    }

}