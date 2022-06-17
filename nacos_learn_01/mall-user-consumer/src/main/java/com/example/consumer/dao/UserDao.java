package com.example.consumer.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.consumer.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * ClassName:UserDao
 * Package:com.example.consumer.dao
 * Description:
 *
 * @Date:2022/6/16 15:24
 * @Author:qs@1.com
 */
@Mapper
public interface UserDao extends BaseMapper<UserEntity> {
}
