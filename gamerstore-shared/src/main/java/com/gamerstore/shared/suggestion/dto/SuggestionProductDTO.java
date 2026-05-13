package com.gamerstore.shared.suggestion.dto;

import java.math.BigDecimal;

public record SuggestionProductDTO(

        Long id,

        String name,

        String description,

        String brand,

        String model,

        String imageUrl,

        String tags,

        BigDecimal price,

        Integer stock

) {
}