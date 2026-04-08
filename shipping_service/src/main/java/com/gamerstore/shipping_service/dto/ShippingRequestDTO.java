package com.gamerstore.shipping_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class ShippingRequestDTO {

    @NotBlank(message = "CEP é obrigatório")
    @Pattern(
        regexp = "\\d{8}",
        message = "CEP deve conter 8 dígitos numéricos"
    )
    private String cep;

    public ShippingRequestDTO() {}

    public ShippingRequestDTO(String cep) {
        this.cep = cep;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }
}
