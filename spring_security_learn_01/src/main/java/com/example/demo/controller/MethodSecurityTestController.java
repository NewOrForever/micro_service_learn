package com.example.demo.controller;

import com.example.demo.domain.User;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.annotation.SecuredAnnotationSecurityMetadataSource;
import org.springframework.security.access.intercept.aopalliance.MethodSecurityInterceptor;
import org.springframework.security.access.intercept.aopalliance.MethodSecurityMetadataSourceAdvisor;
import org.springframework.security.access.method.AbstractFallbackMethodSecurityMetadataSource;
import org.springframework.security.access.prepost.*;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.core.parameters.AnnotationParameterNameDiscoverer;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import java.util.ArrayList;
import java.util.List;

/**
 * ClassName:MethodSecurityTestController
 * Package:com.example.demo.controller
 * Description:  用于测试方法授权 {@link org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity}
 *
 * @Date:2023/10/16 10:51
 * @Author:qs@1.com
 */
@RestController
public class MethodSecurityTestController {
    /**
     * 方法级别的授权
     * @see EnableGlobalMethodSecurity
     * @see org.springframework.security.config.annotation.method.configuration.GlobalMethodSecuritySelector
     * {@link org.springframework.security.config.annotation.method.configuration.MethodSecurityMetadataSourceAdvisorRegistrar}
     * {@link GlobalMethodSecurityConfiguration}
     * @see MethodSecurityMetadataSourceAdvisor
     * @see MethodSecurityInterceptor
     * @see PrePostAnnotationSecurityMetadataSource
     * @see AbstractFallbackMethodSecurityMetadataSource
     * 大致aop 流程：
     * MethodSecurityMetadataSourceAdvisor -> pointcut 匹配 -> MethodSecurityInterceptor
     * -> MethodSecurityMetadataSource -> AccessDecisionManager
     */


    /**
     * jsr250Enabled = true
     *
     * @see org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration#jsr250Enabled()
     * @see org.springframework.security.config.annotation.method.configuration.Jsr250MetadataSourceConfiguration
     * @see org.springframework.security.access.annotation.Jsr250MethodSecurityMetadataSource
     */
    @RequestMapping("/rolesAllowed")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_USER"})
    public String rolesAllowed() {
        return "rolesAllowed";
    }

    @RequestMapping("/permitAll")
    @PermitAll
    public String permitAll() {
        return "permitAll";
    }

    @RequestMapping("/denyAll")
    @DenyAll
    public String denyAll() {
        return "denyAll";
    }

    /**
     * securedEnabled = true
     *
     * @see org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration#securedEnabled()
     * @see SecuredAnnotationSecurityMetadataSource
     */
    @Secured("ROLE_ADMIN")
    @RequestMapping("/secured")
    public String secured() {
        return "secured";
    }

    /**
     * prePostEnabled = true
     * 支持表达式的注解
     * @see org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration#prePostEnabled()
     * @see org.springframework.security.access.prepost.PrePostAnnotationSecurityMetadataSource
     *
     * 还能使用SpEL表达式，注意看方法参数的使用 - #参数名
     * @see AnnotationParameterNameDiscoverer
     */
    /**
     * @PreAuthorize 通常情况下，我们都是使用这个注解
     */
    @RequestMapping("/preAuthorize")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String preAuthorize() {
        return "preAuthorize";
    }

    //限制只能查询Id小于10的用户
    @PreAuthorize("#id < 10")
    @RequestMapping("/findById")
    public User findById(long id) {
        User user = new User();
        user.setId(id);
        return user;
    }

    // 限制只能查询自己的信息
    @PreAuthorize("principal.username.equals(#username)")
    @RequestMapping("/findByName")
    public User findByName(String username) {
        User user = new User();
        user.setUsername(username);
        return user;
    }

    //限制只能新增用户名称为abc的用户
    @PreAuthorize("#user.username.equals('abc')")
    @RequestMapping("/add")
    public User add(User user) {
        return user;
    }

    /**
     * @PostAuthorize 可以在方法调用完之后进行权限检查
     */
    @PostAuthorize("returnObject.id%2 == 0")
    @RequestMapping("/find")
    public User find(long id) {
        User user = new User();
        user.setId(id);
        return user;
    }

    /**
     * 使用@PreFilter和@PostFilter进行过滤
     * 使用@PreFilter和@PostFilter可以对集合类型的参数或返回值进行过滤
     * 使用@PreFilter和@PostFilter时，Spring Security将移除使对应表达式的结果为false的元素
     */
    @PostFilter("filterObject.id%2 == 0")
    @RequestMapping("/findAll")
    public List<User> findAll() {
        List<User> userList = new ArrayList<User>();
        User user;
        for (long i = 0; i < 10; i++) {

            user = new User();
            user.setId(i);
            userList.add(user);
        }
        return userList;
    }

    @PreFilter(filterTarget = "ids", value = "filterObject%2 == 0")
    @RequestMapping("/delete")
    public void delete(@RequestParam("ids") List<Long> ids,
                       @RequestParam(value = "usernames", required = false) List<String> usernames) {
        ids.forEach(System.out::println);
    }


}
