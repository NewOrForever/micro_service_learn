package com.example.order.mapper;

/**
 * ClassName:OrderMapper
 * Package:com.example.order.mapper
 * Description:
 *
 * @Date:2025/3/27 17:06
 * @Author:qs@1.com
 */
import com.example.order.entity.Order;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

/**
 * @author TrevorLink
 */
@Mapper
public interface OrderMapper {

    @Insert("INSERT INTO `order` (user_id, commodity_code,money,create_time,update_time) VALUES (#{userId}, #{commodityCode},#{money},#{createTime},#{updateTime})")
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    int saveOrder(Order order);

}