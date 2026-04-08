package com.gamerstore.product_service.mapper;

import com.gamerstore.product_service.dto.ProductResponseDTO;
import com.gamerstore.product_service.entity.Product;

public class ProductMapper {

    // 🔹 Entity → DTO
    public static ProductResponseDTO toDTO(Product product) {
        if (product == null) {
            return null;
        }

        return new ProductResponseDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getBrand(),
                product.getModel(),
                product.getImageUrl(),
                product.getTags(),
                product.getPrice(),
                product.getStock(),
                product.getSpecifications()
        );
    }
}
