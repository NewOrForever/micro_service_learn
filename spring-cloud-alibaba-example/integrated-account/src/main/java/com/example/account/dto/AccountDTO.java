package com.example.account.dto;

/**
 * ClassName:Order
 * Package:com.example.order.entity
 * Description:
 *
 * @Date:2025/3/27 16:45
 * @Author:qs@1.com
 */
public class AccountDTO {

    private String userId;

    private Integer price;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

}