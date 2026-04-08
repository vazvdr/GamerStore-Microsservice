package com.gamerstore.shared.messaging.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class PaymentConfirmedEvent implements Serializable {

    private Long userId;
    private String paymentIntentId;

    private String userEmail; 
    private String userName;

    private BigDecimal amount;
    private BigDecimal subtotal;
    private BigDecimal shipping;

    private List<ItemDTO> items;

    public PaymentConfirmedEvent() {
    }

    public PaymentConfirmedEvent(
            Long userId,
            String paymentIntentId,
            String userEmail,
            String userName,
            BigDecimal amount,
            BigDecimal subtotal,
            BigDecimal shipping,
            List<ItemDTO> items) {
        this.userId = userId;
        this.paymentIntentId = paymentIntentId;
        this.userEmail = userEmail;
        this.userName = userName;
        this.amount = amount;
        this.subtotal = subtotal;
        this.shipping = shipping;
        this.items = items;
    }

    // ================= GETTERS =================
    public Long getUserId() {
        return userId;
    }

    public String getPaymentIntentId() {
        return paymentIntentId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public BigDecimal getShipping() {
        return shipping;
    }

    public List<ItemDTO> getItems() {
        return items;
    }

    // ================= SETTERS =================
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setPaymentIntentId(String paymentIntentId) {
        this.paymentIntentId = paymentIntentId;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public void setShipping(BigDecimal shipping) {
        this.shipping = shipping;
    }

    public void setItems(List<ItemDTO> items) {
        this.items = items;
    }
}