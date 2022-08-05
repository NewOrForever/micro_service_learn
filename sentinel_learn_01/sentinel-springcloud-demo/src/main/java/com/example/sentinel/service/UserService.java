package com.example.sentinel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.utils.PageUtils;
import com.example.sentinel.entity.UserEntity;

import java.util.Map;

public interface UserService extends IService<UserEntity> {
    PageUtils queryPage(Map<String, Object> params);

    UserEntity getUserById(int id);

}
