package com.example.feign.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

/**
 * ClassName:OrderEntity
 * Package:com.example.provider.entity
 * Description:
 *
 * @Date:2022/6/15 20:57
 * @Author:qs@1.com
 */
@TableName("t_order")
public class OrderEntity implements Serializable {

    private static final long serialVersionUID = 4992143409136953657L;

    @TableId
    private Integer id;
    /**
     *
     */
    private String userId;
    /**
     * 商品编号
     */
    private String commodityCode;
    /**
     *
     */
    private Integer count;
    /**
     *
     */
    private Integer amount;



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

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }
}
