package com.example.order.entity;

import java.sql.Timestamp;
/**
 * ClassName:Order
 * Package:com.example.order.entity
 * Description:
 *
 * @Date:2025/3/27 16:45
 * @Author:qs@1.com
 */
public class Order {

    private Integer id;

    private String userId;

    private String commodityCode;

    private Integer count;

    private Integer money;

    private Timestamp createTime;

    private Timestamp updateTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

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

    public Integer getMoney() {
        return money;
    }

    public void setMoney(Integer money) {
        this.money = money;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "Order{" + "id=" + id + ", userId='" + userId + '\'' + ", commodityCode='"
                + commodityCode + '\'' + ", count=" + count + ", money=" + money
                + ", createTime=" + createTime + ", updateTime=" + updateTime + '}';
    }

}