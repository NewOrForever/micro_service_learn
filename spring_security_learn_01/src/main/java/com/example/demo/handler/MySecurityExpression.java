package com.example.demo.handler;

import org.springframework.security.access.expression.SecurityExpressionOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

/**
 * ClassName:MySecurityExpression
 * Package:com.example.demo.handler
 * Description:
 *
 * @Date:2023/10/12 10:06
 * @Author:qs@1.com
 */
@Component
public class MySecurityExpression implements MySecurityExpressionOperations {
    @Override
    public boolean hasPermission(HttpServletRequest request, Authentication authentication) {
        // 获取主体
        Object obj = authentication.getPrincipal();
        if (obj instanceof UserDetails){
            UserDetails userDetails = (UserDetails) obj;
            String name = request.getParameter("name");
            if (StringUtils.isEmpty(name)) {
                return false;
            }
            // 获取权限
            Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
            // 判断name值是否在权限中
            return authorities.contains(new SimpleGrantedAuthority(name));
        }

        return false;
    }
}
