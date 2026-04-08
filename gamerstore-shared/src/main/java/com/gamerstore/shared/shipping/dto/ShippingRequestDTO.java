package com.gamerstore.shared.shipping.dto;

import jakarta.validation.constraints.NotBlank;

public record ShippingRequestDTO(
    @NotBlank(message = "CEP é obrigatório")
    String cep
) {}
