package com.gamerstore.cart_service.dto;

import java.math.BigDecimal;
import java.util.List;

public record CartDTO(
    String userId,
    List<CartItemDTO> items,
    BigDecimal total
) {}
