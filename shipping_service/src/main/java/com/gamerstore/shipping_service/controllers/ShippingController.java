package com.gamerstore.shipping_service.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gamerstore.shipping_service.dto.ShippingRequestDTO;
import com.gamerstore.shared.shipping.dto.ShippingResponseDTO;
import com.gamerstore.shipping_service.services.ShippingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/shipping")
@Tag(name = "Shipping", description = "Cálculo de frete")
public class ShippingController {

    private final ShippingService shippingService;

    public ShippingController(ShippingService shippingService) {
        this.shippingService = shippingService;
    }

    @Operation(
        summary = "Calcular frete",
        description = "Calcula as opções de frete com base no CEP informado"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Frete calculado com sucesso",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ShippingResponseDTO.class)
        )
    )
    @ApiResponse(
        responseCode = "400",
        description = "CEP inválido"
    )
    @PostMapping("/calcular")
    public ResponseEntity<ShippingResponseDTO> calcularFrete(
        @Valid @RequestBody ShippingRequestDTO request
    ) {
        ShippingResponseDTO response =
            shippingService.calcularFrete(request.getCep());

        return ResponseEntity.ok(response);
    }
}