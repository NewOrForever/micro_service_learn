package com.example.demo.service.impl;

import com.example.demo.domain.Permission;
import com.example.demo.domain.User;
import com.example.demo.mapper.PermissionMapper;
import com.example.demo.mapper.UserMapper;
import com.example.demo.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * ClassName:SysUserServiceImpl
 * Package:com.example.demo.Service
 * Description:
 *
 * @Date:2023/9/28 16:48
 * @Author:qs@1.com
 */
@Service
public class UserServiceImpl implements IUserService, UserDetailsService {
    @Autowired
    private PermissionMapper permissionMapper;
    @Autowired
    private UserMapper userMapper;


    @Override
    public User getByUsername(String username) {
        return userMapper.getByUsername(username);
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        /**
         * 从数据库中加载用户信息
         * 通常在这里可以从数据库中加载用户信息，权限信息，角色信息等
         * 项目中一般也是这么做的
         */

        System.out.println("自定义登录逻辑");
        // 从mysql查询用户
        User user = getByUsername(username);
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (user != null) {
            List<Permission> permissions =
                    permissionMapper.selectByUserId(user.getId());
            // 设置权限
            // authorities = AuthorityUtils.commaSeparatedStringToAuthorityList("admin,normal,ROLE_abc");
            permissions.forEach(permission -> {
                if (permission != null &&
                        !StringUtils.isEmpty(permission.getEnname())) {
                    GrantedAuthority grantedAuthority = new
                            SimpleGrantedAuthority(permission.getEnname());
                    authorities.add(grantedAuthority);
                }
            });


            // 封装成UserDetails的实现类
//            return org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
//                    .password(user.getPassword()).authorities(authorities).build();
            return new org.springframework.security.core.userdetails.User(
                    user.getUsername(), user.getPassword(), authorities);
        } else {
            throw new UsernameNotFoundException("用户名不存在");
        }
    }
}
