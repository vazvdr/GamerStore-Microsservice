package com.gamerstore.shared.shipping.dto;

import java.util.List;

public record ShippingResponseDTO(
    String cep,
    List<ShippingOptionDTO> opcoes
) {}