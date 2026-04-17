package com.gamerstore.product_service.entity.elastic;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "products")
public class ProductDocument {

    @Id
    private Long id;

    private String name;
    private String brand;
    private String model;
    private String description;
    private String tags;
    private Double price;

    public ProductDocument() {
    }

    public ProductDocument(Long id, String name, String brand, String model,
                           String description, String tags, Double price) {
        this.id = id;
        this.name = name;
        this.brand = brand;
        this.model = model;
        this.description = description;
        this.tags = tags;
        this.price = price;
    }

    // ========================
    // GETTERS E SETTERS
    // ========================

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}