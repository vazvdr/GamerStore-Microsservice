package com.gamerstore.cart_service.dto;

import java.math.BigDecimal;
import java.util.List;

public record CartResponseDTO(
    List<CartItemDTO> items,
    BigDecimal subTotal,
    BigDecimal shippingValue,
    BigDecimal total
) {}
