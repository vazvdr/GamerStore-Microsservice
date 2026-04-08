package com.gamerstore.cart_service.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("cart")
public class Cart {

    @Id
    private String userId;

    private List<CartItem> items = new ArrayList<>();

    private BigDecimal subtotal;
    private BigDecimal shippingValue;
    private String shippingType;
    private BigDecimal total;

    public Cart() {
    }

    public Cart(String userId, List<CartItem> items,
            BigDecimal subtotal,
            BigDecimal shippingValue,
            String shippingType,
            BigDecimal total) {
        this.userId = userId;
        this.items = items;
        this.subtotal = subtotal;
        this.shippingValue = shippingValue;
        this.shippingType = shippingType;
        this.total = total;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getShippingValue() {
        return shippingValue;
    }

    public void setShippingValue(BigDecimal shippingValue) {
        this.shippingValue = shippingValue;
    }

    public String getShippingType() {
        return shippingType;
    }

    public void setShippingType(String shippingType) {
        this.shippingType = shippingType;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}