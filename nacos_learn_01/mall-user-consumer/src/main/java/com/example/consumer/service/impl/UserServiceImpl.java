package com.example.consumer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.Query;
import com.example.consumer.dao.UserDao;
import com.example.consumer.entity.UserEntity;
import com.example.consumer.service.UserService;
import com.example.common.utils.PageUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * ClassName:UserServiceImpl
 * Package:com.example.consumer.service.impl
 * Description:
 *
 * @Date:2022/6/16 15:23
 * @Author:qs@1.com
 */
@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserDao, UserEntity> implements UserService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<UserEntity> page = this.page(
                new Query<UserEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }
}
