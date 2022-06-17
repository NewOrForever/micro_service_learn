package com.example.consumer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.consumer.entity.UserEntity;
import com.example.common.utils.PageUtils;

import java.util.Map;

/**
 * ClassName:UserService
 * Package:com.example.consumer.service
 * Description:
 *
 * @Date:2022/6/16 15:19
 * @Author:qs@1.com
 */
public interface UserService extends IService<UserEntity> {
    PageUtils queryPage(Map<String, Object> params);
}
