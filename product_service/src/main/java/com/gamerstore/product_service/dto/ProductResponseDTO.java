package com.gamerstore.product_service.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.JsonNode;

public record ProductResponseDTO(
        Long id,
        String name,
        String description,
        String brand,
        String model,
        String imageUrl,
        String tags,
        BigDecimal price,
        Integer stock,
        JsonNode specifications
) {}
