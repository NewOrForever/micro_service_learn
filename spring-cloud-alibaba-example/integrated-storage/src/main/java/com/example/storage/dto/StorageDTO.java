package com.example.storage.dto;

import java.sql.Timestamp;

/**
 * ClassName:Order
 * Package:com.example.order.entity
 * Description:
 *
 * @Date:2025/3/27 16:45
 * @Author:qs@1.com
 */
public class StorageDTO {

    private String commodityCode;

    private Integer count;

    public String getCommodityCode() {
        return commodityCode;
    }

    public void setCommodityCode(String commodityCode) {
        this.commodityCode = commodityCode;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

}