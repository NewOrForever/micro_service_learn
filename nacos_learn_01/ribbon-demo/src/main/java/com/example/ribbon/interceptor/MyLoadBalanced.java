package com.example.ribbon.interceptor;

import org.springframework.beans.factory.annotation.Qualifier;

import java.lang.annotation.*;

/**
 * ClassName:MyLoadBalanced
 * Package:com.example.ribbon.interceptor
 * Description:
 *
 * @Date:2022/6/17 13:26
 * @Author:qs@1.com
 */
@Target({ ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Qualifier
public @interface MyLoadBalanced {
}
