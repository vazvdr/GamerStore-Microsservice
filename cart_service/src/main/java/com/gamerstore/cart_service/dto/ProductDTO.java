package com.gamerstore.cart_service.dto;

import java.math.BigDecimal;

public record ProductDTO(

        Long id,
        String nome,
        String descricao,
        BigDecimal preco,
        String imageUrl

) {}