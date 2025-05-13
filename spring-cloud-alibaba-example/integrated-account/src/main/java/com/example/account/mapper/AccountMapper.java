package com.example.account.mapper;

import org.apache.ibatis.annotations.*;

import java.sql.Timestamp;

/**
 * ClassName:OrderMapper
 * Package:com.example.order.mapper
 * Description:
 *
 * @Date:2025/3/27 17:06
 * @Author:qs@1.com
 */
@Mapper
public interface AccountMapper {

    @Select("SELECT money FROM account WHERE user_id = #{userId}")
    Integer getBalance(@Param("userId") String userId);

    @Update("UPDATE account SET money = money - #{price},update_time = #{updateTime} WHERE user_id = #{userId} AND money >= ${price}")
    int reduceBalance(@Param("userId") String userId, @Param("price") Integer price,
                      @Param("updateTime") Timestamp updateTime);
}