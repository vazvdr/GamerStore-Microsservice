package com.gamerstore.product_service.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.fasterxml.jackson.databind.JsonNode;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    private String model;

    @Column(length = 500)
    private String imageUrl;

    private String tags;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer stock;

    @Column(nullable = false)
    private Integer reservedStock = 0;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private JsonNode specifications;

    public Product() {
    }

    public Product(
            Long id,
            String name,
            String description,
            String brand,
            String model,
            String imageUrl,
            String tags,
            BigDecimal price,
            Integer stock,
            Integer reserved_stock,
            JsonNode specifications) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.brand = brand;
        this.model = model;
        this.imageUrl = imageUrl;
        this.tags = tags;
        this.price = price;
        this.stock = stock;
        this.reservedStock = reserved_stock;
        this.specifications = specifications;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Integer getReservedStock(){
        return reservedStock;
    }

    public void setReservedStock(Integer reservedStock){
        this.reservedStock = reservedStock;
    }

    public JsonNode getSpecifications() {
        return specifications;
    }

    public void setSpecifications(JsonNode specifications) {
        this.specifications = specifications;
    }
}
