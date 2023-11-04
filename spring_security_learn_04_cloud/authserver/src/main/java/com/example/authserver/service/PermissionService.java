package com.example.authserver.service;

import com.example.authserver.bean.SysPermission;

import java.util.List;

/**
 * @author Fox
 */
public interface PermissionService  {

    List<SysPermission> selectByUserId(Long userId);
}
