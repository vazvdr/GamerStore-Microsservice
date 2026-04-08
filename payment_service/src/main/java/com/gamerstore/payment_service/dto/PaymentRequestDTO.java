package com.gamerstore.payment_service.dto;

import java.math.BigDecimal;
import java.util.List;

import com.gamerstore.shared.messaging.dto.ItemDTO;

public class PaymentRequestDTO {

    private BigDecimal amount;
    private String paymentMethodId;
    private String stripeCustomerId;
    private Long userId;
    private String userEmail;
    private String userName;

    // ✅ itens do carrinho
    private List<ItemDTO> items;

    public PaymentRequestDTO() {
    }

    public PaymentRequestDTO(
            BigDecimal amount,
            String paymentMethodId,
            String stripeCustomerId,
            Long userId,
            String userEmail,   
            String userName,   
            List<ItemDTO> items
    ) {
        this.amount = amount;
        this.paymentMethodId = paymentMethodId;
        this.stripeCustomerId = stripeCustomerId;
        this.userId = userId;
        this.userEmail = userEmail;
        this.userName = userName;
        this.items = items;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getPaymentMethodId() {
        return paymentMethodId;
    }

    public void setPaymentMethodId(String paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
    }

    public String getStripeCustomerId() {
        return stripeCustomerId;
    }

    public void setStripeCustomerId(String stripeCustomerId) {
        this.stripeCustomerId = stripeCustomerId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    // ✅ items
    public List<ItemDTO> getItems() {
        return items;
    }

    public void setItems(List<ItemDTO> items) {
        this.items = items;
    }
}