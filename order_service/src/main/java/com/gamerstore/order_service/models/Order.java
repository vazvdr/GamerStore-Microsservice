package com.gamerstore.order_service.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Document(collection = "orders")
public class Order {

    @Id
    private String id;
    private Long userId;
    private String paymentIntentId;
    private BigDecimal total;
    private List<OrderItem> items;
    private Instant createdAt;
    private BigDecimal subtotal;
    private BigDecimal shipping;

    public Order() {
    }

    public Order(Long userId,
            String paymentIntentId,
            BigDecimal total,
            BigDecimal subtotal,
            BigDecimal shipping,
            List<OrderItem> items) {

        this.userId = userId;
        this.paymentIntentId = paymentIntentId;
        this.total = total;
        this.subtotal = subtotal;
        this.shipping = shipping;
        this.items = items;
        this.createdAt = Instant.now();
    }

    public String getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getPaymentIntentId() {
        return paymentIntentId;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setPaymentIntentId(String paymentIntentId) {
        this.paymentIntentId = paymentIntentId;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public BigDecimal getShipping() {
        return shipping;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public void setShipping(BigDecimal shipping) {
        this.shipping = shipping;
    }
}