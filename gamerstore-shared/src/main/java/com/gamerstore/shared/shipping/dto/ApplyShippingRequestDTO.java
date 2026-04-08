package com.gamerstore.shared.shipping.dto;

import jakarta.validation.constraints.NotBlank;

public record ApplyShippingRequestDTO(
    @NotBlank
    String cep,

    @NotBlank
    String tipoFrete
) {}