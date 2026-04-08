package com.gamerstore.shared.shipping.dto;

import java.math.BigDecimal;

public record ShippingOptionDTO(
    String tipo,
    Integer prazoDias,
    BigDecimal valor
) {}