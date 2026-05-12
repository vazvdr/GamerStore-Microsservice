package com.gamerstore.suggestion_service.clients;

import com.gamerstore.shared.suggestion.dto.SuggestionProductDTO;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "product-service",
        url = "${product.service.url}"
)
public interface ProductClient {

    @GetMapping("/products/{id}")
    SuggestionProductDTO findById(
            @PathVariable Long id
    );
}