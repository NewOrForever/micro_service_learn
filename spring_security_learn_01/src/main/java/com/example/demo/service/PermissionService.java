package com.example.demo.service;




import com.example.demo.domain.Permission;

import java.util.List;

/**
 * @author Fox
 */
public interface PermissionService  {

    List<Permission> selectByUserId(Long userId);
}
