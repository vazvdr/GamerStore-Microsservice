package com.gamerstore.product_service.dto;

import java.util.List;

public record ProductPageResponseDTO(

        List<ProductResponseDTO> products,

        int currentPage,

        int totalPages,

        long totalElements

) {
}