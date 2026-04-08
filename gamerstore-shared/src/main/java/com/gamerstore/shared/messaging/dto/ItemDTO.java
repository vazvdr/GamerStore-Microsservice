package com.gamerstore.shared.messaging.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public class ItemDTO implements Serializable {

    private Long productId;
    private String name;
    private String imageUrl;
    private BigDecimal price;
    private Integer quantity;

    public ItemDTO() {
    }

    public ItemDTO(Long productId, String name, String imageUrl, BigDecimal price, Integer quantity) {
        this.productId = productId;
        this.name = name;
        this.imageUrl = imageUrl;
        this.price = price;
        this.quantity = quantity;
    }

    public Long getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}