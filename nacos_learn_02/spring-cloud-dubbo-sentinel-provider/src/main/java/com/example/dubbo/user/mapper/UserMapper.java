package com.example.dubbo.user.mapper;

import com.example.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * ClassName:UserMapper
 * Package:com.example.dubbo.user.mapper
 * Description:
 *
 * @Date:2022/6/21 13:25
 * @Author:qs@1.com
 */
@Mapper
public interface UserMapper {
    @Select("select * from t_user")
    List<User> list();

    @Select("select * from t_user where id = #{id}")
    User getById(Integer id);
}
