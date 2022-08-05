package com.example.sentinel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.sentinel.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {
}
