package com.gamerstore.order_service.models;

import java.math.BigDecimal;

public class OrderItem {

    private Long productId;
    private Integer quantity;
    private String name;      // opcional: nome do produto
    private String imageUrl;  // URL da foto
    private BigDecimal price; // preço unitário (opcional)

    public OrderItem() {}

    public OrderItem(Long productId, Integer quantity, String name, String imageUrl, BigDecimal price) {
        this.productId = productId;
        this.quantity = quantity;
        this.name = name;
        this.imageUrl = imageUrl;
        this.price = price;
    }

    // getters e setters
    public Long getProductId() { return productId; }
    public Integer getQuantity() { return quantity; }
    public String getName() { return name; }
    public String getImageUrl() { return imageUrl; }
    public BigDecimal getPrice() { return price; }

    public void setProductId(Long productId) { this.productId = productId; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public void setName(String name) { this.name = name; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setPrice(BigDecimal price) { this.price = price; }
}