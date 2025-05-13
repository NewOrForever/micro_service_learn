package com.example.order.feign.dto;

/**
 * ClassName:AccountDTO
 * Package:com.example.order.feign.dto
 * Description:
 *
 * @Date:2025/3/27 17:11
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
