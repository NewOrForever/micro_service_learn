package com.example.storage.mapper;

import com.example.storage.dto.StorageDTO;
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
public interface StorageMapper {

    @Select("SELECT `count` FROM storage WHERE commodity_code = #{commodityCode}")
    Integer getStock(@Param("commodityCode") String commodityCode);

    @Update("UPDATE storage SET count = count - #{count},update_time=#{updateTime} WHERE commodity_code = #{commodityCode}")
    int reduceStock(@Param("commodityCode") String commodityCode,
                    @Param("count") Integer count, @Param("updateTime") Timestamp updateTime);
}