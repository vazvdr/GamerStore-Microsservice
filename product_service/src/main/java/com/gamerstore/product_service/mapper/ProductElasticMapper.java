package com.gamerstore.product_service.mapper;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.gamerstore.product_service.dto.ProductResponseDTO;
import com.gamerstore.product_service.entity.Product;
import com.gamerstore.product_service.entity.elastic.ProductDocument;

@Component
public class ProductElasticMapper {

    // JPA → Elastic
    public static ProductDocument toDocument(Product product) {
        return new ProductDocument(
                product.getId(),
                product.getName(),
                product.getBrand(),
                product.getModel(),
                product.getDescription(),
                product.getTags(),
                product.getPrice().doubleValue());
    }

    // Elastic → DTO
    public ProductResponseDTO mapToDTO(ProductDocument doc) {
        return new ProductResponseDTO(
                doc.getId(),
                doc.getName(),
                doc.getDescription(),
                doc.getBrand(),
                doc.getModel(),
                null, 
                doc.getTags(),
                doc.getPrice() != null ? BigDecimal.valueOf(doc.getPrice()) : null,
                null, 
                null
        );
    }
}