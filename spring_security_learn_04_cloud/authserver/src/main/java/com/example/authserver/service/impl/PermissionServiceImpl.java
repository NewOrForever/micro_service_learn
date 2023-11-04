package com.example.authserver.service.impl;

import com.example.authserver.mapper.PermissionMapper;
import com.example.authserver.service.PermissionService;
import com.example.authserver.bean.SysPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Fox
 */
@Service
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private PermissionMapper permissionMapper;
    @Override
    public List<SysPermission> selectByUserId(Long userId) {

        return permissionMapper.selectByUserId(userId);
    }
}
