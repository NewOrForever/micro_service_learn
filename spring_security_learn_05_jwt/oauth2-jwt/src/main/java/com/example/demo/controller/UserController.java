package com.example.demo.controller;

import io.jsonwebtoken.Jwts;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/user")
public class UserController {

    @RequestMapping("/getCurrentUser")
    public Object getCurrentUser(Authentication authentication, HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        String token = null;
        if (authorization != null) {
            token = authorization.substring(authorization.indexOf("bearer") + 7);
        } else {
            token = request.getParameter("access_token");
        }

        return Jwts.parser()
                /**
                 * 这里我没用这个 getBytes 会报错呢 -> 加密的盐不一致
                 */
                .setSigningKey("jwt_oauth2_secret".getBytes(StandardCharsets.UTF_8))
                .parseClaimsJws(token)
                .getBody();
    }


    @RequestMapping("/test")
    public String test() {
        return "test";
    }
}
